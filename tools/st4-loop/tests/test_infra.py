import copy
import importlib.util
import json
import os
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

from st4loop import debt, gitutil
from tests.util import TESTS_DIR

REPO_ROOT = TESTS_DIR.parents[2]
VERIFY_SH = TESTS_DIR.parent / "verify-task.sh"
WORKER_MD = REPO_ROOT / ".claude" / "st4-loop" / "worker.md"
SYNC_SCRIPT = TESTS_DIR.parent / "sync_ledger_inventory.py"


def load_sync_module():
    name = "st4_loop_sync_inventory_test_module"
    spec = importlib.util.spec_from_file_location(name, SYNC_SCRIPT)
    module = importlib.util.module_from_spec(spec)
    sys.modules[name] = module
    spec.loader.exec_module(module)
    return module


class InventorySynchronizationTest(unittest.TestCase):
    def test_deleting_earlier_backend_does_not_renumber_later_entries(self):
        sync = load_sync_module()
        first = sync.Backend(
            area="SQL",
            class_name="CJavaFirst",
            backend_fqn="generate.java.SQL.CJavaFirst",
            semantic_fqn="semantic.SQL.CEntityFirst",
            source_path="naca-trans/src/main/java/generate/java/SQL/CJavaFirst.java",
        )
        second = sync.Backend(
            area="SQL",
            class_name="CJavaSecond",
            backend_fqn="generate.java.SQL.CJavaSecond",
            semantic_fqn="semantic.SQL.CEntitySecond",
            source_path="naca-trans/src/main/java/generate/java/SQL/CJavaSecond.java",
        )
        initial = sync.synchronize({"entries": []}, [first, second])
        before = sync.rendered(initial)

        # During worker verification the source is already deleted, while the
        # controller-owned ledger still contains the non-terminal entry.
        in_flight = sync.synchronize(copy.deepcopy(initial), [second])

        self.assertEqual(sync.rendered(in_flight), before)
        self.assertEqual(
            next(e for e in in_flight["entries"] if e["id"].endswith("CJAVASECOND"))[
                "priority"
            ],
            101,
        )

    def test_forms_and_fpac_backends_get_scope_specific_fields(self):
        sync = load_sync_module()
        forms = sync.Backend(
            area="forms",
            class_name="CJavaForm",
            backend_fqn="generate.java.forms.CJavaForm",
            semantic_fqn="semantic.forms.CEntityResourceForm",
            source_path="naca-trans/src/main/java/generate/java/forms/CJavaForm.java",
        )
        fpac = sync.Backend(
            area="fpac",
            class_name="CFPacJavaAssign",
            backend_fqn="generate.fpacjava.CFPacJavaAssign",
            semantic_fqn="semantic.Verbs.CEntityAssign",
            source_path="naca-trans/src/main/java/generate/fpacjava/CFPacJavaAssign.java",
        )
        cics = sync.Backend(
            area="CICS",
            class_name="CJavaReturn",
            backend_fqn="generate.java.CICS.CJavaReturn",
            semantic_fqn="semantic.CICS.CEntityReturn",
            source_path="naca-trans/src/main/java/generate/java/CICS/CJavaReturn.java",
        )
        data = sync.synchronize({"entries": []}, [forms, fpac, cics])
        by_id = {e["id"]: e for e in data["entries"]}

        bms_entry = by_id["BMS-BACKEND-CJAVAFORM"]
        self.assertEqual("BMS_ARTIFACT", bms_entry["scope"])
        self.assertEqual({"bmsDirectBackends": -1}, bms_entry["expectedDebtDelta"])
        self.assertIn("BmsFormsDirectBackendInventoryTest",
                      bms_entry["verification"][0])

        fpac_entry = by_id["FPAC-BACKEND-CFPACJAVAASSIGN"]
        self.assertEqual("FPAC", fpac_entry["scope"])
        self.assertEqual({"fpacDirectBackends": -1}, fpac_entry["expectedDebtDelta"])
        self.assertIn("FPacDirectBackendInventoryTest",
                      fpac_entry["verification"][0])
        # FPAC queue sits after the BMS priority band
        self.assertGreater(fpac_entry["priority"], bms_entry["priority"])

        cics_entry = by_id["BACKEND-CICS-CJAVARETURN"]
        self.assertEqual("EMBEDDED_CICS", cics_entry["scope"])
        self.assertEqual({"directBackends": -1}, cics_entry["expectedDebtDelta"])
        self.assertIn("architecture.DirectBackendInventoryTest",
                      cics_entry["verification"][0])

    def test_scoped_architecture_report_becomes_ready_ledger_items(self):
        sync = load_sync_module()
        with tempfile.TemporaryDirectory() as td:
            root = Path(td)
            report = (
                root
                / "naca-trans/build/test-results/finalArchitectureCheck"
                / "TEST-architecture.FinalArchitectureContractTest.xml"
            )
            report.parent.mkdir(parents=True)
            report.write_text(
                """<testsuite failures="3">
                <testcase name="src/main/java/semantic/CICS/CEntityOne.java">
                  <failure message="semantic class contract violations (1):"/>
                </testcase>
                <testcase name="src/main/java/semantic/SQL/CEntityTwo.java">
                  <failure message="semantic class contract violations (2):"/>
                </testcase>
                <testcase name="src/main/java/semantic/forms/CEntityBms.java">
                  <failure message="semantic class contract violations (1):"/>
                </testcase>
                </testsuite>""",
                encoding="utf-8",
            )

            debts = sync.discover_architecture_debt(root)
            data = sync.synchronize_architecture({"entries": []}, debts)

            self.assertEqual(2, len(data["entries"]))
            first, second = data["entries"]
            self.assertEqual("EMBEDDED_CICS", first["scope"])
            self.assertEqual({"failures": -1}, first["expectedDebtDelta"])
            self.assertEqual("EMBEDDED_SQL", second["scope"])
            self.assertNotIn("BMS", "\n".join(e["id"] for e in data["entries"]))


