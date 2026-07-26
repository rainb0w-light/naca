"""Orchestration for the external ST4 migration loop.

One iteration = select one READY ledger item -> launch one fresh worker for exactly
that item -> independently verify (deterministic gates + read-only reviewer) ->
commit explicitly-selected files on success, or retry then mark a reproducible
blocker and continue. The worker/verify/reviewer commands are all injectable so the
loop is testable without invoking the real `claude` CLI.
"""

import json
import posixpath
import subprocess
import time
from dataclasses import dataclass, field
from pathlib import Path

from . import debt, gitutil, ledger, worker_result

ITEM_BEGIN = "<<<ST4_ITEM_BEGIN>>>"
ITEM_END = "<<<ST4_ITEM_END>>>"
DIFF_BEGIN = "<<<ST4_DIFF_BEGIN>>>"
DIFF_END = "<<<ST4_DIFF_END>>>"

# Defense in depth: even though the worker prompt forbids git mutations, we also
# deny the obvious history/remote-mutating commands at the CLI layer. Kept as a LIST
# of individual rules: --disallowedTools is variadic, so each rule is its own argv
# element (a single space-joined string is ambiguous and mis-parses).
DEFAULT_DISALLOWED_TOOLS = [
    "Bash(git push:*)",
    "Bash(git merge:*)",
    "Bash(git rebase:*)",
    "Bash(git commit:*)",
    "Bash(git reset:*)",
    "Bash(git clean:*)",
    "Bash(git checkout:*)",
    "Bash(git stash:*)",
]

# Worker permission mode. Default acceptEdits: file edits are auto-approved but all
# OTHER permission checks stay active and do NOT depend on the external auto-mode
# classifier (which can be unavailable and stall the worker indefinitely). bypass and
# dontAsk are deliberately NOT allowed; the loop never disables permission checks.
WORKER_PERMISSION_MODES = ("acceptEdits", "auto", "plan", "manual")
DEFAULT_WORKER_PERMISSION_MODE = "acceptEdits"


@dataclass
class Config:
    repo_root: Path
    ledger_path: Path
    worker_prompt_path: Path
    result_schema_path: Path
    review_schema_path: Path
    log_dir: Path
    claude_cmd: list = field(default_factory=lambda: ["claude"])
    verify_cmd: list = None            # default: <repo>/tools/st4-loop/verify-task.sh
    reviewer_cmd: list = None          # default: build a claude reviewer invocation
    model: str = None
    effort: str = None
    max_iterations: int = 1
    max_attempts: int = 3
    worker_timeout: int = 1800
    permission_mode: str = DEFAULT_WORKER_PERMISSION_MODE
    dry_run: bool = False
    allow_commit: bool = True
    disallowed_tools: list = field(default_factory=lambda: list(DEFAULT_DISALLOWED_TOOLS))

    def __post_init__(self):
        self.repo_root = Path(self.repo_root)
        self.ledger_path = Path(self.ledger_path)
        self.log_dir = Path(self.log_dir)
        if self.verify_cmd is None:
            self.verify_cmd = [
                str(self.repo_root / "tools" / "st4-loop" / "verify-task.sh")
            ]
        if self.permission_mode not in WORKER_PERMISSION_MODES:
            raise LoopError(
                f"unsafe worker permission mode refused: {self.permission_mode!r} "
                f"(allowed: {', '.join(WORKER_PERMISSION_MODES)}; the loop never uses "
                f"bypassPermissions/dontAsk/dangerously-skip-permissions)"
            )


class LoopError(Exception):
    pass


