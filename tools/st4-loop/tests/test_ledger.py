import copy
import unittest

from st4loop import ledger

STATUS_ORDER = ["not-started", "parser-preserved", "semantic-built",
                "binding-added", "template-added", "production-wired",
                "direct-retired", "done"]


PHASE1_SCOPES = ("COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS")
PHASE2_SCOPES = ("BMS_ARTIFACT", "FPAC")


def base_ledger(entries):
    return {
        "meta": {
            "schemaVersion": 1,
            "statusOrder": STATUS_ORDER,
            "scopeOrder": ["COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS",
                           "BMS_ARTIFACT", "FPAC"],
            "ratchet": {
                "finalArchitectureCheck": {"tests": 10, "failures": 5, "directBackends": 3},
                "phase2": {
                    "phase": "phase-2-bms-fpac",
                    "queueScopes": ["BMS_ARTIFACT", "FPAC"],
                    "bmsArchitectureCheck": {"bmsDirectBackends": 4},
                    "fpacArchitectureCheck": {"fpacDirectBackends": 7},
                },
                "rules": ["r"],
            },
        },
        "entries": entries,
    }


def entry(id, scope="EMBEDDED_CICS", status="semantic-built", pr=True, **sched):
    e = {"id": id, "scope": scope, "status": status,
         "productionReachable": pr, "blocker": None}
    e.update(sched)
    return e


class SelectionTest(unittest.TestCase):
    def test_picks_highest_priority_ready_within_a_phase(self):
        data = base_ledger([
            entry("BMS-X", scope="BMS_ARTIFACT", status="production-wired", priority=1),
            entry("CICS-LOW", status="not-started", priority=50),
            entry("CICS-HIGH", status="semantic-built", priority=10),
            entry("CICS-BLOCKED", priority=1, blocked=True),
            entry("CICS-DONE", status="done", priority=1),
        ])
        sel = ledger.select_ready(data, PHASE1_SCOPES)
        self.assertEqual(sel["id"], "CICS-HIGH")

    def test_phase1_excludes_bms_and_fpac(self):
        # The completed COBOL phase never re-opens BMS/FPac (or COBOL) debt
        # outside its own frozen scope list.
        data = base_ledger([
            entry("BMS-ONLY", scope="BMS_ARTIFACT", priority=1),
            entry("FPAC-ONLY", scope="FPAC", priority=2),
            entry("NOT-PR", priority=3, productionReachable=False),
        ])
        self.assertIsNone(ledger.select_ready(data, PHASE1_SCOPES))

    def test_default_phase_is_phase2_bms_fpac(self):
        self.assertEqual(ledger.DEFAULT_PHASE, "phase-2-bms-fpac")
        self.assertEqual(ledger.QUEUE_SCOPES, ("BMS_ARTIFACT", "FPAC"))
        self.assertEqual(ledger.queue_scopes_for("phase-1-cobol"), PHASE1_SCOPES)
        self.assertEqual(ledger.queue_scopes_for("phase-2-bms-fpac"), PHASE2_SCOPES)

    def test_unknown_phase_fails_closed(self):
        with self.assertRaises(ledger.LedgerError):
            ledger.queue_scopes_for("phase-9-nope")

    def test_phase2_selects_bms_before_fpac_regardless_of_priority(self):
        # The phase's scope order is a structural queue: every READY BMS item
        # is drained before any FPAC item, even against priority numbers.
        data = base_ledger([
            entry("FPAC-URGENT", scope="FPAC", status="semantic-built", priority=1),
            entry("BMS-LATER", scope="BMS_ARTIFACT", status="semantic-built",
                  priority=9000),
        ])
        self.assertEqual(ledger.select_ready(data, PHASE2_SCOPES)["id"], "BMS-LATER")
        # Once BMS is terminal, FPAC is selected.
        ledger.entry_by_id(data, "BMS-LATER")["status"] = "direct-retired"
        self.assertEqual(ledger.select_ready(data, PHASE2_SCOPES)["id"], "FPAC-URGENT")

    def test_phase2_excludes_completed_cobol_scopes(self):
        data = base_ledger([
            entry("CICS-ONE", status="semantic-built", priority=1),
            entry("FPAC-ONE", scope="FPAC", status="semantic-built", priority=2),
        ])
        self.assertEqual(ledger.select_ready(data, PHASE2_SCOPES)["id"], "FPAC-ONE")

    def test_default_selection_uses_active_phase(self):
        # No explicit queue_scopes -> DEFAULT_PHASE (phase 2): BMS/FPAC in
        # queue, COBOL scopes out.
        data = base_ledger([
            entry("CICS-ONE", status="semantic-built", priority=1),
            entry("BMS-ONE", scope="BMS_ARTIFACT", status="semantic-built", priority=9),
        ])
        self.assertEqual(ledger.select_ready(data)["id"], "BMS-ONE")

    def test_dependency_blocks_until_satisfied(self):
        data = base_ledger([
            entry("CICS-DEP", priority=1, dependencies=["CICS-LOW"]),
            entry("CICS-LOW", status="not-started", priority=50),
        ])
        # dep not-started -> CICS-DEP not ready; only CICS-LOW selectable
        self.assertEqual(ledger.select_ready(data, PHASE1_SCOPES)["id"], "CICS-LOW")
        # advance dep to production-wired -> satisfied -> CICS-DEP (prio 1) wins
        ledger.entry_by_id(data, "CICS-LOW")["status"] = "production-wired"
        self.assertEqual(ledger.select_ready(data, PHASE1_SCOPES)["id"], "CICS-DEP")

    def test_missing_dependency_is_fail_closed(self):
        data = base_ledger([entry("CICS-DEP", priority=1, dependencies=["GONE"])])
        self.assertIsNone(ledger.select_ready(data, PHASE1_SCOPES))

    def test_furthest_along_wins_priority_tie(self):
        data = base_ledger([
            entry("A", status="not-started", priority=10),
            entry("B", status="semantic-built", priority=10),
        ])
        self.assertEqual(ledger.select_ready(data, PHASE1_SCOPES)["id"], "B")

    def test_id_tiebreak_is_deterministic(self):
        data = base_ledger([
            entry("BB", status="semantic-built", priority=10),
            entry("AA", status="semantic-built", priority=10),
        ])
        self.assertEqual(ledger.select_ready(data, PHASE1_SCOPES)["id"], "AA")

    def test_terminal_direct_retired_is_skipped(self):
        # A priority-1 retired item (debt already removed) must NOT be re-selected;
        # the next READY item wins. Re-running it would fail measured delta 0 != -1.
        data = base_ledger([
            entry("CICS-RETIRED", status="direct-retired", priority=1),
            entry("CICS-NEXT", status="semantic-built", priority=11),
        ])
        self.assertFalse(ledger.is_ready(
            data, ledger.entry_by_id(data, "CICS-RETIRED"), PHASE1_SCOPES))
        self.assertEqual(ledger.select_ready(data, PHASE1_SCOPES)["id"], "CICS-NEXT")

    def test_terminal_statuses_are_not_ready(self):
        for terminal in ledger.TERMINAL_STATUSES:
            data = base_ledger([entry("X", status=terminal, priority=1)])
            self.assertIsNone(ledger.select_ready(data, PHASE1_SCOPES),
                              f"{terminal} must be terminal")

    def test_direct_retired_still_satisfies_a_dependency(self):
        # direct-retired is terminal for selection but counts as a satisfied dependency.
        data = base_ledger([
            entry("CICS-DEP", priority=1, dependencies=["CICS-BASE"]),
            entry("CICS-BASE", status="direct-retired", priority=5),
        ])
        self.assertEqual(ledger.select_ready(data, PHASE1_SCOPES)["id"], "CICS-DEP")


