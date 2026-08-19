import importlib.util
import tempfile
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[3]
SCRIPT = ROOT / "tools/quality-loop/checkstyle_javadoc_batch.py"
SPEC = importlib.util.spec_from_file_location("checkstyle_javadoc_batch", SCRIPT)
BATCH = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(BATCH)


class CheckstyleJavadocBatchTests(unittest.TestCase):
    def test_generates_semantic_summaries(self):
        self.assertEqual(BATCH.method_summary("getAccountName"),
                         "Returns the account name.")
        self.assertEqual(BATCH.method_summary("setAccountName"),
                         "Sets the account name.")
        self.assertEqual(BATCH.type_summary("AccountStore", "public interface AccountStore"),
                         "Defines the contract for account store.")

    def test_inserts_before_annotation_block(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "Example.java"
            path.write_text(
                "class Example {\n    @Override\n    public String toString() { return \"x\"; }\n}\n",
                encoding="latin1",
            )
            result = BATCH.apply_batch(
                {path: {2: (BATCH.METHOD_RULE, "toString")}}, apply=True
            )
            rendered = path.read_text(encoding="latin1")
            self.assertIn("    /** Returns a string representation of this value. */\n"
                          "    @Override", rendered)
            self.assertEqual(result["inserted"], 1)

    def test_relocates_functional_interface_method_report(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "Setter.java"
            path.write_text(
                "@FunctionalInterface\npublic interface Setter {\n"
                "    void setValue(String value);\n}\n",
                encoding="latin1",
            )
            BATCH.apply_batch(
                {path: {1: (BATCH.METHOD_RULE, "setValue")}}, apply=True
            )
            self.assertIn(
                "    /** Sets the value. */\n    void setValue",
                path.read_text(encoding="latin1"),
            )


if __name__ == "__main__":
    unittest.main()
