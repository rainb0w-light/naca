import importlib.util
import tempfile
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parents[3]
SCRIPT = ROOT / "tools/quality-loop/checkstyle_tab_batch.py"
SPEC = importlib.util.spec_from_file_location("checkstyle_tab_batch", SCRIPT)
BATCH = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(BATCH)


class CheckstyleTabBatchTests(unittest.TestCase):
    def test_expands_code_and_comment_tabs_at_four_column_stops(self):
        source = "\tint value;\n  \t// note\ttext\n"
        rendered, metrics = BATCH.transform_tabs(source)
        self.assertEqual(rendered, "    int value;\n    // note text\n")
        self.assertEqual(metrics["expanded"], 3)
        self.assertNotIn("\t", rendered)

    def test_escapes_literal_tabs_without_changing_runtime_character(self):
        source = 'String value = "left\tright"; char tab = \'\t\';\n'
        rendered, metrics = BATCH.transform_tabs(source)
        self.assertEqual(
            rendered,
            'String value = "left\\tright"; char tab = \'\\t\';\n',
        )
        self.assertEqual(metrics["escapedInString"], 1)
        self.assertEqual(metrics["escapedInChar"], 1)

    def test_fails_closed_for_text_block_tabs(self):
        with self.assertRaisesRegex(ValueError, "text block"):
            BATCH.transform_tabs('String value = """\n\ttext\n""";\n')

    def test_trims_trailing_spaces_but_preserves_text_block_content(self):
        source = 'int value;   \nString text = """\ncontent   \n""";   \n'
        rendered, metrics = BATCH.transform_tabs(source)
        self.assertEqual(
            rendered,
            'int value;\nString text = """\ncontent   \n""";\n',
        )
        self.assertEqual(metrics["trailingWhitespaceLines"], 2)

    def test_collects_only_reported_files(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory).resolve()
            included = root / "Included.java"
            excluded = root / "Excluded.java"
            included.write_text("\tclass Included {}\n", encoding="latin1")
            excluded.write_text("\tclass Excluded {}\n", encoding="latin1")
            report = root / "main.xml"
            document = ET.Element("checkstyle")
            file_element = ET.SubElement(document, "file", name=str(included))
            ET.SubElement(
                file_element,
                "error",
                line="1",
                source=BATCH.FILE_TAB_SOURCE,
            )
            ET.ElementTree(document).write(report)
            self.assertEqual(BATCH.collect_files(root, [report]), [included])


if __name__ == "__main__":
    unittest.main()
