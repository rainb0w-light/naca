import importlib.util
import tempfile
import unittest
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[3]
SCRIPT = ROOT / "tools/quality-loop/checkstyle_star_import_batch.py"
SPEC = importlib.util.spec_from_file_location("checkstyle_star_import_batch", SCRIPT)
BATCH = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(BATCH)


class CheckstyleStarImportBatchTests(unittest.TestCase):
    def test_masks_literals_comments_and_finds_unqualified_types(self):
        source = '''
            import sample.*;
            class Demo {
                Actual value;
                String text = "IgnoredType";
                // CommentType ignored;
                Outer.Nested nested;
            }
        '''
        self.assertEqual(
            BATCH.referenced_types(source), {"Actual", "String", "Outer", "Demo"}
        )

    def test_indexes_only_top_level_archive_types(self):
        with tempfile.TemporaryDirectory() as directory:
            archive = Path(directory) / "types.jar"
            with zipfile.ZipFile(archive, "w") as jar:
                jar.writestr("sample/Actual.class", b"")
                jar.writestr("sample/Actual$Nested.class", b"")
                jar.writestr("other/Other.class", b"")
            index = {}
            from collections import defaultdict
            typed = defaultdict(set, index)
            BATCH.add_archive_types(typed, {"sample"}, [archive])
            self.assertEqual(typed["sample"], {"Actual"})

    def test_expands_non_static_and_defers_static_wildcard(self):
        with tempfile.TemporaryDirectory() as directory:
            source = Path(directory) / "Demo.java"
            source.write_text(
                "import sample.*;\n"
                "import static checks.Assertions.*;\n"
                "class Demo { Actual value; }\n",
                encoding="latin1",
            )
            result = BATCH.apply_batch(
                {source: {0, 1}}, {"sample": {"Actual", "Unused"}}, apply=True
            )
            rendered = source.read_text(encoding="latin1")
            self.assertIn("import sample.Actual;", rendered)
            self.assertIn("import static checks.Assertions.*;", rendered)
            self.assertEqual(result["staticDeferred"], 1)

    def test_finds_packages_even_when_report_lines_are_stale(self):
        with tempfile.TemporaryDirectory() as directory:
            source = Path(directory) / "Demo.java"
            source.write_text(
                "// an earlier batch inserted this line\nimport sample.*;\nclass Demo {}\n",
                encoding="latin1",
            )
            self.assertEqual(BATCH.imported_packages({source: {0}}), {"sample"})


if __name__ == "__main__":
    unittest.main()