class UpdateTest(unittest.TestCase):
    def test_advance_status_forward_only(self):
        data = base_ledger([entry("X", status="semantic-built")])
        ledger.advance_status(data, "X", "binding-added")
        self.assertEqual(ledger.entry_by_id(data, "X")["status"], "binding-added")
        with self.assertRaises(ledger.LedgerError):
            ledger.advance_status(data, "X", "not-started")

    def test_apply_debt_delta_decreases(self):
        data = base_ledger([entry("X")])
        fac = ledger.apply_debt_delta(data, {"directBackends": -1})
        self.assertEqual(fac["directBackends"], 2)

    def test_apply_debt_delta_refuses_growth(self):
        data = base_ledger([entry("X")])
        with self.assertRaises(ledger.LedgerError):
            ledger.apply_debt_delta(data, {"directBackends": 1})

    def test_apply_debt_delta_routes_bms_and_fpac_counters_to_phase2(self):
        data = base_ledger([entry("X", scope="BMS_ARTIFACT")])
        ledger.apply_debt_delta(data, {"bmsDirectBackends": -1})
        phase2 = data["meta"]["ratchet"]["phase2"]
        self.assertEqual(phase2["bmsArchitectureCheck"]["bmsDirectBackends"], 3)
        self.assertEqual(phase2["fpacArchitectureCheck"]["fpacDirectBackends"], 7)
        ledger.apply_debt_delta(data, {"fpacDirectBackends": -2})
        self.assertEqual(phase2["fpacArchitectureCheck"]["fpacDirectBackends"], 5)
        # the frozen COBOL counter is untouched by phase-2 deltas
        self.assertEqual(
            data["meta"]["ratchet"]["finalArchitectureCheck"]["directBackends"], 3)

    def test_apply_debt_delta_refuses_bms_and_fpac_growth(self):
        data = base_ledger([entry("X", scope="BMS_ARTIFACT")])
        for delta in ({"bmsDirectBackends": 1}, {"fpacDirectBackends": 1}):
            with self.assertRaises(ledger.LedgerError):
                ledger.apply_debt_delta(data, delta)

    def test_record_attempt_and_block(self):
        e = entry("X")
        ledger.record_attempt(e, blocked=True, blocked_reason="boom", commit="abc")
        self.assertEqual(e["attempts"], 1)
        self.assertTrue(e["blocked"])
        self.assertEqual(e["blockedReason"], "boom")
        self.assertEqual(e["lastAttemptCommit"], "abc")

    def test_sched_defaults_are_additive(self):
        e = entry("X")  # no scheduler fields at all
        s = ledger.sched(e)
        self.assertEqual(s["priority"], ledger.DEFAULT_PRIORITY)
        self.assertEqual(s["dependencies"], [])
        self.assertFalse(s["blocked"])
        self.assertNotIn("priority", e)  # sched() must not mutate the entry


if __name__ == "__main__":
    unittest.main()
