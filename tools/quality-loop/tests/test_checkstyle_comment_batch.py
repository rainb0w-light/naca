import importlib.util
import tempfile
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parents[3]
SCRIPT = ROOT / "tools/quality-loop/checkstyle_comment_batch.py"
SPEC = importlib.util.spec_from_file_location("checkstyle_comment_batch", SCRIPT)
BATCH = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(BATCH)


class CheckstyleCommentBatchTests(unittest.TestCase):
    def test_wraps_line_and_javadoc_comments(self):
        line = "    // " + "word " * 40
        wrapped = BATCH.wrap_comment_line(line, maximum=60)
        self.assertGreater(len(wrapped), 1)
        self.assertTrue(all(item.startswith("    // ") for item in wrapped))
        self.assertTrue(all(len(item) <= 60 for item in wrapped))

        javadoc = "     * " + "documentation " * 20
        wrapped = BATCH.wrap_comment_line(javadoc, maximum=72)
        self.assertTrue(all(item.startswith("     * ") for item in wrapped))
        self.assertTrue(all(len(item) <= 72 for item in wrapped))

        continuation = BATCH.wrap_comment_line(
            " *     " + "continued " * 20, maximum=72
        )
        self.assertTrue(all(item.startswith(" *     ") for item in continuation))

    def test_rejects_code_trailing_comments_and_long_tokens(self):
        self.assertEqual(BATCH.wrap_comment_line("call(); // trailing " + "x " * 80), [])
        self.assertEqual(BATCH.wrap_comment_line("    /* inline block */"), [])
        self.assertEqual(BATCH.wrap_comment_line("// " + "x" * 200), [])

    def test_caps_pathological_comment_indentation(self):
        wrapped = BATCH.wrap_comment_line("\t" * 31 + "// params", maximum=140)
        self.assertEqual(wrapped, [" " * 40 + "// params"])

    def test_fixes_javadoc_continuation_without_touching_code(self):
        self.assertEqual(
            BATCH.fix_javadoc_continuation("\t * description"),
            "\t *     description",
        )
        self.assertIsNone(BATCH.fix_javadoc_continuation("call(); // description"))
        self.assertIsNone(BATCH.fix_javadoc_continuation("\t */"))

    def test_repairs_repeated_javadoc_parse_patterns(self):
        self.assertEqual(
            BATCH.repair_javadoc_parse_line(" *     @ see Class Cond"),
            " * @see Class Cond",
        )
        repaired = BATCH.repair_javadoc_parse_line(
            " *     @see https://example.test/path for details"
        )
        self.assertIn('<a href="https://example.test/path">', repaired)
        self.assertEqual(
            BATCH.repair_javadoc_parse_line(" *     @author Name <name@example.test>"),
            " * @author Name &lt;name@example.test&gt;",
        )
        self.assertIn(
            "{@link #grantWriteAccessToUsersOrGroups} and",
            BATCH.repair_javadoc_document(
                "{@link #grantWriteAccessToUsersOrGroups and next"
            ),
        )

    def test_splits_only_real_trailing_comments(self):
        line = '    call("http://example.test"); // explains the call'
        self.assertEqual(
            BATCH.split_trailing_comment_line(line, maximum=80),
            ["    // explains the call", '    call("http://example.test");'],
        )
        self.assertIsNone(BATCH.trailing_comment_index('String url = "http://x";'))
        self.assertEqual(BATCH.split_trailing_comment_line("// only a comment"), [])
        too_long = "    " + "method()." * 30 + "run(); // explanation"
        self.assertEqual(BATCH.split_trailing_comment_line(too_long), [])

    def test_applies_only_reported_comment_findings(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = root / "Sample.java"
            source.write_text(
                "class Sample {\n"
                "    // " + "reported " * 30 + "\n"
                "    // " + "unreported " * 30 + "\n"
                "}\n",
                encoding="latin1",
            )
            report = root / "main.xml"
            checkstyle = ET.Element("checkstyle")
            file_element = ET.SubElement(checkstyle, "file", name=str(source))
            ET.SubElement(
                file_element,
                "error",
                line="2",
                source=BATCH.LINE_LENGTH_SOURCE,
            )
            ET.ElementTree(checkstyle).write(report)

            targets = BATCH.collect_targets(root, [report])
            result = BATCH.apply_batch(targets, maximum=60, apply=True)
            rendered = source.read_text(encoding="latin1")
            self.assertEqual(result["wrapped"], 1)
            self.assertEqual(result["filesChanged"], 1)
            self.assertGreater(rendered.count("// reported"), 1)
            self.assertEqual(rendered.count("// unreported"), 1)


if __name__ == "__main__":
    unittest.main()
