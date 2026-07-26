#!/usr/bin/env python3
"""CLI entry for the external ST4 migration loop controller.

Examples:
    # prove which task would be selected, without invoking claude
    python3 tools/st4-loop/run_loop.py --dry-run

    # one real iteration (launches one fresh worker for the top READY item)
    python3 tools/st4-loop/run_loop.py --max-iterations 1

    # run the loop repeatedly
    python3 tools/st4-loop/run_loop.py --max-iterations 50 --max-attempts 3

The worker/verify/reviewer commands are injectable so the loop is testable without
ever invoking the real `claude` binary (see tools/st4-loop/tests).
"""

import argparse
import shlex
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))

from st4loop.controller import (  # noqa: E402
    Config, Controller, LoopError,
    WORKER_PERMISSION_MODES, DEFAULT_WORKER_PERMISSION_MODE,
)


def default_repo_root():
    # tools/st4-loop/run_loop.py -> repo root is two levels up
    return HERE.parent.parent


def build_config(args):
    repo = Path(args.repo_root).resolve()
    return Config(
        repo_root=repo,
        ledger_path=repo / "docs" / "migration-ledger.json",
        worker_prompt_path=repo / ".claude" / "st4-loop" / "worker.md",
        result_schema_path=repo / ".claude" / "st4-loop" / "result.schema.json",
        review_schema_path=repo / ".claude" / "st4-loop" / "review.schema.json",
        log_dir=repo / ".st4-loop" / "logs",
        claude_cmd=shlex.split(args.claude_cmd),
        verify_cmd=(shlex.split(args.verify_cmd) if args.verify_cmd else None),
        reviewer_cmd=(shlex.split(args.reviewer_cmd) if args.reviewer_cmd else None),
        model=args.model,
        effort=args.effort,
        max_iterations=args.max_iterations,
        max_attempts=args.max_attempts,
        worker_timeout=args.worker_timeout,
        permission_mode=args.permission_mode,
        dry_run=args.dry_run,
        allow_commit=not args.no_commit,
    )


def _build_parser():
    parser = argparse.ArgumentParser(description="External ST4 migration loop controller")
    parser.add_argument("--repo-root", default=str(default_repo_root()))
    parser.add_argument("--dry-run", action="store_true",
                        help="select and print the top READY task; do not invoke claude")
    parser.add_argument("--max-iterations", type=int, default=1)
    parser.add_argument("--max-attempts", type=int, default=3,
                        help="worker attempts per item before it is marked blocked")
    parser.add_argument("--worker-timeout", type=int, default=1800,
                        help="seconds before a single worker run is killed (default 1800)")
    parser.add_argument("--permission-mode", default=DEFAULT_WORKER_PERMISSION_MODE,
                        choices=list(WORKER_PERMISSION_MODES),
                        help="worker permission mode (default acceptEdits; the loop "
                             "never uses bypassPermissions/dontAsk/dangerously-skip)")
    parser.add_argument("--claude-cmd", default="claude",
                        help="base worker command (inject a stub for tests)")
    parser.add_argument("--verify-cmd", default=None,
                        help="override verifier command (default: verify-task.sh)")
    parser.add_argument("--reviewer-cmd", default=None,
                        help="override reviewer base command (default: claude reviewer)")
    parser.add_argument("--model", default=None)
    parser.add_argument("--effort", default=None)
    parser.add_argument("--no-commit", action="store_true",
                        help="do not create commits (still updates the ledger file)")
    return parser


def main(argv=None):
    parser = _build_parser()
    args = parser.parse_args(argv)

    cfg = build_config(args)
    controller = Controller(cfg)
    try:
        summary = controller.run()
    except LoopError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2
    print("\n=== run summary ===")
    for entry in summary:
        print(entry)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
