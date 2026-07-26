"""Ledger loading, scheduler selection and field updates.

The ledger (docs/migration-ledger.json) is the single source of truth. This module
adds the minimum scheduler fields the loop needs, kept additive so the existing
LedgerConsistencyTest (which does not know about them) stays green:

    priority          int    lower = do first
    dependencies      [id]   must be satisfied (>= production-wired) before READY
    verification      [cmd]  deterministic commands the slice must pass
    expectedDebtDelta {..}   e.g. {"directBackends": -1}
    attempts          int    worker attempts spent so far
    blocked           bool   scheduler will skip a blocked item
    blockedReason     str?   reproducible blocker (set when blocked)
    lastAttemptCommit str?   commit of the last successful checkpoint
"""

import json
from pathlib import Path

# Scopes that are in the migration queue. BMS_ARTIFACT (and any non-COBOL pipeline
# such as FPac) is deliberately excluded.
QUEUE_SCOPES = ("COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS")

# A dependency counts as satisfied once the feature works in production.
SATISFIED_STATUSES = ("production-wired", "direct-retired", "done")

DEFAULT_PRIORITY = 1000

_SCHEDULER_DEFAULTS = {
    "priority": DEFAULT_PRIORITY,
    "dependencies": [],
    "verification": [],
    "expectedDebtDelta": {},
    "attempts": 0,
    "blocked": False,
    "blockedReason": None,
    "lastAttemptCommit": None,
}


class LedgerError(Exception):
    pass


def load_ledger(path):
    path = Path(path)
    if not path.is_file():
        raise LedgerError(f"ledger not found: {path}")
    with path.open(encoding="utf-8") as fh:
        return json.load(fh)


def save_ledger(path, data):
    path = Path(path)
    with path.open("w", encoding="utf-8") as fh:
        json.dump(data, fh, indent=2, ensure_ascii=False)
        fh.write("\n")


def status_order(data):
    return list(data["meta"]["statusOrder"])


def status_index(data, status):
    order = status_order(data)
    if status not in order:
        raise LedgerError(f"unknown status: {status}")
    return order.index(status)


def sched(entry):
    """Return the scheduler sub-fields of an entry with defaults applied.

    Reads top-level scheduler fields if present, else returns defaults. Never
    mutates the entry.
    """
    out = dict(_SCHEDULER_DEFAULTS)
    for key in _SCHEDULER_DEFAULTS:
        if key in entry and entry[key] is not None:
            out[key] = entry[key]
    return out


def entry_by_id(data, item_id):
    for entry in data["entries"]:
        if entry.get("id") == item_id:
            return entry
    return None


def _dependencies_satisfied(data, entry):
    deps = sched(entry)["dependencies"]
    for dep_id in deps:
        dep = entry_by_id(data, dep_id)
        if dep is None:
            return False  # missing dependency -> not ready (fail closed)
        if dep.get("status") not in SATISFIED_STATUSES:
            return False
    return True


def is_ready(data, entry):
    """READY = in-queue, production-reachable, not done, not blocked, deps satisfied."""
    if entry.get("scope") not in QUEUE_SCOPES:
        return False
    if not entry.get("productionReachable"):
        return False
    if entry.get("status") == "done":
        return False
    if sched(entry)["blocked"]:
        return False
    return _dependencies_satisfied(data, entry)


def select_ready(data):
    """Pick exactly one highest-priority READY item, or None.

    Sort key: priority asc, then furthest-along status first, then id asc (stable,
    deterministic tie-break).
    """
    candidates = [e for e in data["entries"] if is_ready(data, e)]
    if not candidates:
        return None
    candidates.sort(
        key=lambda e: (
            sched(e)["priority"],
            -status_index(data, e["status"]),
            e["id"],
        )
    )
    return candidates[0]


def advance_status(data, item_id, new_status):
    """Move an entry's status forward along statusOrder (never backwards)."""
    entry = entry_by_id(data, item_id)
    if entry is None:
        raise LedgerError(f"no such entry: {item_id}")
    current = entry.get("status")
    if new_status == current:
        return
    if status_index(data, new_status) < status_index(data, current):
        raise LedgerError(
            f"{item_id}: status may only advance forward "
            f"({current} -> {new_status} refused)"
        )
    entry["status"] = new_status


def record_attempt(entry, attempts=None, blocked=False, blocked_reason=None, commit=None):
    """Record the attempt count and optional blocked/checkpoint state (in place).

    `attempts` is the absolute number of worker attempts consumed; if omitted it
    defaults to incrementing the existing count by one.
    """
    if attempts is None:
        attempts = sched(entry)["attempts"] + 1
    entry["attempts"] = attempts
    if blocked:
        entry["blocked"] = True
        entry["blockedReason"] = blocked_reason
    if commit:
        entry["lastAttemptCommit"] = commit


def apply_debt_delta(data, debt_delta):
    """Apply a worker's debtDelta onto meta.ratchet.finalArchitectureCheck.

    Enforces the ratchet: COBOL-scope directBackends may only decrease. Returns the
    updated fac block. Raises LedgerError if debt would grow.
    """
    if not debt_delta:
        return data["meta"]["ratchet"]["finalArchitectureCheck"]
    fac = data["meta"]["ratchet"]["finalArchitectureCheck"]
    for key, delta in debt_delta.items():
        if key not in fac:
            continue  # only ratchet known counters
        new_value = fac[key] + int(delta)
        if key == "directBackends" and new_value > fac[key]:
            raise LedgerError(
                f"ratchet violation: directBackends would grow "
                f"{fac[key]} -> {new_value}"
            )
        fac[key] = new_value
    return fac
