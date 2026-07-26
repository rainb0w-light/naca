#!/usr/bin/env bash
# Thin wrapper around the Python controller. All real logic lives in run_loop.py so
# it is unit-testable; this shim only resolves the repo root and forwards args.
#
# Usage:
#   tools/st4-loop/run-loop.sh --dry-run
#   tools/st4-loop/run-loop.sh --max-iterations 1
#   tools/st4-loop/run-loop.sh --max-iterations 50 --max-attempts 3
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PY="${PYTHON:-python3}"
if ! command -v "$PY" >/dev/null 2>&1; then
  echo "ERROR: python3 not found on PATH" >&2
  exit 3
fi

exec "$PY" "$SCRIPT_DIR/run_loop.py" "$@"
