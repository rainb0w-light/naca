import json
import unittest

from st4loop import worker_result as wr


def envelope(structured, is_error=False):
    return json.dumps({
        "type": "result",
        "subtype": "success",
        "is_error": is_error,
        "result": json.dumps(structured) if structured else "text only",
        "structured_output": structured,
        "session_id": "s1",
        "total_cost_usd": 0.5,
    })


def good(item="CICS-RETURN", outcome="success", debt=-1, **over):
    so = {
        "itemId": item, "outcome": outcome, "summary": "ok",
        "filesChanged": ["a.java"], "debtDelta": {"directBackends": debt},
        "evidence": ["T"], "blocker": None,
    }
    so.update(over)
    return so


class InterpretTest(unittest.TestCase):
    def test_success_is_ok(self):
        out = wr.interpret(envelope(good()), expected_item_id="CICS-RETURN")
        self.assertTrue(out.ok)
        self.assertEqual(out.outcome, "success")
        self.assertEqual(out.files_changed, ["a.java"])
        self.assertEqual(out.debt_delta, {"directBackends": -1})
        self.assertEqual(out.cost_usd, 0.5)

    def test_is_error_flag_fails_closed(self):
        out = wr.interpret(envelope(good(), is_error=True))
        self.assertFalse(out.ok)
        self.assertTrue(out.is_error)

    def test_failed_outcome_not_ok(self):
        out = wr.interpret(envelope(good(outcome="failed")))
        self.assertFalse(out.ok)
        self.assertEqual(out.outcome, "failed")

    def test_item_id_mismatch_is_a_problem(self):
        out = wr.interpret(envelope(good(item="OTHER")), expected_item_id="CICS-RETURN")
        self.assertFalse(out.ok)
        self.assertTrue(any("mismatch" in p for p in out.problems))

    def test_debt_growth_is_a_problem(self):
        out = wr.interpret(envelope(good(debt=1)))
        self.assertFalse(out.ok)
        self.assertTrue(any("directBackends" in p for p in out.problems))

    def test_blocked_requires_blocker(self):
        out = wr.interpret(envelope(good(outcome="blocked", blocker=None)))
        self.assertFalse(out.ok)
        self.assertTrue(any("blocker" in p for p in out.problems))

    def test_malformed_json_fails_closed(self):
        out = wr.interpret("not json")
        self.assertFalse(out.ok)
        self.assertTrue(out.problems)

    def test_empty_output_fails_closed(self):
        out = wr.interpret("")
        self.assertFalse(out.ok)

    def test_missing_structured_output_fails_closed(self):
        raw = json.dumps({"type": "result", "is_error": False, "result": "plain text"})
        out = wr.interpret(raw)
        self.assertFalse(out.ok)
        self.assertTrue(any("structured_output" in p for p in out.problems))

    def test_structured_in_result_string_is_recovered(self):
        so = good()
        raw = json.dumps({"type": "result", "is_error": False, "result": json.dumps(so)})
        out = wr.interpret(raw, expected_item_id="CICS-RETURN")
        self.assertTrue(out.ok)

    def test_missing_required_field(self):
        so = good()
        del so["debtDelta"]
        out = wr.interpret(envelope(so))
        self.assertFalse(out.ok)
        self.assertTrue(any("debtDelta" in p for p in out.problems))


if __name__ == "__main__":
    unittest.main()
