import json
import subprocess
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
CLI = ROOT / "tools/quality-loop/quality_loop.py"
VERIFY = ROOT / "tools/quality-loop/verify_task.py"


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


if __name__ == "__main__":
    unittest.main()