class GitEncodingTest(unittest.TestCase):
    def test_diff_tolerates_legacy_iso_8859_1_source(self):
        with tempfile.TemporaryDirectory() as td:
            root = Path(td)
            subprocess.run(["git", "init", "-q"], cwd=root, check=True)
            subprocess.run(
                ["git", "config", "user.email", "loop@example.invalid"],
                cwd=root,
                check=True,
            )
            subprocess.run(
                ["git", "config", "user.name", "Loop Test"],
                cwd=root,
                check=True,
            )
            source = root / "Legacy.java"
            source.write_bytes(b"// cr\\xfb legacy source\\nclass Legacy {}\\n")
            subprocess.run(["git", "add", "Legacy.java"], cwd=root, check=True)
            subprocess.run(["git", "commit", "-qm", "base"], cwd=root, check=True)
            source.unlink()

            diff = gitutil.diff(root)

            self.assertIn("Legacy.java", diff)
            # Git may quote the byte as "\\xfb"; the contract is that legacy
            # source content never crashes the controller's text pipeline.
            self.assertTrue("\ufffd" in diff or "\\xfb" in diff, diff)


class VerifyScriptPortabilityTest(unittest.TestCase):
    def test_no_bash4_only_constructs(self):
        text = VERIFY_SH.read_text(encoding="utf-8")
        # Ignore comment lines: only actual command usage is a portability bug.
        code = "\n".join(
            line for line in text.splitlines() if not line.lstrip().startswith("#"))
        self.assertNotIn("mapfile", code, "mapfile is Bash 4+; macOS ships Bash 3.2")
        self.assertNotIn("readarray", code, "readarray is Bash 4+; macOS ships Bash 3.2")

    def test_item_verification_commands_run_under_skip_gradle(self):
        # Proves the while-read item-command loop executes (Bash-3.2 portable path).
        with tempfile.TemporaryDirectory() as td:
            marker = Path(td) / "marker.txt"
            ledger = {
                "entries": [{"id": "TEST-ITEM",
                             "verification": [f"touch {marker}"]}]
            }
            ledger_path = Path(td) / "ledger.json"
            ledger_path.write_text(json.dumps(ledger), encoding="utf-8")
            env = dict(os.environ, ST4_SKIP_GRADLE="1", ST4_LEDGER=str(ledger_path))
            result = subprocess.run(
                ["bash", str(VERIFY_SH), "TEST-ITEM"],
                capture_output=True, text=True, env=env)
            self.assertEqual(result.returncode, 0,
                             f"stdout={result.stdout}\nstderr={result.stderr}")
            self.assertIn("VERIFY-PASS", result.stdout)
            self.assertTrue(marker.exists(), "item verification command did not run")

    def test_failing_item_command_fails_the_verifier(self):
        with tempfile.TemporaryDirectory() as td:
            ledger = {"entries": [{"id": "TEST-ITEM", "verification": ["exit 3"]}]}
            ledger_path = Path(td) / "ledger.json"
            ledger_path.write_text(json.dumps(ledger), encoding="utf-8")
            env = dict(os.environ, ST4_SKIP_GRADLE="1", ST4_LEDGER=str(ledger_path))
            result = subprocess.run(
                ["bash", str(VERIFY_SH), "TEST-ITEM"],
                capture_output=True, text=True, env=env)
            self.assertNotEqual(result.returncode, 0)
            self.assertIn("VERIFY-FAIL", result.stdout)

    def test_empty_item_verification_is_valid_under_nounset(self):
        # Regression: macOS Bash 3.2 treats "${EMPTY_ARRAY[@]}" as an unbound
        # variable under `set -u` unless the expansion is guarded.
        with tempfile.TemporaryDirectory() as td:
            ledger = {"entries": [{"id": "TEST-ITEM", "verification": []}]}
            ledger_path = Path(td) / "ledger.json"
            ledger_path.write_text(json.dumps(ledger), encoding="utf-8")
            env = dict(os.environ, ST4_SKIP_GRADLE="1", ST4_LEDGER=str(ledger_path))
            result = subprocess.run(
                ["/bin/bash", str(VERIFY_SH), "TEST-ITEM"],
                capture_output=True, text=True, env=env)
            self.assertEqual(result.returncode, 0,
                             f"stdout={result.stdout}\nstderr={result.stderr}")
            self.assertIn("VERIFY-PASS", result.stdout)


