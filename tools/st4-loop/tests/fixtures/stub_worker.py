#!/usr/bin/env python3
"""Stub `claude` worker used by controller tests (no real model is invoked).

It accepts the same argv shape the controller builds (`-p --output-format json
--json-schema ... <prompt>`) and emits a documented JSON envelope on stdout, driven
by environment variables:

    ST4_STUB_OUTCOME         success | failed | blocked   (default: success)
    ST4_STUB_ITEM_ID         item id to echo               (default: TEST-ITEM)
    ST4_STUB_TOUCH           repo-relative file to create/append (simulate an edit)
    ST4_STUB_STATUS_ADVANCE  statusAdvance value           (default: none)
    ST4_STUB_DEBT            directBackends delta integer  (default: 0)
    ST4_STUB_BAD_JSON        1 -> print non-JSON garbage instead of an envelope
    ST4_STUB_NO_STRUCTURED   1 -> emit an envelope with no structured_output
    ST4_STUB_NO_DECLARE      1 -> touch ST4_STUB_TOUCH but do NOT declare it (undeclared file)
    ST4_STUB_EXTRA_FILES     comma list added to filesChanged WITHOUT being created
                             (simulate phantom / absolute / `..` declarations)
"""
import json
import os
import sys


def main():
    if os.environ.get("ST4_STUB_BAD_JSON") == "1":
        sys.stdout.write("this is not json at all\n")
        return 0
    outcome = os.environ.get("ST4_STUB_OUTCOME", "success")
    item_id = os.environ.get("ST4_STUB_ITEM_ID", "TEST-ITEM")
    debt = int(os.environ.get("ST4_STUB_DEBT", "0"))
    status_advance = os.environ.get("ST4_STUB_STATUS_ADVANCE") or None

    touch = os.environ.get("ST4_STUB_TOUCH")
    files = []
    if touch:
        with open(touch, "a", encoding="utf-8") as fh:
            fh.write(f"// edited by stub worker for {item_id}\n")
        if os.environ.get("ST4_STUB_NO_DECLARE") != "1":
            files.append(touch)
    extra = os.environ.get("ST4_STUB_EXTRA_FILES")
    if extra:
        files.extend(p for p in extra.split(",") if p)

    if os.environ.get("ST4_STUB_NO_STRUCTURED") == "1":
        envelope = {"type": "result", "subtype": "success", "is_error": False,
                    "result": "did stuff but no structured output", "session_id": "stub"}
        sys.stdout.write(json.dumps(envelope))
        return 0

    structured = {
        "itemId": item_id,
        "outcome": outcome,
        "summary": f"stub {outcome} for {item_id}",
        "filesChanged": files,
        "statusAdvance": status_advance,
        "verificationRun": ["stub-verify -> PASS"],
        "debtDelta": {"directBackends": debt},
        "evidence": ["stub.EvidenceTest"],
        "blocker": ("reproducible stub blocker" if outcome == "blocked" else None),
    }
    envelope = {
        "type": "result",
        "subtype": "success" if outcome == "success" else "error_during_execution",
        "is_error": outcome != "success" and os.environ.get("ST4_STUB_IS_ERROR") == "1",
        "result": json.dumps(structured),
        "structured_output": structured,
        "session_id": "stub-session",
        "total_cost_usd": 0.0,
        "usage": {"input_tokens": 1, "output_tokens": 1},
    }
    sys.stdout.write(json.dumps(envelope))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
