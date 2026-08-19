import importlib.util
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[3]
SCRIPT = ROOT / "tools/quality-loop/checkstyle_line_wrap_batch.py"
SPEC = importlib.util.spec_from_file_location("checkstyle_line_wrap_batch", SCRIPT)
BATCH = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(BATCH)


class CheckstyleLineWrapBatchTests(unittest.TestCase):
    def test_wraps_method_declaration_and_preserves_generics(self):
        line = (
            "    public void consume(Map<String, Integer> values, "
            "String description, boolean enabled)"
        )
        self.assertEqual(
            BATCH.wrap_argument_list(line, maximum=80),
            [
                "    public void consume(",
                "        Map<String, Integer> values,",
                "        String description,",
                "        boolean enabled)",
            ],
        )

    def test_ignores_commas_in_literals_and_nested_calls(self):
        line = '        result = call("a,b", nested(first, second), finalValue);'
        self.assertEqual(
            BATCH.wrap_argument_list(line, maximum=60),
            [
                "        result = call(",
                '            "a,b",',
                "            nested(first, second),",
                "            finalValue);",
            ],
        )

    def test_skips_unbalanced_or_unsplittable_lines(self):
        self.assertEqual(BATCH.wrap_argument_list("call(one, two;"), [])
        self.assertEqual(BATCH.wrap_argument_list("call(singleValue);"), [])
        self.assertEqual(
            BATCH.wrap_argument_list('call("' + "x" * 200 + '", value);'),
            [],
        )

    def test_keeps_closing_suffix_on_a_separate_line_when_needed(self):
        line = "    public Result call(String first, String second) throws VeryLongCheckedException"
        wrapped = BATCH.wrap_argument_list(line, maximum=50)
        self.assertEqual(wrapped[-1], "    ) throws VeryLongCheckedException")
        self.assertTrue(all(len(item) <= 50 for item in wrapped))

    def test_wraps_boolean_and_concatenation_chains(self):
        boolean = "    if (firstCondition && secondCondition && thirdCondition)"
        self.assertEqual(
            BATCH.wrap_operators(boolean, {"&&", "||"}, maximum=45),
            [
                "    if (firstCondition && secondCondition",
                "        && thirdCondition)",
            ],
        )
        concatenation = '    String value = "prefix" + first + second + third;'
        wrapped = BATCH.wrap_operators(concatenation, {"+"}, maximum=42)
        self.assertEqual(wrapped[1], "        + second + third;")

    def test_operators_inside_literals_and_increment_are_ignored(self):
        line = '    String value = "a+b" + count++;'
        self.assertEqual(BATCH.operator_positions(line, {"+"}), [(25, "+")])

    def test_wraps_operator_chain_that_continues_a_prior_line(self):
        line = "        + first + secondValue + thirdValue"
        self.assertEqual(
            BATCH.wrap_continuation_operators(line, {"+"}, maximum=30),
            ["        + first + secondValue", "        + thirdValue"],
        )

    def test_splits_multiple_statements_but_not_for_headers(self):
        line = "        values[0]=1;values[1]=2;values[2]=3;"
        self.assertEqual(
            BATCH.split_semicolon_statements(line, maximum=40),
            [
                "        values[0]=1;",
                "        values[1]=2;",
                "        values[2]=3;",
            ],
        )
        self.assertEqual(
            BATCH.split_semicolon_statements(
                "for (int index=0; index<10; index++) work();"
            ),
            [],
        )

    def test_splits_long_string_without_changing_its_content(self):
        line = '    String message = "one two three four five six seven";'
        wrapped = BATCH.wrap_long_string_literal(line, maximum=42)
        self.assertGreater(len(wrapped), 1)
        literals = []
        for item in wrapped:
            literals.extend(
                item[start + 1 : end - 1]
                for start, end in BATCH.string_literal_spans(item)
            )
        self.assertEqual("".join(literals), "one two three four five six seven")
        self.assertTrue(all(len(item) <= 42 for item in wrapped))
        boundary = '    value = "' + ("word " * 30) + 'tail";'
        boundary_wrapped = BATCH.wrap_long_string_literal(boundary, maximum=40)
        self.assertTrue(boundary_wrapped)
        self.assertTrue(all(len(item) <= 40 for item in boundary_wrapped))

    def test_expands_inline_method_body(self):
        line = "    public void setValue(String value) { this.value = value; }"
        self.assertEqual(
            BATCH.split_inline_method_body(line),
            [
                "    public void setValue(String value)",
                "    {",
                "        this.value = value;",
                "    }",
            ],
        )

    def test_splits_assignment_and_continued_comma_sequence(self):
        assignment = "    VeryLongType value = new VeryLongType();"
        self.assertEqual(
            BATCH.split_assignment(assignment, maximum=35),
            ["    VeryLongType value", "            = new VeryLongType();"],
        )
        continuation = "        String first, String second, String third)"
        self.assertEqual(
            BATCH.split_comma_sequence(continuation, maximum=30),
            [
                "        String first,",
                "        String second,",
                "        String third)",
            ],
        )
        commented = "    LongType value = new LongType(); // explanation"
        self.assertEqual(
            BATCH.split_commented_assignment(commented, maximum=45),
            [
                "    // explanation",
                "    LongType value",
                "            = new LongType();",
            ],
        )

    def test_wraps_only_block_comment_text(self):
        source = "    /* first comment line\n    second comment line */\n    int value;\n"
        self.assertEqual(BATCH.block_comment_only_lines(source), {0, 1})
        wrapped = BATCH.wrap_block_comment(
            "    /* one two three four five six seven */", maximum=25
        )
        self.assertGreater(len(wrapped), 1)


if __name__ == "__main__":
    unittest.main()