class WorkerPromptGateTest(unittest.TestCase):
    def test_requires_focused_contract_gates_not_full_module(self):
        text = WORKER_MD.read_text(encoding="utf-8")
        self.assertIn("LedgerConsistencyTest", text)
        self.assertIn("RuntimeContractTest", text)
        self.assertIn(
            ':naca-cloud-native:test --tests "*LedgerConsistencyTest" '
            '--tests "*RuntimeContractTest"',
            text,
        )
        # documents the pre-existing failure so success is not made impossible
        self.assertIn("pre-existing", text)
        self.assertIn("Do **not** require the full", text)
        # the old "full module must be green" instruction is gone
        self.assertNotIn("ledger consistency stays green", text)

    def test_verifier_runs_runtime_contract_gate(self):
        text = VERIFY_SH.read_text(encoding="utf-8")
        self.assertIn('--tests "*LedgerConsistencyTest"', text)
        self.assertIn('--tests "*RuntimeContractTest"', text)


class DebtScannerTest(unittest.TestCase):
    def _tree(self, td):
        root = Path(td)
        base = root / "naca-trans/src/main/java/generate/java"
        (base / "verbs").mkdir(parents=True)
        (base / "CICS").mkdir(parents=True)
        (base / "verbs" / "A.java").write_text("class A extends CEntityX {}\n")
        (base / "CICS" / "B.java").write_text("class B extends CBaseActionEntity {}\n")
        (base / "C.java").write_text("class C extends CDataEntity {}\n")
        (base / "D.java").write_text("class D extends SomethingElse {}\n")
        (base / "E.java").write_text("class E { /* no extends */ }\n")
        return root

    def test_counts_only_direct_semantic_subclasses(self):
        with tempfile.TemporaryDirectory() as td:
            root = self._tree(td)
            self.assertEqual(debt.measure_direct_backends(root), 3)

    def test_missing_tree_is_zero(self):
        with tempfile.TemporaryDirectory() as td:
            self.assertEqual(debt.measure_direct_backends(Path(td)), 0)

    def test_scope_measurer_each_own_inventory(self):
        with tempfile.TemporaryDirectory() as td:
            root = Path(td)
            # COBOL inventory: generate/java minus forms/
            cobol = root / "naca-trans/src/main/java/generate/java"
            (cobol / "forms").mkdir(parents=True)
            (cobol / "verbs").mkdir(parents=True)
            (cobol / "verbs" / "V.java").write_text(
                "class V extends CEntityX {}\n")
            # BMS inventory: forms/ incl. the CResourceStrings subclass
            (cobol / "forms" / "F.java").write_text(
                "class F extends CEntityForm {}\n")
            (cobol / "forms" / "R.java").write_text(
                "class R extends CResourceStrings {}\n")
            (cobol / "forms" / "X.java").write_text(
                "class X extends SomethingElse {}\n")
            # FPac inventory: generate/fpacjava incl. the wrapped declaration
            fpac = root / "naca-trans/src/main/java/generate/fpacjava"
            fpac.mkdir(parents=True)
            (fpac / "P.java").write_text("class P extends CEntityAssign {}\n")
            (fpac / "S.java").write_text(
                "class S extends\n\tCSubStringAttributReference {}\n")

            self.assertEqual(debt.measure_direct_backends(root), 1)
            self.assertEqual(debt.measure_bms_direct_backends(root), 2)
            self.assertEqual(debt.measure_fpac_direct_backends(root), 2)
            self.assertEqual(debt.measure_for_scope(root, "EMBEDDED_CICS"), 1)
            self.assertEqual(debt.measure_for_scope(root, "BMS_ARTIFACT"), 2)
            self.assertEqual(debt.measure_for_scope(root, "FPAC"), 2)

    def test_scope_counter_keys(self):
        self.assertEqual(debt.counter_key_for_scope("COBOL_CORE"), "directBackends")
        self.assertEqual(debt.counter_key_for_scope("EMBEDDED_SQL"), "directBackends")
        self.assertEqual(debt.counter_key_for_scope("EMBEDDED_CICS"), "directBackends")
        self.assertEqual(debt.counter_key_for_scope("BMS_ARTIFACT"),
                         "bmsDirectBackends")
        self.assertEqual(debt.counter_key_for_scope("FPAC"), "fpacDirectBackends")

    def test_checked_in_baseline_is_scope_aware(self):
        with tempfile.TemporaryDirectory() as td:
            root = Path(td)
            arch = root / "naca-trans/src/test/java/architecture"
            arch.mkdir(parents=True)
            (arch / "DirectBackendInventoryTest.java").write_text(
                "private static final int DIRECT_BACKEND_TOTAL_BASELINE = 0;\n")
            (arch / "BmsFormsDirectBackendInventoryTest.java").write_text(
                "private static final int BMS_DIRECT_BACKEND_TOTAL_BASELINE = 34;\n")
            (arch / "FPacDirectBackendInventoryTest.java").write_text(
                "private static final int FPAC_DIRECT_BACKEND_TOTAL_BASELINE = 43;\n")
            self.assertEqual(debt.read_checked_in_baseline(root), 0)
            self.assertEqual(debt.read_checked_in_baseline(root, "COBOL_CORE"), 0)
            self.assertEqual(debt.read_checked_in_baseline(root, "BMS_ARTIFACT"), 34)
            self.assertEqual(debt.read_checked_in_baseline(root, "FPAC"), 43)
            # minimal fixture (no test files) -> None, not a crash
            with tempfile.TemporaryDirectory() as td2:
                self.assertIsNone(debt.read_checked_in_baseline(Path(td2), "FPAC"))

    def test_rule_matches_the_inventory_test_regex(self):
        # Same rule as architecture.DirectBackendInventoryTest.
        self.assertIsNotNone(debt.DIRECT_SEMANTIC_SUBCLASS.search(" extends CEntity"))
        self.assertIsNotNone(debt.DIRECT_SEMANTIC_SUBCLASS.search(" extends CBaseActionEntity"))
        self.assertIsNotNone(debt.DIRECT_SEMANTIC_SUBCLASS.search(" extends CDataEntity"))
        self.assertIsNone(debt.DIRECT_SEMANTIC_SUBCLASS.search(" extends CEntityFoo".replace("CEntityFoo", "Other")))


if __name__ == "__main__":
    unittest.main()
