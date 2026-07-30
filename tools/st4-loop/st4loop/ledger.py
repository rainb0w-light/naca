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

# Migration phases. Each phase declares an ORDERED queue of scopes: the scheduler
# fully consumes READY items of earlier scopes before selecting any later scope
# (BMS_ARTIFACT first, FPAC second in phase 2). Exactly one ledger item is handed
# to exactly one worker per iteration, just like phase 1.
#
# Phase 1 (COBOL core + embedded SQL/CICS) is COMPLETE and frozen: every queue
# item is terminal and its direct-backend ratchet sits at zero. Its scope list is
# retained so the completed queue and its zero-debt gates stay reproducible and so
# no phase can ever re-open COBOL/SQL/CICS debt.
MIGRATION_PHASES = {
    "phase-1-cobol": ("COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS"),
    "phase-2-bms-fpac": ("BMS_ARTIFACT", "FPAC"),
}

# The phase the loop runs unless configured otherwise. Phase 1 is done, so the
# active phase migrates the independent BMS and FPac DSL pipelines.
DEFAULT_PHASE = "phase-2-bms-fpac"


def queue_scopes_for(phase):
    """The ordered queue scopes of a migration phase (fail closed on unknown)."""
    try:
        return MIGRATION_PHASES[phase]
    except KeyError:
        raise LedgerError(
            f"unknown migration phase: {phase!r}; known phases: "
            f"{', '.join(sorted(MIGRATION_PHASES))}"
        )


# Back-compat alias: the active phase's queue scopes.
QUEUE_SCOPES = queue_scopes_for(DEFAULT_PHASE)

# A dependency counts as satisfied once the feature works in production.
SATISFIED_STATUSES = ("production-wired", "direct-retired", "done")

# Terminal statuses for this ST4 migration queue: the loop's target (direct backend
# retired onto the recursive-ST4 assembler) is complete at `direct-retired`, and fully
# complete at `done`. Neither is READY for (re)selection — a retired item has no debt
# left to remove, so a re-run would necessarily fail the measured-debt-delta gate.
TERMINAL_STATUSES = ("direct-retired", "done")

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


def is_ready(data, entry, queue_scopes=None):
    """READY = in-queue, production-reachable, not terminal, not blocked, deps satisfied.

    `queue_scopes` selects the migration phase's queue; it defaults to the active
    phase (DEFAULT_PHASE). Terminal = `direct-retired` or `done` (see
    TERMINAL_STATUSES): the slice's debt is already removed, so it must not be
    re-selected.
    """
    scopes = QUEUE_SCOPES if queue_scopes is None else tuple(queue_scopes)
    if entry.get("scope") not in scopes:
        return False
    if not entry.get("productionReachable"):
        return False
    if entry.get("status") in TERMINAL_STATUSES:
        return False
    if sched(entry)["blocked"]:
        return False
    return _dependencies_satisfied(data, entry)


def select_ready(data, queue_scopes=None):
    """Pick exactly one READY item, or None.

    A phase's scopes are an ORDERED queue: every READY item of an earlier scope
    ranks before every item of a later scope (phase 2 drains BMS_ARTIFACT fully
    before any FPAC item). Within one scope: priority asc, then furthest-along
    status first, then id asc (stable, deterministic tie-break).
    """
    scopes = QUEUE_SCOPES if queue_scopes is None else tuple(queue_scopes)
    candidates = [e for e in data["entries"] if is_ready(data, e, scopes)]
    if not candidates:
        return None
    scope_rank = {scope: index for index, scope in enumerate(scopes)}
    candidates.sort(
        key=lambda e: (
            scope_rank[e["scope"]],
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


def _ratchet_counter_blocks(data):
    """Every ratchet block that may hold debt counters, probed in order.

    Phase 1 counters live in meta.ratchet.finalArchitectureCheck; phase 2 keeps
    its frozen COBOL gates untouched and records BMS/FPac counters in their own
    blocks under meta.ratchet.phase2.
    """
    ratchet = data["meta"]["ratchet"]
    blocks = [ratchet["finalArchitectureCheck"]]
    phase2 = ratchet.get("phase2")
    if isinstance(phase2, dict):
        for key in ("bmsArchitectureCheck", "fpacArchitectureCheck"):
            block = phase2.get(key)
            if isinstance(block, dict):
                blocks.append(block)
    return blocks


def apply_debt_delta(data, debt_delta):
    """Apply a worker's debtDelta onto the ratchet block owning each counter key.

    `directBackends` (COBOL scope) routes to finalArchitectureCheck;
    `bmsDirectBackends` / `fpacDirectBackends` route to their phase-2 blocks.
    Enforces the ratchet: NO pipeline's direct-backend count may ever grow.
    Returns the block holding the first applied counter (the fac block when the
    delta is empty), for backwards compatibility. Raises LedgerError on growth.
    """
    fac = data["meta"]["ratchet"]["finalArchitectureCheck"]
    if not debt_delta:
        return fac
    blocks = _ratchet_counter_blocks(data)
    for key, delta in debt_delta.items():
        target = next((block for block in blocks if key in block), None)
        if target is None:
            continue  # only ratchet known counters
        new_value = target[key] + int(delta)
        if key.lower().endswith("directbackends") and new_value > target[key]:
            raise LedgerError(
                f"ratchet violation: {key} would grow "
                f"{target[key]} -> {new_value}"
            )
        target[key] = new_value
    return fac
