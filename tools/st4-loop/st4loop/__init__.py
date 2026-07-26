"""ST4 migration loop controller.

Small, auditable helpers that drive the external migration loop:
  - ledger.py        : load/save docs/migration-ledger.json + scheduler selection
  - worker_result.py : parse the Claude Code JSON envelope + structured result
  - gitutil.py       : worktree cleanliness, scoped revert, explicit commits
  - controller.py    : orchestration (select -> worker -> verify -> commit/block)

The loop is injectable: the worker command, verifier and reviewer are all
overridable so it is fully testable without ever invoking the real `claude` CLI.
"""
