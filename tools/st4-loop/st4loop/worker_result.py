"""Parse the Claude Code `--output-format json --json-schema ...` envelope.

Documented envelope (Claude Code v2.x, `claude -p`):

    {
      "type": "result",
      "subtype": "success",
      "is_error": false,
      "result": "<final assistant text>",
      "structured_output": { ... validated against --json-schema ... },
      "session_id": "...",
      "total_cost_usd": 0.0,
      "usage": { ... }
    }

Real output has quirks across versions (structured_output may be absent, or the
structured object may live in `result` as a dict or a JSON string). We handle all
of these and, when in doubt, fail CLOSED (treat as a failed attempt) — the loop
never trusts self-reported success.
"""

import json
import re

VALID_OUTCOMES = ("success", "failed", "blocked")
REQUIRED_FIELDS = ("itemId", "outcome", "summary", "filesChanged", "debtDelta")


class WorkerResultError(Exception):
    """The worker produced no usable envelope at all (not even parseable JSON)."""


class WorkerOutcome:
    """Normalized view of one worker run."""

    def __init__(
        self,
        ok,
        item_id=None,
        outcome="failed",
        summary="",
        files_changed=None,
        debt_delta=None,
        status_advance=None,
        evidence=None,
        blocker=None,
        problems=None,
        is_error=False,
        session_id=None,
        cost_usd=None,
        raw=None,
    ):
        self.ok = ok
        self.item_id = item_id
        self.outcome = outcome
        self.summary = summary
        self.files_changed = files_changed or []
        self.debt_delta = debt_delta or {}
        self.status_advance = status_advance
        self.evidence = evidence or []
        self.blocker = blocker
        self.problems = problems or []
        self.is_error = is_error
        self.session_id = session_id
        self.cost_usd = cost_usd
        self.raw = raw

    def __repr__(self):  # pragma: no cover - debug aid
        return (
            f"WorkerOutcome(ok={self.ok}, item_id={self.item_id!r}, "
            f"outcome={self.outcome!r}, problems={self.problems})"
        )


def parse_envelope(raw_text):
    """Parse the top-level JSON envelope. Raises WorkerResultError if unparseable."""
    if raw_text is None or not str(raw_text).strip():
        raise WorkerResultError("empty worker output")
    try:
        envelope = json.loads(raw_text)
    except (ValueError, TypeError) as exc:
        raise WorkerResultError(f"worker output is not JSON: {exc}") from exc
    if not isinstance(envelope, dict):
        raise WorkerResultError("worker output is not a JSON object")
    return envelope


_FENCED_BLOCK = re.compile(r"```(?:json|JSON)?[ \t]*\r?\n?(.*?)```", re.DOTALL)


def _balanced_json_objects(text):
    """Non-overlapping top-level JSON objects decoded from free text.

    Uses JSONDecoder.raw_decode at each '{', skipping to the end of each successful
    decode so nested objects are not double-counted. Objects that fail to parse are
    skipped (prose with stray braces yields nothing).
    """
    decoder = json.JSONDecoder()
    objs = []
    i, n = 0, len(text)
    while i < n:
        if text[i] == "{":
            try:
                obj, end = decoder.raw_decode(text, i)
            except ValueError:
                i += 1
                continue
            if isinstance(obj, dict):
                objs.append(obj)
            i = end
        else:
            i += 1
    return objs


def recover_structured_from_text(text):
    """Fail-closed recovery of ONE structured object from a free-text `result`.

    Some models ignore --json-schema and return a fenced ```json object (plus
    explanatory prose) in `result` instead of `structured_output`. We recover exactly
    one object and otherwise fail closed:
      1. the whole string is a JSON object;
      2. exactly one fenced ```json object (multiple fenced objects => ambiguous);
      3. exactly one balanced top-level object in the prose (multiple => ambiguous).
    Returns dict or None. The caller still runs the normal schema validation.
    """
    if not isinstance(text, str):
        return None
    stripped = text.strip()
    if stripped.startswith("{"):
        try:
            obj = json.loads(stripped)
        except ValueError:
            obj = None
        if isinstance(obj, dict):
            return obj
    fenced = []
    for chunk in _FENCED_BLOCK.findall(text):
        try:
            obj = json.loads(chunk.strip())
        except ValueError:
            continue
        if isinstance(obj, dict):
            fenced.append(obj)
    if fenced:
        return fenced[0] if len(fenced) == 1 else None  # ambiguous -> fail closed
    objs = _balanced_json_objects(text)
    return objs[0] if len(objs) == 1 else None  # 0 or >1 -> fail closed