# --------------------------------------------------------------------------- #
# Default runners (shell out). Tests inject their own callables instead.
# --------------------------------------------------------------------------- #
def default_worker_runner(prompt, cfg):
    """Invoke the worker CLI and return its raw stdout (the JSON envelope).

    The prompt is sent via STDIN, never as a trailing positional: --disallowedTools
    (and --tools) are VARIADIC and would otherwise swallow a following positional
    prompt ("Input must be provided either through stdin or as a prompt argument").
    The deny rules are passed as separate argv elements and placed LAST so the
    variadic flag stops cleanly at end-of-argv.
    """
    if cfg.permission_mode not in WORKER_PERMISSION_MODES:
        raise LoopError(
            f"unsafe worker permission mode refused: {cfg.permission_mode!r}")
    schema = Path(cfg.result_schema_path).read_text(encoding="utf-8")
    argv = list(cfg.claude_cmd) + [
        "-p",
        "--output-format", "json",
        "--json-schema", schema,
        "--permission-mode", cfg.permission_mode,
        "--no-session-persistence",
    ]
    if cfg.model:
        argv += ["--model", cfg.model]
    if cfg.effort:
        argv += ["--effort", cfg.effort]
    # Variadic flag last; each rule its own element; prompt delivered via stdin.
    argv += ["--disallowedTools", *cfg.disallowed_tools]
    try:
        result = subprocess.run(
            argv, cwd=str(cfg.repo_root), input=prompt,
            capture_output=True, text=True, timeout=cfg.worker_timeout,
        )
    except subprocess.TimeoutExpired as exc:
        raise worker_result.WorkerResultError(
            f"worker timed out after {cfg.worker_timeout}s"
        ) from exc
    # Non-zero exit is not fatal by itself: the envelope carries is_error/outcome.
    return result.stdout or result.stderr


def default_verify_runner(item, cfg):
    """Run verify-task.sh for the item. Returns (ok, report_text)."""
    env = {
        "ST4_LEDGER": str(cfg.ledger_path),
        "ST4_ITEM_ID": item["id"],
    }
    result = subprocess.run(
        cfg.verify_cmd + [item["id"]],
        cwd=str(cfg.repo_root), capture_output=True, text=True, env=_merged_env(env),
    )
    report = (result.stdout or "") + (result.stderr or "")
    return result.returncode == 0, report


def default_reviewer_runner(diff_text, item, cfg):
    """Invoke the read-only st4-reviewer and parse its structured verdict."""
    schema = Path(cfg.review_schema_path).read_text(encoding="utf-8")
    prompt = (
        "Review this single ST4 migration slice diff against its ledger item.\n"
        f"{ITEM_BEGIN}\n{json.dumps(item, indent=2)}\n{ITEM_END}\n"
        f"{DIFF_BEGIN}\n{diff_text}\n{DIFF_END}\n"
        "Return only the structured verdict."
    )
    base = cfg.reviewer_cmd if cfg.reviewer_cmd is not None else cfg.claude_cmd
    # --tools is variadic: give it a single value and place it LAST; the review
    # prompt goes via stdin so the variadic flag cannot swallow it.
    argv = list(base) + [
        "-p",
        "--agent", "st4-reviewer",
        "--permission-mode", "plan",
        "--output-format", "json",
        "--json-schema", schema,
        "--tools", "Read,Grep,Glob",
    ]
    result = subprocess.run(
        argv, cwd=str(cfg.repo_root), input=prompt,
        capture_output=True, text=True, timeout=cfg.worker_timeout,
    )
    envelope = worker_result.parse_envelope(result.stdout)
    verdict = worker_result.extract_structured(envelope)
    return verdict or {"approved": False, "issues": ["reviewer returned no verdict"]}


def _merged_env(extra):
    import os
    env = dict(os.environ)
    env.update(extra)
    return env


