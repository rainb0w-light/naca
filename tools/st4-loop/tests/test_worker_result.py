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

    def test_api_error_is_preserved_for_controller_pause(self):
        raw = json.dumps({
            "type": "result",
            "is_error": True,
            "api_error_status": 429,
            "result": "quota exhausted",
        })
        out = wr.interpret(raw)
        self.assertFalse(out.ok)
        self.assertEqual(out.api_error_status, 429)
        self.assertIn("Claude API error 429", out.problems[0])

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


class ExtractRecoveryTest(unittest.TestCase):
    """extract_structured fails closed but recovers exactly one JSON object from a
    free-text `result` (fenced ```json or balanced), as some models ignore
    --json-schema and emit prose + a fenced object instead of structured_output."""

    def env(self, result):
        return {"type": "result", "is_error": False, "result": result}

    def test_recovers_single_fenced_json(self):
        result = "Here is my verdict:\n```json\n{\"approved\": true, \"issues\": []}\n```\nDone."
        self.assertEqual(wr.extract_structured(self.env(result)),
                         {"approved": True, "issues": []})

    def test_recovers_fenced_json_uppercase_tag(self):
        result = "```JSON\n{\"approved\": false}\n```"
        self.assertEqual(wr.extract_structured(self.env(result)), {"approved": False})

    def test_recovers_single_balanced_object_in_prose(self):
        result = "Verdict: {\"approved\": true, \"issues\": [\"x\"]} -- thanks!"
        self.assertEqual(wr.extract_structured(self.env(result)),
                         {"approved": True, "issues": ["x"]})

    def test_whole_string_json_still_works(self):
        self.assertEqual(wr.extract_structured(self.env("{\"a\": 1}")), {"a": 1})

    def test_rejects_multiple_fenced_objects(self):
        result = "```json\n{\"a\": 1}\n```\nand also\n```json\n{\"b\": 2}\n```"
        self.assertIsNone(wr.extract_structured(self.env(result)))

    def test_rejects_multiple_balanced_objects(self):
        result = "first {\"a\": 1} then {\"b\": 2}"
        self.assertIsNone(wr.extract_structured(self.env(result)))

    def test_rejects_malformed_fenced_only(self):
        result = "```json\n{not valid json}\n```"
        self.assertIsNone(wr.extract_structured(self.env(result)))

    def test_rejects_prose_without_json(self):
        self.assertIsNone(wr.extract_structured(self.env("looks good to me")))

    def test_interpret_validates_recovered_fenced_worker_result(self):
        so = good()
        result = "Summary text.\n```json\n" + json.dumps(so) + "\n```\nTrailing."
        envelope = json.dumps({"type": "result", "is_error": False, "result": result})
        out = wr.interpret(envelope, expected_item_id="CICS-RETURN")
        self.assertTrue(out.ok)
        self.assertEqual(out.outcome, "success")

    def test_interpret_recovered_but_invalid_fails_closed(self):
        # recovered object lacks required fields -> validation fails -> not ok
        result = "```json\n{\"unrelated\": 1}\n```"
        envelope = json.dumps({"type": "result", "is_error": False, "result": result})
        out = wr.interpret(envelope, expected_item_id="CICS-RETURN")
        self.assertFalse(out.ok)


if __name__ == "__main__":
    unittest.main()
