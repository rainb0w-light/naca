#!/usr/bin/env bash
# Deterministic, standalone verifier for one ST4 migration slice.
#
#   tools/st4-loop/verify-task.sh <ITEM_ID>
#
# It NEVER trusts the worker's self-report. It runs the gates that must stay green
# during migration and asserts debt does not grow:
#   1. :naca-cloud-native:test  -- LedgerConsistencyTest (ledger stays valid)
#   2. :naca-trans:test         -- daily gate; includes architecture.DirectBackendInventoryTest
#                                  which asserts directBackends <= 170 (debt cannot grow)
#   3. the item's own `verification` commands from the ledger
#   4. :naca-trans:finalArchitectureCheck -- EXPECTED RED during migration; we only
#      assert its failure count did not EXCEED the recorded baseline (debt not grow)
#
# Env overrides (used by tests / fast paths):
#   ST4_LEDGER                     path to migration-ledger.json (default: docs/migration-ledger.json)
#   ST4_SKIP_GRADLE=1              skip all gradle gates (unit-test fast path)
#   ST4_SKIP_ARCH=1                skip the finalArchitectureCheck debt-growth check
#   ST4_ARCH_FAILURES_BASELINE=N   override the baseline failure count (default: from ledger)
#
# Exit 0 = verified; non-zero = failed (with a report on stdout).
set -uo pipefail

ITEM_ID="${1:-${ST4_ITEM_ID:-}}"
if [[ -z "$ITEM_ID" ]]; then
  echo "usage: verify-task.sh <ITEM_ID>" >&2
  exit 64
fi

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"

LEDGER="${ST4_LEDGER:-$REPO_ROOT/docs/migration-ledger.json}"
if [[ ! -f "$LEDGER" ]]; then
  echo "VERIFY-FAIL: ledger not found: $LEDGER"
  exit 66
fi

fail() { echo "VERIFY-FAIL: $*"; exit 1; }
note() { echo "verify: $*"; }

# --- baseline failure count for finalArchitectureCheck (debt ceiling) --------
BASELINE_FAILURES="${ST4_ARCH_FAILURES_BASELINE:-}"
if [[ -z "$BASELINE_FAILURES" ]]; then
  BASELINE_FAILURES="$(jq -r '.meta.ratchet.finalArchitectureCheck.failures' "$LEDGER")"
fi
[[ "$BASELINE_FAILURES" =~ ^[0-9]+$ ]] || BASELINE_FAILURES=999999
note "debt ceiling: finalArchitectureCheck failures must stay <= $BASELINE_FAILURES"

# --- 1. ledger consistency gate ---------------------------------------------
if [[ "${ST4_SKIP_GRADLE:-0}" != "1" ]]; then
  note "running :naca-cloud-native:test (LedgerConsistencyTest)"
  ./gradlew :naca-cloud-native:test --tests "*LedgerConsistencyTest" --console=plain \
    || fail "LedgerConsistencyTest is not green"

  # --- 2. daily transpiler gate (includes the direct-backend debt ratchet) ---
  note "running :naca-trans:test (daily gate + DirectBackendInventoryTest ratchet)"
  ./gradlew :naca-trans:test --console=plain \
    || fail ":naca-trans:test daily gate is not green (debt may have grown)"

  # --- 4. architecture debt must not grow (check is expected RED overall) ----
  if [[ "${ST4_SKIP_ARCH:-0}" != "1" ]]; then
    note "running :naca-trans:finalArchitectureCheck (expected RED; measuring debt)"
    ./gradlew :naca-trans:finalArchitectureCheck --console=plain --continue >/dev/null 2>&1
    # Sum failures+errors across the JUnit XML for this task.
    RESULTS_DIR="naca-trans/build/test-results/finalArchitectureCheck"
    CURRENT_FAILURES=0
    if [[ -d "$RESULTS_DIR" ]]; then
      _f=$(grep -ho 'failures="[0-9]*"' "$RESULTS_DIR"/*.xml 2>/dev/null \
             | grep -o '[0-9]*' | awk '{s+=$1} END{print s+0}')
      _e=$(grep -ho 'errors="[0-9]*"' "$RESULTS_DIR"/*.xml 2>/dev/null \
             | grep -o '[0-9]*' | awk '{s+=$1} END{print s+0}')
      CURRENT_FAILURES=$((_f + _e))
    fi
    [[ "$CURRENT_FAILURES" =~ ^[0-9]+$ ]] || CURRENT_FAILURES=999999
    note "finalArchitectureCheck failures now: $CURRENT_FAILURES (ceiling $BASELINE_FAILURES)"
    if (( CURRENT_FAILURES > BASELINE_FAILURES )); then
      fail "architecture debt GREW: $CURRENT_FAILURES > baseline $BASELINE_FAILURES"
    fi
  fi
else
  note "ST4_SKIP_GRADLE=1: skipping gradle gates (test fast path)"
fi

# --- 3. the item's own verification commands (always run) -------------------
# Bash 3.2 portable (no mapfile/readarray): read into an array via while-read.
ITEM_CMDS=()
while IFS= read -r _cmd; do
  ITEM_CMDS+=("$_cmd")
done < <(jq -r --arg id "$ITEM_ID" \
  '.entries[] | select(.id==$id) | (.verification // [])[]' "$LEDGER")
for cmd in "${ITEM_CMDS[@]}"; do
  [[ -z "$cmd" ]] && continue
  note "running item verification: $cmd"
  bash -c "$cmd" || fail "item verification command failed: $cmd"
done

echo "VERIFY-PASS: $ITEM_ID"
exit 0