# --------------------------------------------------------------------------- #
# Controller
# --------------------------------------------------------------------------- #
class Controller:
    def __init__(
        self,
        cfg,
        worker_runner=None,
        verify_runner=None,
        reviewer_runner=None,
        debt_measurer=None,
        log=print,
    ):
        self.cfg = cfg
        self.worker_runner = worker_runner or default_worker_runner
        self.verify_runner = verify_runner or default_verify_runner
        self.reviewer_runner = reviewer_runner or default_reviewer_runner
        self.debt_measurer = debt_measurer or debt.measure_direct_backends
        self.log = log

    # -- path safety -------------------------------------------------------- #
    def _ledger_rel(self):
        return self.cfg.ledger_path.relative_to(self.cfg.repo_root).as_posix()

    def _is_log_state(self, path):
        """True for controller log/runtime state (excluded from the worker's diff)."""
        log_rel = self.cfg.log_dir.relative_to(self.cfg.repo_root).as_posix()
        return path == ".st4-loop" or path.startswith(".st4-loop/") or path.startswith(log_rel + "/")

    def _validate_declared_files(self, files):
        """Fail closed on absolute paths, `..` traversal, the ledger, or log state.

        Returns (normalized_list, problems).
        """
        problems = []
        normalized = []
        for raw in files:
            if not isinstance(raw, str) or not raw:
                problems.append(f"invalid file entry: {raw!r}")
                continue
            if raw.startswith("/"):
                problems.append(f"absolute path not allowed: {raw}")
                continue
            parts = raw.split("/")
            if ".." in parts:
                problems.append(f"path traversal not allowed: {raw}")
                continue
            norm = posixpath.normpath(raw)
            if norm == self._ledger_rel():
                problems.append(f"worker must not edit the ledger: {raw}")
                continue
            if self._is_log_state(norm):
                problems.append(f"worker must not touch loop state: {raw}")
                continue
            normalized.append(norm)
        return normalized, problems

    def _actual_dirty_production_files(self):
        """Every dirty path (tracked-modified, deleted AND untracked) minus loop state."""
        out = []
        for _code, path in gitutil.porcelain(self.cfg.repo_root):
            if self._is_log_state(path):
                continue
            out.append(posixpath.normpath(path))
        return sorted(set(out))

    def _build_review_bundle(self, files):
        """A complete review patch: tracked `git diff` PLUS full untracked new-file
        contents, so the reviewer sees files `git diff` alone would hide."""
        chunks = ["=== tracked changes (git diff) ===", gitutil.diff(self.cfg.repo_root)]
        root = self.cfg.repo_root
        codes = {path: code for code, path in gitutil.porcelain(root)}
        new_files = [f for f in files if codes.get(f) == "??"]
        if new_files:
            chunks.append("=== new / untracked files (full content) ===")
            for rel in new_files:
                abs_path = root / rel
                try:
                    content = abs_path.read_text(encoding="utf-8", errors="replace")
                except OSError as exc:
                    content = f"<unreadable: {exc}>"
                chunks.append(f"--- new file: {rel} ---")
                chunks.append(content)
                chunks.append(f"--- end {rel} ---")
        return "\n".join(chunks)

    def _measure_debt_delta(self, base_debt):
        """Return (actual_delta, current_count) measured independently in the worktree."""
        current = self.debt_measurer(self.cfg.repo_root)
        return current - base_debt, current

    # -- prompt construction ------------------------------------------------ #
    def build_worker_prompt(self, item):
        template = Path(self.cfg.worker_prompt_path).read_text(encoding="utf-8")
        return (
            f"{template}\n\n"
            f"{ITEM_BEGIN}\n{json.dumps(item, indent=2)}\n{ITEM_END}\n"
        )

    def _protect(self):
        """Paths the scoped revert must never touch (ledger + log dir)."""
        root = self.cfg.repo_root
        ledger_rel = self.cfg.ledger_path.relative_to(root).as_posix()
        log_rel = self.cfg.log_dir.relative_to(root).as_posix()
        return {ledger_rel, log_rel}

    def _revert_worker_changes(self):
        dirty = gitutil.dirty_paths(self.cfg.repo_root)
        gitutil.scoped_revert(self.cfg.repo_root, dirty, protect=self._protect())

    def _write_log(self, item_id, name, content):
        self.cfg.log_dir.mkdir(parents=True, exist_ok=True)
        stamp = time.strftime("%Y%m%d-%H%M%S")
        path = self.cfg.log_dir / f"{stamp}-{item_id}-{name}.txt"
        path.write_text(content or "", encoding="utf-8")
        return path

    # -- main loop ---------------------------------------------------------- #
    def run(self):
        root = self.cfg.repo_root
        if not gitutil.is_clean(root):
            raise LoopError(
                "refusing to start: worktree is dirty. Commit or stash first.\n"
                + gitutil.run_git(root, "status", "--porcelain").stdout
            )

        data = ledger.load_ledger(self.cfg.ledger_path)
        summary = []
        for iteration in range(1, self.cfg.max_iterations + 1):
            item = ledger.select_ready(data)
            if item is None:
                self.log("[loop] no READY items remain; stopping.")
                break
            item_id = item["id"]
            self.log(
                f"[loop] iteration {iteration}: selected {item_id} "
                f"(priority={ledger.sched(item)['priority']}, status={item['status']})"
            )
            if self.cfg.dry_run:
                self._print_dry_run(item)
                summary.append({"iteration": iteration, "item": item_id, "dry_run": True})
                break  # dry-run proves the selection once, then stops

            base_sha = gitutil.head_sha(root)
            outcome = self._run_item(data, item, base_sha)
            summary.append(outcome)
            # loop continues to the next READY item regardless of outcome
        return summary

    def _print_dry_run(self, item):
        argv = list(self.cfg.claude_cmd) + [
            "-p", "--output-format", "json", "--json-schema", "<schema>",
            "--permission-mode", self.cfg.permission_mode, "--no-session-persistence",
        ]
        if self.cfg.model:
            argv += ["--model", self.cfg.model]
        argv += ["--disallowedTools", *self.cfg.disallowed_tools]
        self.log("[dry-run] would launch worker (prompt piped via stdin):")
        self.log("  " + " ".join(argv) + "  < <worker-prompt>")
        self.log(f"[dry-run] permission-mode={self.cfg.permission_mode} "
                 f"worker-timeout={self.cfg.worker_timeout}s "
                 f"max-attempts={self.cfg.max_attempts}")
        self.log(f"[dry-run] verifier: {' '.join(self.cfg.verify_cmd)} {item['id']}")
        self.log(f"[dry-run] item verification commands:")
        for cmd in ledger.sched(item)["verification"]:
            self.log(f"    - {cmd}")
        self.log(f"[dry-run] expectedDebtDelta: {ledger.sched(item)['expectedDebtDelta']}")

    # -- one item ----------------------------------------------------------- #
    def _run_item(self, data, item, base_sha):
        item_id = item["id"]
        attempts = ledger.sched(item)["attempts"]
        last_outcome = None
        last_report = ""
        base_debt = self.debt_measurer(self.cfg.repo_root)  # clean worktree at entry
        expected_db = (ledger.sched(item)["expectedDebtDelta"] or {}).get("directBackends", 0)
        while attempts < self.cfg.max_attempts:
            attempts += 1
            self.log(f"[{item_id}] attempt {attempts}/{self.cfg.max_attempts}")
            try:
                raw = self.worker_runner(self.build_worker_prompt(item), self.cfg)
            except worker_result.WorkerResultError as exc:
                self.log(f"[{item_id}] worker error: {exc}")
                self._write_log(item_id, f"attempt{attempts}-error", str(exc))
                self._revert_worker_changes()
                continue

            outcome = worker_result.interpret(raw, expected_item_id=item_id)
            last_outcome = outcome
            self._write_log(item_id, f"attempt{attempts}-result", raw or "")
            self.log(
                f"[{item_id}] outcome={outcome.outcome} ok={outcome.ok} "
                f"problems={outcome.problems}"
            )

            if outcome.ok:
                gate = self._independent_gate(item, outcome, base_debt, expected_db, attempts)
                last_report = gate["report"]
                if gate["pass"]:
                    return self._commit_success(
                        data, item, outcome, base_sha, attempts, gate)
                self.log(f"[{item_id}] independent gate failed: {gate['reasons']}")
            # any failure (worker not ok, or gate failed): discard and retry
            self._revert_worker_changes()

        # exhausted attempts -> reproducible blocker, then continue the global loop
        return self._mark_blocked(data, item, last_outcome, last_report, base_sha, attempts)

    def _independent_gate(self, item, outcome, base_debt, expected_db, attempts):
        """Never trust self-report. Enforce, fail closed:
        1. declared files are path-safe (relative, no `..`, not ledger/log state);
        2. declared files EXACTLY equal the actual dirty production/test files;
        3. deterministic verifier is green;
        4. read-only reviewer approves the FULL patch (incl. untracked) w/o debt growth;
        5. independently-measured direct-backend delta == item expectedDebtDelta.
        """
        reasons = []
        item_id = item["id"]

        declared, path_problems = self._validate_declared_files(outcome.files_changed)
        reasons.extend(path_problems)
        declared_set = set(declared)
        actual = self._actual_dirty_production_files()
        actual_set = set(actual)
        if declared_set != actual_set:
            undeclared = sorted(actual_set - declared_set)
            phantom = sorted(declared_set - actual_set)
            if undeclared:
                reasons.append(f"undeclared dirty files (would be unreviewed): {undeclared}")
            if phantom:
                reasons.append(f"declared but not actually changed: {phantom}")

        # deterministic gates
        vok, report = self.verify_runner(item, self.cfg)
        self._write_log(item_id, f"attempt{attempts}-verify", report)
        if not vok:
            reasons.append("deterministic verifier failed")

        # read-only reviewer sees the COMPLETE patch (tracked + untracked contents)
        bundle = self._build_review_bundle(sorted(declared_set | actual_set))
        self._write_log(item_id, f"attempt{attempts}-review-bundle", bundle)
        verdict = self.reviewer_runner(bundle, item, self.cfg)
        approved = bool(verdict.get("approved")) and not verdict.get("debtGrew")
        if not approved:
            reasons.append(f"reviewer did not approve: {verdict.get('issues')}")

        # independent debt measurement (exact match against the item's expectation)
        actual_delta, current_db = self._measure_debt_delta(base_debt)
        if actual_delta != expected_db:
            reasons.append(
                f"measured directBackends delta {actual_delta} != expected {expected_db} "
                f"(base {base_debt} -> {current_db})")

        return {
            "pass": not reasons,
            "reasons": reasons,
            "report": report,
            "files": sorted(declared_set | actual_set),
            "debt_delta": {"directBackends": actual_delta},
            "debt_current": current_db,
            "verdict": verdict,
        }

    def _commit_success(self, data, item, outcome, base_sha, attempts_used, gate):
        item_id = item["id"]
        if outcome.status_advance:
            ledger.advance_status(data, item_id, outcome.status_advance)
        # apply the INDEPENDENTLY MEASURED debt delta, not the worker's self-report
        ledger.apply_debt_delta(data, gate["debt_delta"])
        ledger.record_attempt(item, attempts=attempts_used, blocked=False, commit=base_sha)
        ledger.save_ledger(self.cfg.ledger_path, data)

        root = self.cfg.repo_root
        ledger_rel = self._ledger_rel()
        files = list(gate["files"])  # exactly the reviewed, declared-and-actual files
        commit_paths = files + [ledger_rel]
        sha = None
        if self.cfg.allow_commit:
            message = (
                f"feat(st4-loop): migrate {item_id} "
                f"({item.get('status')} <- worker outcome)\n\n"
                f"{outcome.summary}\n"
                f"debt: directBackends {gate['debt_delta'].get('directBackends')}\n"
                f"evidence: {', '.join(outcome.evidence) or 'n/a'}"
            )
            sha = gitutil.stage_and_commit(root, commit_paths, message)
            self.log(f"[{item_id}] committed {sha}")
        # ensure a clean worktree even if the worker left stray files
        self._revert_worker_changes()
        return {
            "item": item_id, "result": "success", "commit": sha,
            "files": files, "debt_delta": gate["debt_delta"],
        }

    def _mark_blocked(self, data, item, outcome, report, base_sha, attempts_used):
        item_id = item["id"]
        reason = (outcome.blocker if outcome else None) or (
            f"exhausted {self.cfg.max_attempts} attempts without independent "
            f"verification; last outcome={getattr(outcome, 'outcome', 'unknown')}"
        )
        ledger.record_attempt(
            item, attempts=attempts_used, blocked=True,
            blocked_reason=reason, commit=base_sha)
        ledger.save_ledger(self.cfg.ledger_path, data)
        # discard any partial worker edits, then checkpoint the blocked ledger
        self._revert_worker_changes()
        sha = None
        if self.cfg.allow_commit:
            root = self.cfg.repo_root
            ledger_rel = self.cfg.ledger_path.relative_to(root).as_posix()
            sha = gitutil.stage_and_commit(
                root, [ledger_rel],
                f"chore(st4-loop): mark {item_id} blocked\n\n{reason}\n"
                f"verify-report: {report[:2000]}",
            )
            self.log(f"[{item_id}] blocked checkpoint {sha}")
        return {"item": item_id, "result": "blocked", "commit": sha, "reason": reason}
