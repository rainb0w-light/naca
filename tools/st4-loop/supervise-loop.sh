#!/usr/bin/env bash
# Persistent wrapper for the fail-closed ST4 controller.
#
# The controller intentionally exits cleanly when the Claude API reports quota
# or service unavailability. This supervisor keeps the tmux session alive and
# retries after a bounded interval, while preserving one append-only operator
# log and one capture of the latest controller run.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
STATE_DIR="$REPO_ROOT/.st4-loop"
CONTINUOUS_LOG="$STATE_DIR/continuous-loop.log"
LATEST_RUN="$STATE_DIR/latest-supervisor-run.log"
RETRY_SECONDS="${ST4_LOOP_RETRY_SECONDS:-1800}"

if ! [[ "$RETRY_SECONDS" =~ ^[1-9][0-9]*$ ]]; then
  echo "ERROR: ST4_LOOP_RETRY_SECONDS must be a positive integer" >&2
  exit 3
fi

mkdir -p "$STATE_DIR"

while true; do
  set +e
  "$SCRIPT_DIR/run-loop.sh" "$@" 2>&1 \
    | tee "$LATEST_RUN" \
    | tee -a "$CONTINUOUS_LOG"
  controller_status=${PIPESTATUS[0]}
  set -e

  if rg -q -F "[loop] no READY items remain" "$LATEST_RUN"; then
    echo "[supervisor] queue has no READY item; stopping for ledger intervention."
    exit "$controller_status"
  fi

  if rg -q -F "[loop] Claude API unavailable" "$LATEST_RUN"; then
    reason="Claude API unavailable"
  elif (( controller_status != 0 )); then
    reason="controller exit $controller_status"
  else
    reason="iteration batch completed"
  fi

  next_retry="$(date -v+"${RETRY_SECONDS}"S '+%Y-%m-%d %H:%M:%S %Z' 2>/dev/null \
    || date -d "+${RETRY_SECONDS} seconds" '+%Y-%m-%d %H:%M:%S %Z' 2>/dev/null \
    || echo "after ${RETRY_SECONDS}s")"
  echo "[supervisor] ${reason}; retrying at ${next_retry}."
  sleep "$RETRY_SECONDS"
done
