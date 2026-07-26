import copy
import unittest

from st4loop import ledger

STATUS_ORDER = ["not-started", "parser-preserved", "semantic-built",
                "binding-added", "template-added", "production-wired",
                "direct-retired", "done"]


def base_ledger(entries):
    return {
        "meta": {
            "schemaVersion": 1,
            "statusOrder": STATUS_ORDER,
            "scopeOrder": ["COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS", "BMS_ARTIFACT"],
            "ratchet": {
                "finalArchitectureCheck": {"tests": 10, "failures": 5, "directBackends": 3},
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
    def test_picks_highest_priority_ready(self):
        data = base_ledger([
            entry("BMS-X", scope="BMS_ARTIFACT", status="production-wired", priority=1),
            entry("CICS-LOW", status="not-started", priority=50),
            entry("CICS-HIGH", status="semantic-built", priority=10),
            entry("CICS-BLOCKED", priority=1, blocked=True),
            entry("CICS-DONE", status="done", priority=1),
        ])
        sel = ledger.select_ready(data)
        self.assertEqual(sel["id"], "CICS-HIGH")

    def test_excludes_bms_and_non_production_reachable(self):
        data = base_ledger([
            entry("BMS-ONLY", scope="BMS_ARTIFACT", priority=1),
            entry("NOT-PR", priority=2, productionReachable=False),
        ])
        self.assertIsNone(ledger.select_ready(data))

    def test_dependency_blocks_until_satisfied(self):
        data = base_ledger([
            entry("CICS-DEP", priority=1, dependencies=["CICS-LOW"]),
            entry("CICS-LOW", status="not-started", priority=50),
        ])
        # dep not-started -> CICS-DEP not ready; only CICS-LOW selectable
        self.assertEqual(ledger.select_ready(data)["id"], "CICS-LOW")
        # advance dep to production-wired -> satisfied -> CICS-DEP (prio 1) wins
        ledger.entry_by_id(data, "CICS-LOW")["status"] = "production-wired"
        self.assertEqual(ledger.select_ready(data)["id"], "CICS-DEP")

    def test_missing_dependency_is_fail_closed(self):
        data = base_ledger([entry("CICS-DEP", priority=1, dependencies=["GONE"])])
        self.assertIsNone(ledger.select_ready(data))

    def test_furthest_along_wins_priority_tie(self):
        data = base_ledger([
            entry("A", status="not-started", priority=10),
            entry("B", status="semantic-built", priority=10),
        ])
        self.assertEqual(ledger.select_ready(data)["id"], "B")

    def test_id_tiebreak_is_deterministic(self):
        data = base_ledger([
            entry("BB", status="semantic-built", priority=10),
            entry("AA", status="semantic-built", priority=10),
        ])
        self.assertEqual(ledger.select_ready(data)["id"], "AA")


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
