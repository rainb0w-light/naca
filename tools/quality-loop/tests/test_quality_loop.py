import json
import importlib.util
import os
import re
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
CLI = ROOT / "tools/quality-loop/quality_loop.py"
VERIFY = ROOT / "tools/quality-loop/verify_task.py"
METRICS_PATH = ROOT / "tools/quality-loop/quality_metrics.py"
SPEC = importlib.util.spec_from_file_location("quality_metrics", METRICS_PATH)
METRICS = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(METRICS)
CARD_SPEC = importlib.util.spec_from_file_location("carddemo_preflight", ROOT / "tools/quality-loop/carddemo_preflight.py")
CARD = importlib.util.module_from_spec(CARD_SPEC)
CARD_SPEC.loader.exec_module(CARD)
PROBE_SPEC = importlib.util.spec_from_file_location("carddemo_probe", ROOT / "tools/quality-loop/carddemo_probe.py")
PROBE = importlib.util.module_from_spec(PROBE_SPEC)
PROBE_SPEC.loader.exec_module(PROBE)


class QualityLoopTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)

    def cli(self, *args):
        return subprocess.run(["python3", str(CLI), *map(str, args)], text=True, capture_output=True)

    def ledger(self, tasks):
        path = Path(self.temp.name) / "ledger.json"
        path.write_text(json.dumps({"version": 1, "tasks": tasks}))
        return path

    def task(self, **updates):
        task = {"id": "A", "objective": "test objective", "stream": "s", "priority": 1,
                "dependencies": [], "allowedPaths": ["ok/**", "ledger.json"], "forbiddenPaths": [],
                "authorizedProtectedPaths": [], "maxFiles": 5, "verification": [], "state": "READY", "attempts": 0}
        task.update(updates)
        return task

    def test_duplicate_and_schema_validation(self):
        self.assertNotEqual(self.cli("validate", self.ledger([self.task(), self.task()])).returncode, 0)
        self.assertNotEqual(self.cli("validate", self.ledger([self.task(dependencies=["MISSING"])] )).returncode, 0)
        missing = {key: value for key, value in self.task().items() if key != "objective"}
        self.assertNotEqual(self.cli("validate", self.ledger([missing])).returncode, 0)
        self.assertNotEqual(self.cli("validate", self.ledger([self.task(priority="high")])).returncode, 0)

    def test_deterministic_next(self):
        tasks = [self.task(id="B", priority=2), self.task(id="A", priority=2)]
        self.assertEqual(json.loads(self.cli("next", self.ledger(tasks)).stdout)["id"], "A")

    def test_dependency_must_be_verified(self):
        tasks = [self.task(id="A", state="RUNNING"), self.task(id="B", dependencies=["A"], priority=0)]
        self.assertEqual(json.loads(self.cli("next", self.ledger(tasks)).stdout)["status"], "NONE")

    def test_transition_and_attempts(self):
        path = self.ledger([self.task()])
        for state in ("RUNNING", "REJECTED", "READY", "RUNNING"):
            self.assertEqual(self.cli("transition", "A", state, path).returncode, 0)
        self.assertEqual(json.loads(path.read_text())["tasks"][0]["attempts"], 2)
        self.assertNotEqual(self.cli("transition", "A", "READY", path).returncode, 0)

    def test_no_task(self):
        self.assertEqual(json.loads(self.cli("next", self.ledger([self.task(state="VERIFIED")])).stdout)["status"], "NONE")

    def test_real_ledger_worker_control_boundaries(self):
        real_ledger = json.loads((ROOT / "docs/quality-governance/ledger.json").read_text())
        for task in real_ledger["tasks"]:
            if task["id"] != "QG-000" and task["state"] == "READY":
                self.assertIn("docs/quality-governance/ledger.json", task["forbiddenPaths"])
        worker = (ROOT / ".claude/quality-loop/worker.md").read_text()
        result_schema = worker.split("Return exactly one JSON object", 1)[1]
        self.assertIn("REVIEW|REJECTED|BLOCKED", result_schema)
        self.assertNotIn('state":"VERIFIED', result_schema)

    def git_repo(self):
        path = Path(tempfile.mkdtemp(dir=self.temp.name))
        subprocess.run(["git", "init", "-q"], cwd=path, check=True)
        subprocess.run(["git", "config", "user.email", "test@example.invalid"], cwd=path, check=True)
        subprocess.run(["git", "config", "user.name", "Test"], cwd=path, check=True)
        (path / "ok").mkdir()
        (path / "ok/a").write_text("x")
        subprocess.run(["git", "add", "."], cwd=path, check=True)
        subprocess.run(["git", "commit", "-qm", "base"], cwd=path, check=True)
        return path

    def verify(self, path, task):
        ledger = path / "ledger.json"
        ledger.write_text(json.dumps({"version": 1, "tasks": [task]}))
        subprocess.run(["git", "add", "."], cwd=path, check=True)
        subprocess.run(["git", "commit", "-qm", "change"], cwd=path, check=True)
        return subprocess.run(["python3", str(VERIFY), "A", "--base-ref", "HEAD~1", str(ledger)], cwd=path, text=True, capture_output=True)

    def test_verify_out_of_scope_and_command_failure(self):
        path = self.git_repo(); (path / "bad").write_text("x")
        result = self.verify(path, self.task(verification=["python3 -c 'raise SystemExit(2)'"], maxFiles=1))
        self.assertEqual(json.loads(result.stdout)["reason"], "paths")
        path = self.git_repo(); (path / "ok/b").write_text("x")
        result = self.verify(path, self.task(verification=["python3 -c 'raise SystemExit(2)'"], maxFiles=2))
        self.assertEqual(json.loads(result.stdout)["reason"], "verification")

    def test_verify_allowed_forbidden_and_maxfiles(self):
        path = self.git_repo(); (path / "ok/b").write_text("x")
        self.assertEqual(json.loads(self.verify(path, self.task(maxFiles=1)).stdout)["reason"], "maxFiles")
        path = self.git_repo(); (path / "ok/b").write_text("x")
        self.assertEqual(json.loads(self.verify(path, self.task(forbiddenPaths=["ok/**"])).stdout)["reason"], "paths")

    def test_protected_requires_explicit_authorization(self):
        path = self.git_repo(); (path / "docs").mkdir(); (path / "docs/project-quality-baseline.json").write_text("x")
        task = self.task(allowedPaths=["docs/**", "ledger.json"])
        self.assertEqual(json.loads(self.verify(path, task).stdout)["reason"], "paths")
        path = self.git_repo(); (path / "docs").mkdir(); (path / "docs/project-quality-baseline.json").write_text("x")
        task = self.task(allowedPaths=["docs/**", "ledger.json"], authorizedProtectedPaths=["docs/project-quality-baseline.json"])
        self.assertEqual(self.verify(path, task).returncode, 0)

    def test_verify_from_subdirectory_runs_root_verification(self):
        path = self.git_repo(); (path / "ok/b").write_text("x")
        (path / "root-marker").write_text("pass")
        task = self.task(allowedPaths=["ok/**", "ledger.json", "root-marker"], verification=["test -f root-marker"])
        ledger = path / "ledger.json"; ledger.write_text(json.dumps({"version": 1, "tasks": [task]}))
        subprocess.run(["git", "add", "."], cwd=path, check=True)
        subprocess.run(["git", "commit", "-qm", "change"], cwd=path, check=True)
        result = subprocess.run(["python3", str(VERIFY), "A", "--base-ref", "HEAD~1", str(ledger)], cwd=path / "ok", text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)

    def metric_fixture(self):
        root = Path(self.temp.name) / "fixture"
        root.mkdir()
        baseline = {"aggregateLineCoverageMinimum": 0.5, "cpdDuplications": 2,
                    "staticAnalysis": {"mod": {"checkstyleMain": 1, "pmdMain": 1,
                    "spotbugsMain": 1}}}
        (root / "docs").mkdir(); (root / "docs/project-quality-baseline.json").write_text(json.dumps(baseline))
        for report, body in (("checkstyle/main.xml", "<x:error/><x:error/>"),
                             ("pmd/main.xml", "<x:violation/>"), ("spotbugs/spotbugsMain.xml", "<x:BugInstance/>"),
                             ("../../reports/pmd/cpd.xml", "<x:duplication/><x:duplication/>")):
            path = root / "mod/build/reports" / report if report.startswith("../") is False else root / "build/reports/pmd/cpd.xml"
            path.parent.mkdir(parents=True, exist_ok=True); path.write_text(f"<root xmlns:x='urn:test'>{body}</root>")
        (root / "mod/build/reports/jacoco/test").mkdir(parents=True)
        (root / "mod/build/reports/jacoco/test/jacocoTestReport.xml").write_text('<root xmlns="urn:test"><counter type="LINE" missed="1" covered="1"/></root>')
        return root, baseline

    def test_metrics_collect_and_compare(self):
        root, baseline = self.metric_fixture()
        collected = METRICS.collect(root, baseline)
        self.assertEqual(collected["staticAnalysis"]["mod"]["checkstyleMain"], 2)
        result = METRICS.compare(collected, baseline)
        self.assertEqual(result["status"], "FAIL")
        self.assertTrue(result["regressions"])

    def test_metrics_fail_closed_and_improvements(self):
        root, baseline = self.metric_fixture()
        (root / "mod/build/reports/pmd/main.xml").write_text("<broken>")
        with self.assertRaises(ValueError): METRICS.collect(root, baseline)
        current = {"staticAnalysis": {"mod": {"checkstyleMain": 0, "pmdMain": 0, "spotbugsMain": 0}}, "cpdDuplications": 1, "aggregateLineCoverage": 0.75}
        self.assertEqual(METRICS.compare(current, baseline)["status"], "PASS")
        current["staticAnalysis"]["mod"].pop("pmdMain")
        self.assertEqual(METRICS.compare(current, baseline)["status"], "FAIL")

    def test_metrics_coverage_decline_alone_fails(self):
        _, baseline = self.metric_fixture()
        current = {"staticAnalysis": baseline["staticAnalysis"], "cpdDuplications": 2,
                   "aggregateLineCoverage": 0.49}
        self.assertEqual(METRICS.compare(current, baseline)["status"], "FAIL")

    def test_line_counter_uses_root_only(self):
        path = Path(self.temp.name) / "nested.xml"
        path.write_text('<report xmlns="urn:test"><counter type="LINE" missed="2" covered="8"/><package><counter type="LINE" missed="99" covered="1"/><class><counter type="LINE" missed="50" covered="1"/></class></package></report>')
        self.assertEqual(METRICS.line_counter(path), (2, 8))
        path.write_text('<report><counter type="LINE" missed="1" covered="1"/><counter type="LINE" missed="2" covered="2"/></report>')
        with self.assertRaises(ValueError): METRICS.line_counter(path)

    def test_metrics_cli_subdirectory_uses_explicit_baseline(self):
        root = Path(self.temp.name) / "cli-repo"
        (root / "docs").mkdir(parents=True)
        subprocess.run(["git", "init", "-q"], cwd=root, check=True)
        baseline = {"aggregateLineCoverageMinimum": 0.5, "cpdDuplications": 0, "staticAnalysis": {}}
        (root / "docs/project-quality-baseline.json").write_text(json.dumps(baseline))
        current = root / "current.json"
        current.write_text(json.dumps({"aggregateLineCoverage": 0.5, "cpdDuplications": 0, "staticAnalysis": {}}))
        external = root / "baseline.json"
        external.write_text(json.dumps(baseline))
        child = root / "child"; child.mkdir()
        result = subprocess.run(["python3", str(METRICS_PATH), "compare", str(current), "--baseline", str(external)], cwd=child, text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)

    def test_metrics_missing_report(self):
        root, baseline = self.metric_fixture()
        (root / "build/reports/pmd/cpd.xml").unlink()
        with self.assertRaises(ValueError): METRICS.collect(root, baseline)

    def test_carddemo_manifest_and_schema_boundaries(self):
        manifest = json.loads((ROOT / "docs/quality-governance/carddemo-corpus.json").read_text())
        CARD.validate(manifest)
        broken = json.loads(json.dumps(manifest)); broken["source"]["commit"] = "0" * 40
        with self.assertRaises(ValueError): CARD.validate(broken)
        broken = json.loads(json.dumps(manifest)); broken["candidates"][0]["path"] = "../escape.cbl"
        with self.assertRaises(ValueError): CARD.validate(broken)
        broken = json.loads(json.dumps(manifest)); broken["candidates"] = broken["candidates"][:1]
        with self.assertRaises(ValueError): CARD.validate(broken)
        for field, value in (("bytes", 0), ("lines", 0), ("copyDependencies", []), ("externalCalls", []), ("organization", ""), ("selectionReason", ""), ("expectedFirstStage", "")):
            broken = json.loads(json.dumps(manifest)); broken["candidates"][0][field] = value
            with self.subTest(field=field), self.assertRaises(ValueError): CARD.validate(broken)

    def test_carddemo_source_facts_and_checkout_commit(self):
        checkout = Path(self.temp.name) / "checkout"
        checkout.mkdir(); (checkout / "app/cbl").mkdir(parents=True)
        (checkout / "LICENSE").write_text("Apache License")
        sources = {"CBACT02C.cbl": "       COPY CVACT02Y.\n       CALL 'CEE3ABD'.\n", "CBCUS01C.cbl": "       COPY CVCUS01Y.\n       CALL 'CEE3ABD'.\n"}
        for name, content in sources.items():
            (checkout / "app/cbl" / name).write_text(content)
        subprocess.run(["git", "init", "-q"], cwd=checkout, check=True)
        subprocess.run(["git", "config", "user.email", "test@example.invalid"], cwd=checkout, check=True)
        subprocess.run(["git", "config", "user.name", "Test"], cwd=checkout, check=True)
        subprocess.run(["git", "add", "."], cwd=checkout, check=True)
        subprocess.run(["git", "commit", "-qm", "fixture"], cwd=checkout, check=True)
        commit = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=checkout, text=True).strip()
        license_bytes = (checkout / "LICENSE").read_bytes()
        manifest = {"schemaVersion": 1, "source": {"url": CARD.URL, "commit": commit, "license": {"spdx": "Apache-2.0", "path": "LICENSE", "sha256": __import__("hashlib").sha256(license_bytes).hexdigest()}}, "candidates": []}
        for name in sources:
            path = checkout / "app/cbl" / name
            facts = CARD.source_facts(path)
            manifest["candidates"].append({"path": f"app/cbl/{name}", **facts, "organization": "fixture", "selectionReason": "fixture", "expectedFirstStage": "parse"})
        original_pinned = CARD.PINNED
        CARD.PINNED = commit
        CARD.validate(manifest)
        CARD.verify(checkout, manifest)
        for label, mutate in (("wrong commit", lambda item: item["source"].update(commit="0" * 40)), ("license hash", lambda item: item["source"]["license"].update(sha256="0" * 64)), ("candidate sha", lambda item: item["candidates"][0].update(sha256="0" * 64)), ("candidate bytes", lambda item: item["candidates"][0].update(bytes=999)), ("candidate lines", lambda item: item["candidates"][0].update(lines=999)), ("COPY dependency", lambda item: item["candidates"][0].update(copyDependencies=["OTHER"])), ("CALL dependency", lambda item: item["candidates"][0].update(externalCalls=["OTHER"])), ("missing candidate", lambda item: item["candidates"].__setitem__(0, {**item["candidates"][0], "path": "app/cbl/MISSING.cbl"}))):
            broken = json.loads(json.dumps(manifest)); mutate(broken)
            with self.subTest(mismatch=label), self.assertRaises(ValueError): CARD.verify(checkout, broken)
        CARD.PINNED = original_pinned

    def test_carddemo_validate_manifest_from_subdirectory(self):
        result = subprocess.run(["python3", str(ROOT / "tools/quality-loop/carddemo_preflight.py"), "validate-manifest"], cwd=ROOT / "docs/quality-governance", text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)

    def test_probe_report_schema_and_failure_order(self):
        stages = [PROBE.stage("source-verification", "PASS", "source"), PROBE.stage("parse-transpile", "FAIL", "transpile", 1, "unsupported-or-parse"), PROBE.stage("generated-java", "NOT_RUN", "inspect"), PROBE.stage("javac", "NOT_RUN", "javac")]
        report = PROBE.report(stages, "parse-transpile")
        PROBE.validate(report)
        broken = json.loads(json.dumps(report)); broken["stages"][2]["status"] = "PASS"
        with self.assertRaises(ValueError): PROBE.validate(broken)
        broken = json.loads(json.dumps(report)); broken["stages"].reverse()
        with self.assertRaises(ValueError): PROBE.validate(broken)

    def test_probe_mocked_tool_failure_and_stage_reports(self):
        class Completed:
            def __init__(self, code, output=""):
                self.returncode = code; self.stdout = output; self.stderr = output
        checkout = Path(self.temp.name) / "checkout"; (checkout / "app/cbl").mkdir(parents=True); (checkout / "app/cpy").mkdir()
        (checkout / "app/cbl/CBACT02C.cbl").write_text("source"); (checkout / "app/cpy/CVACT02Y.cpy").write_text("copy")
        def runner_for(scenario):
            def runner(command, **kwargs):
                if command[0] == sys.executable:
                    return Completed(0, '{"status":"PASS"}')
                if command[0] == "./gradlew":
                    if scenario == "transpile-fail": return Completed(1, "ERROR LogFile - unsupported")
                    config = Path(next(value.split("=", 1)[1] for value in command if value.startswith("-PconfigFile=")))
                    output = Path(re.search(r'OutputPath="([^"]+)', config.read_text()).group(1)); output.mkdir(parents=True, exist_ok=True)
                    if scenario != "generated-absent":
                        (output / "Cbact02c.java").write_text("class Cbact02c {}")
                        (output / "Cvact02y.java").write_text("class Cvact02y {}")
                    return Completed(0, "CBACT02C processed")
                return Completed(1 if scenario == "javac-fail" else 0, "javac error")
            return runner
        for scenario, blocker in (("success", None), ("transpile-fail", "parse-transpile"), ("generated-absent", "generated-java"), ("javac-fail", "javac")):
            with self.subTest(scenario=scenario):
                result = PROBE.run_probe(checkout, runner_for(scenario)); self.assertEqual(result["firstBlocker"], blocker); PROBE.validate(result)
        original_which = PROBE.shutil.which; PROBE.shutil.which = lambda name: None
        try:
            result = PROBE.run_probe(checkout, runner_for("success")); self.assertEqual(result["firstBlocker"], "javac")
        finally:
            PROBE.shutil.which = original_which

    def test_probe_validate_report_from_subdirectory(self):
        result = subprocess.run(["python3", str(ROOT / "tools/quality-loop/carddemo_probe.py"), "validate-report"], cwd=ROOT / "docs/quality-governance", text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)

    def test_probe_requires_both_generated_carddemo_artifacts(self):
        checkout = Path(self.temp.name) / "checkout"
        (checkout / "app/cbl").mkdir(parents=True)
        (checkout / "app/cpy").mkdir()
        (checkout / "app/cbl/CBACT02C.cbl").write_text("source")
        (checkout / "app/cpy/CVACT02Y.cpy").write_text("copy")

        class Completed:
            returncode = 0
            stdout = '{"status":"PASS"}'
            stderr = ""

        for missing in ("Cbact02c.java", "Cvact02y.java"):
            def runner(command, missing=missing, **kwargs):
                if command[0] == sys.executable:
                    return Completed()
                if command[0] == "./gradlew":
                    config = Path(next(value.split("=", 1)[1] for value in command if value.startswith("-PconfigFile=")))
                    output = Path(re.search(r'OutputPath="([^"]+)', config.read_text()).group(1))
                    output.mkdir(parents=True, exist_ok=True)
                    for artifact in ("Cbact02c.java", "Cvact02y.java"):
                        if artifact != missing:
                            (output / artifact).write_text("class Generated {}")
                    return Completed()
                return Completed()

            with self.subTest(missing=missing):
                result = PROBE.run_probe(checkout, runner)
                self.assertEqual(result["firstBlocker"], "generated-java")
                self.assertEqual(result["stages"][2]["errorType"], "missing-generated-artifact")
                PROBE.validate(result)

    def test_probe_javac_runtime_classpath_and_stable_diagnostics(self):
        checkout = Path(self.temp.name) / "checkout"
        (checkout / "app/cbl").mkdir(parents=True)
        (checkout / "app/cpy").mkdir()
        (checkout / "app/cbl/CBACT02C.cbl").write_text("source")
        (checkout / "app/cpy/CVACT02Y.cpy").write_text("copy")

        class Completed:
            def __init__(self, code=0, output=""):
                self.returncode = code
                self.stdout = output
                self.stderr = output

        def runner(commands):
            def run(command, **kwargs):
                commands.append(command)
                if command[0] == sys.executable:
                    return Completed(output='{"status":"PASS"}')
                if command[0] == "./gradlew":
                    config = Path(next(value.split("=", 1)[1] for value in command if value.startswith("-PconfigFile=")))
                    config_text = config.read_text()
                    self.assertIn('<Group Name="IncludeGroup"><Application Name="CVACT02Y"><File Name="CVACT02Y"/></Application></Group>', config_text)
                    output = Path(re.search(r'OutputPath="([^"]+)', config_text).group(1))
                    output.mkdir(parents=True, exist_ok=True)
                    (output / "Cbact02c.java").write_text("class Cbact02c {}")
                    (output / "Cvact02y.java").write_text("class Cvact02y {}")
                    return Completed(output="CBACT02C processed")
                return Completed()
            return run

        first_commands = []
        first = PROBE.run_probe(checkout, runner(first_commands))
        second_commands = []
        second = PROBE.run_probe(checkout, runner(second_commands))
        self.assertEqual(first, second)
        gradle = next(command for command in first_commands if command[0] == "./gradlew")
        self.assertEqual(gradle[1:4], [":naca-jlib:classes", ":naca-rt:classes", ":naca-trans:transpile"])
        javac = next(command for command in first_commands if command[0].endswith("javac"))
        self.assertIn("-J-Duser.language=en", javac)
        self.assertIn("-J-Duser.country=US", javac)
        self.assertIn("-cp", javac)
        self.assertIn(os.pathsep, javac[javac.index("-cp") + 1])

        self.assertEqual(
            PROBE.normalize_diagnostic("/tmp/one/output/Sample.java:19: error", Path("/tmp/one")),
            "<temp>/output/Sample.java:19: error",
        )
        self.assertEqual(
            PROBE.normalize_diagnostic("/tmp/two/output/Sample.java:19: error", Path("/tmp/two")),
            "<temp>/output/Sample.java:19: error",
        )

    def test_probe_gradle_missing_fails_closed(self):
        checkout = Path(self.temp.name) / "checkout"
        (checkout / "app/cbl").mkdir(parents=True)
        (checkout / "app/cpy").mkdir()
        (checkout / "app/cbl/CBACT02C.cbl").write_text("source")
        (checkout / "app/cpy/CVACT02Y.cpy").write_text("copy")

        def missing_runner(command, **kwargs):
            if command[0] == sys.executable:
                class Completed:
                    returncode = 0
                    stdout = '{"status":"PASS"}'
                    stderr = ""
                return Completed()
            raise FileNotFoundError("gradlew")

        result = PROBE.run_probe(checkout, missing_runner)
        self.assertEqual(result["firstBlocker"], "parse-transpile")
        self.assertEqual([item["status"] for item in result["stages"]], ["PASS", "FAIL", "NOT_RUN", "NOT_RUN"])
        PROBE.validate(result)

    def test_probe_preflight_and_javac_oserror_reports_are_canonical(self):
        checkout = Path(self.temp.name) / "checkout"
        (checkout / "app/cbl").mkdir(parents=True)
        (checkout / "app/cpy").mkdir()
        (checkout / "app/cbl/CBACT02C.cbl").write_text("source")
        (checkout / "app/cpy/CVACT02Y.cpy").write_text("copy")

        def preflight_missing(command, **kwargs):
            raise OSError("preflight unavailable")

        source_failure = PROBE.run_probe(checkout, preflight_missing)
        self.assertEqual(source_failure["firstBlocker"], "source-verification")
        self.assertEqual([item["status"] for item in source_failure["stages"]], ["FAIL", "NOT_RUN", "NOT_RUN", "NOT_RUN"])
        PROBE.validate(source_failure)

        class Completed:
            returncode = 0
            stdout = '{"status":"PASS"}'
            stderr = ""

        def javac_missing(command, **kwargs):
            if command[0] == sys.executable:
                return Completed()
            if command[0] == "./gradlew":
                config = Path(next(value.split("=", 1)[1] for value in command if value.startswith("-PconfigFile=")))
                output = Path(re.search(r'OutputPath="([^"]+)', config.read_text()).group(1))
                output.mkdir(parents=True, exist_ok=True)
                (output / "Cbact02c.java").write_text("class Cbact02c {}")
                (output / "Cvact02y.java").write_text("class Cvact02y {}")
                return Completed()
            raise OSError("javac unavailable")

        javac_failure = PROBE.run_probe(checkout, javac_missing)
        self.assertEqual(javac_failure["firstBlocker"], "javac")
        self.assertEqual([item["status"] for item in javac_failure["stages"]], ["PASS", "PASS", "PASS", "FAIL"])
        PROBE.validate(javac_failure)


if __name__ == "__main__":
    unittest.main()