def extract_structured(envelope):
    """Best-effort extraction of the structured result object from the envelope.

    Tries, in order: envelope['structured_output']; envelope['result'] when it is a
    dict; then fail-closed recovery of one JSON object from the result text (whole
    string, a single fenced ```json block, or a single balanced object). Returns dict
    or None.
    """
    so = envelope.get("structured_output")
    if isinstance(so, dict):
        return so
    result = envelope.get("result")
    if isinstance(result, dict):
        return result
    if isinstance(result, str):
        return recover_structured_from_text(result)
    return None


def validate_structured(so, expected_item_id=None):
    """Return a list of problems (empty list == valid)."""
    problems = []
    if not isinstance(so, dict):
        return ["structured result is not an object"]
    for field in REQUIRED_FIELDS:
        if field not in so:
            problems.append(f"missing required field: {field}")
    if "outcome" in so and so["outcome"] not in VALID_OUTCOMES:
        problems.append(f"invalid outcome: {so['outcome']!r}")
    if "filesChanged" in so and not isinstance(so["filesChanged"], list):
        problems.append("filesChanged must be an array")
    if "debtDelta" in so and not isinstance(so["debtDelta"], dict):
        problems.append("debtDelta must be an object")
    if expected_item_id is not None and so.get("itemId") != expected_item_id:
        problems.append(
            f"itemId mismatch: worker reported {so.get('itemId')!r}, "
            f"expected {expected_item_id!r}"
        )
    if so.get("outcome") == "blocked" and not so.get("blocker"):
        problems.append("outcome=blocked requires a blocker description")
    db = (so.get("debtDelta") or {}).get("directBackends")
    if isinstance(db, int) and db > 0:
        problems.append(f"debtDelta.directBackends must be <= 0, got {db}")
    return problems


def interpret(raw_text, expected_item_id=None):
    """Turn raw worker stdout into a normalized WorkerOutcome (fail closed)."""
    try:
        envelope = parse_envelope(raw_text)
    except WorkerResultError as exc:
        return WorkerOutcome(
            ok=False, outcome="failed", summary=str(exc),
            problems=[str(exc)], evidence=[f"envelope-parse: {exc}"],
            raw=raw_text,
        )

    is_error = bool(envelope.get("is_error"))
    session_id = envelope.get("session_id")
    cost_usd = envelope.get("total_cost_usd")
    so = extract_structured(envelope)

    if so is None:
        msg = "no structured_output in worker envelope"
        return WorkerOutcome(
            ok=False, outcome="failed", summary=str(envelope.get("result") or msg),
            problems=[msg], is_error=is_error, session_id=session_id,
            cost_usd=cost_usd, evidence=[f"envelope: {msg}"], raw=raw_text,
        )

    problems = validate_structured(so, expected_item_id)
    outcome = so.get("outcome", "failed")
    ok = (outcome == "success") and not is_error and not problems
    return WorkerOutcome(
        ok=ok,
        item_id=so.get("itemId"),
        outcome=outcome if outcome in VALID_OUTCOMES else "failed",
        summary=so.get("summary", ""),
        files_changed=so.get("filesChanged") or [],
        debt_delta=so.get("debtDelta") or {},
        status_advance=so.get("statusAdvance"),
        evidence=so.get("evidence") or [],
        blocker=so.get("blocker"),
        problems=problems,
        is_error=is_error,
        session_id=session_id,
        cost_usd=cost_usd,
        raw=raw_text,
    )
