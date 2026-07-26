import json
import sys
import unittest
from pathlib import Path

from st4loop import controller, gitutil, ledger
from tests.util import STUB_WORKER, make_repo

STATUS_ORDER = ["not-started", "parser-preserved", "semantic-built",
                "binding-added", "template-added", "production-wired",
                "direct-retired", "done"]


def ledger_two_items():
    return {
        "meta": {
            "schemaVersion": 1,
            "statusOrder": STATUS_ORDER,
            "scopeOrder": ["COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS", "BMS_ARTIFACT"],
            "ratchet": {
                "finalArchitectureCheck": {"tests": 10, "failures": 5, "directBackends": 3},
                "rules": ["r"],
            },
        },
        "entries": [
            {"id": "CICS-FIRST", "scope": "EMBEDDED_CICS", "status": "semantic-built",
             "productionReachable": True, "blocker": None, "priority": 10,
             "verification": [], "expectedDebtDelta": {"directBackends": -1}},
            {"id": "CICS-SECOND", "scope": "EMBEDDED_CICS", "status": "semantic-built",
             "productionReachable": True, "blocker": None, "priority": 20,
             "verification": []},
        ],
    }


def cfg_for(repo, tmp_path, max_iterations=1, max_attempts=3, dry_run=False,
            allow_commit=True):
    return controller.Config(
        repo_root=repo,
        ledger_path=repo / "docs" / "migration-ledger.json",
        worker_prompt_path=repo / ".claude" / "st4-loop" / "worker.md",
        result_schema_path=repo / ".claude" / "st4-loop" / "result.schema.json",
        review_schema_path=repo / ".claude" / "st4-loop" / "review.schema.json",
        log_dir=repo / ".st4-loop" / "logs",
        claude_cmd=[sys.executable, str(STUB_WORKER)],
        max_iterations=max_iterations,
        max_attempts=max_attempts,
        worker_timeout=60,
        dry_run=dry_run,
        allow_commit=allow_commit,
    )


def env_patch(testcase, **env):
    """Set env vars for the stub worker, restoring afterwards."""
    import os
    saved = {k: os.environ.get(k) for k in env}
    for k, v in env.items():
        os.environ[k] = v
    def restore():
        for k, v in saved.items():
            if v is None:
                os.environ.pop(k, None)
            else:
                os.environ[k] = v
    testcase.addCleanup(restore)


def debt_fake(base, cur):
    """Fake debt measurer: returns `base` on the first (clean-entry) call and
    `cur` on every subsequent (post-worker) call, so delta = cur - base."""
    state = {"first": True}
    def measure(repo_root):
        if state["first"]:
            state["first"] = False
            return base
        return cur
    return measure


class ControllerTest(unittest.TestCase):
    def _ok_review(self, diff_text, item, cfg):
        return {"approved": True, "issues": [], "singleSlice": True, "debtGrew": False}

    def _ok_verify(self, item, cfg):
        return True, "verify ok"

    def test_dirty_worktree_refused(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            (repo / "src" / "dirty.txt").write_text("x")  # untracked -> dirty
            ctrl = controller.Controller(cfg_for(repo, td), log=lambda *_: None)
            with self.assertRaises(controller.LoopError):
                ctrl.run()

    def test_success_commits_selected_files_and_advances(self):
        import tempfile, os
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            env_patch(self, ST4_STUB_OUTCOME="success", ST4_STUB_ITEM_ID="CICS-FIRST",
                      ST4_STUB_TOUCH="src/New.java",
                      ST4_STUB_STATUS_ADVANCE="binding-added", ST4_STUB_DEBT="-1")
            cfg = cfg_for(repo, td, max_iterations=1)
            ctrl = controller.Controller(
                cfg, verify_runner=self._ok_verify, reviewer_runner=self._ok_review,
                debt_measurer=debt_fake(3, 2), log=lambda *_: None)
            summary = ctrl.run()
            self.assertEqual(summary[0]["result"], "success")
            self.assertIsNotNone(summary[0]["commit"])
            # worktree clean after the iteration
            self.assertTrue(gitutil.is_clean(repo))
            # ledger updated: status advanced, debt decreased, checkpointed
            data = ledger.load_ledger(cfg.ledger_path)
            first = ledger.entry_by_id(data, "CICS-FIRST")
            self.assertEqual(first["status"], "binding-added")
            self.assertEqual(data["meta"]["ratchet"]["finalArchitectureCheck"]["directBackends"], 2)
            self.assertEqual(first["attempts"], 1)
            # the new file and ledger are in the commit; worktree has the file
            self.assertTrue((repo / "src" / "New.java").exists())

    def test_failed_worker_retries_then_blocks_and_continues(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            # worker always fails for the first item; the loop must exhaust attempts,
            # mark it blocked, then move on to CICS-SECOND within max_iterations.
            env_patch(self, ST4_STUB_OUTCOME="failed", ST4_STUB_ITEM_ID="CICS-FIRST",
                      ST4_STUB_TOUCH="src/Bad.java")
            cfg = cfg_for(repo, td, max_iterations=2, max_attempts=2)
            attempts = {"n": 0}
            orig_verify = self._ok_verify
            ctrl = controller.Controller(
                cfg, verify_runner=self._ok_verify, reviewer_runner=self._ok_review,
                debt_measurer=debt_fake(3, 2), log=lambda *_: None)
            summary = ctrl.run()
            # iteration 1: CICS-FIRST blocked after 2 attempts
            self.assertEqual(summary[0]["item"], "CICS-FIRST")
            self.assertEqual(summary[0]["result"], "blocked")
            # the failed worker's stray file must NOT be committed/present
            self.assertFalse((repo / "src" / "Bad.java").exists())
            # iteration 2: now CICS-SECOND is selected (FIRST is blocked)
            self.assertEqual(summary[1]["item"], "CICS-SECOND")
            # ledger records the blocker
            data = ledger.load_ledger(cfg.ledger_path)
            first = ledger.entry_by_id(data, "CICS-FIRST")
            self.assertTrue(first["blocked"])
            self.assertEqual(first["attempts"], 2)
            # worktree clean at the end
            self.assertTrue(gitutil.is_clean(repo))

    def test_verification_failure_causes_retry_not_commit(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            env_patch(self, ST4_STUB_OUTCOME="success", ST4_STUB_ITEM_ID="CICS-FIRST",
                      ST4_STUB_TOUCH="src/New.java", ST4_STUB_DEBT="-1")
            cfg = cfg_for(repo, td, max_iterations=1, max_attempts=2)
            # verifier always says no -> success never commits -> blocked
            ctrl = controller.Controller(
                cfg, verify_runner=lambda item, c: (False, "gate red"),
                reviewer_runner=self._ok_review, debt_measurer=debt_fake(3, 2),
                log=lambda *_: None)
            summary = ctrl.run()
            self.assertEqual(summary[0]["result"], "blocked")
            self.assertFalse((repo / "src" / "New.java").exists())
            self.assertTrue(gitutil.is_clean(repo))

    def test_reviewer_rejection_blocks_commit(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            env_patch(self, ST4_STUB_OUTCOME="success", ST4_STUB_ITEM_ID="CICS-FIRST",
                      ST4_STUB_TOUCH="src/New.java", ST4_STUB_DEBT="-1")
            cfg = cfg_for(repo, td, max_iterations=1, max_attempts=1)
            ctrl = controller.Controller(
                cfg, verify_runner=self._ok_verify,
                reviewer_runner=lambda d, i, c: {"approved": False, "debtGrew": True,
                                                  "issues": ["adds a direct backend"]},
                debt_measurer=debt_fake(3, 2), log=lambda *_: None)
            summary = ctrl.run()
            self.assertEqual(summary[0]["result"], "blocked")
            self.assertFalse((repo / "src" / "New.java").exists())

    def test_dry_run_selects_without_invoking_worker(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            cfg = cfg_for(repo, td, dry_run=True, max_iterations=5)
            calls = {"n": 0}
            def worker(prompt, c):
                calls["n"] += 1
                return "{}"
            ctrl = controller.Controller(cfg, worker_runner=worker, log=lambda *_: None)
            summary = ctrl.run()
            self.assertEqual(calls["n"], 0)          # worker never invoked
            self.assertEqual(summary[0]["item"], "CICS-FIRST")
            self.assertTrue(summary[0]["dry_run"])
            self.assertEqual(len(summary), 1)        # dry-run proves selection once

    def test_malformed_worker_output_fails_closed(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            env_patch(self, ST4_STUB_BAD_JSON="1")
            cfg = cfg_for(repo, td, max_iterations=1, max_attempts=1)
            ctrl = controller.Controller(
                cfg, verify_runner=self._ok_verify, reviewer_runner=self._ok_review,
                debt_measurer=debt_fake(3, 2), log=lambda *_: None)
            summary = ctrl.run()
            self.assertEqual(summary[0]["result"], "blocked")
            self.assertTrue(gitutil.is_clean(repo))


class IndependentGateTest(unittest.TestCase):
    """The controller never trusts self-report: filesChanged must exactly match the
    actual dirty files, paths must be safe, the reviewer sees untracked content, and
    the debt delta is measured independently and must match the item's expectation."""

    def _run(self, td, debt, reviewer=None, verify=None, **env):
        repo = make_repo(Path(td) / "repo", ledger_two_items())
        env_patch(self, ST4_STUB_OUTCOME="success", ST4_STUB_ITEM_ID="CICS-FIRST", **env)
        cfg = cfg_for(repo, td, max_iterations=1, max_attempts=1)
        ctrl = controller.Controller(
            cfg, verify_runner=verify or (lambda i, c: (True, "ok")),
            reviewer_runner=reviewer or (lambda d, i, c: {"approved": True, "issues": []}),
            debt_measurer=debt, log=lambda *_: None)
        return repo, ctrl.run()

    def test_undeclared_dirty_file_blocks_and_is_not_committed(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo, summary = self._run(td, debt_fake(3, 2),
                                      ST4_STUB_TOUCH="src/New.java", ST4_STUB_NO_DECLARE="1")
            self.assertEqual(summary[0]["result"], "blocked")
            self.assertFalse((repo / "src" / "New.java").exists())  # reverted, unreviewed
            self.assertTrue(gitutil.is_clean(repo))

    def test_phantom_declared_file_blocks(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo, summary = self._run(td, debt_fake(3, 2),
                                      ST4_STUB_EXTRA_FILES="src/Ghost.java")
            self.assertEqual(summary[0]["result"], "blocked")
            self.assertTrue(gitutil.is_clean(repo))

    def test_absolute_path_declaration_blocks(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo, summary = self._run(td, debt_fake(3, 2),
                                      ST4_STUB_EXTRA_FILES="/etc/passwd")
            self.assertEqual(summary[0]["result"], "blocked")

    def test_traversal_declaration_blocks(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo, summary = self._run(td, debt_fake(3, 2),
                                      ST4_STUB_EXTRA_FILES="../evil")
            self.assertEqual(summary[0]["result"], "blocked")

    def test_debt_mismatch_blocks_even_when_all_else_green(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            # files match, verify ok, review ok -- but measured delta 0 != expected -1
            repo, summary = self._run(td, debt_fake(3, 3), ST4_STUB_TOUCH="src/New.java")
            self.assertEqual(summary[0]["result"], "blocked")
            self.assertFalse((repo / "src" / "New.java").exists())
            self.assertTrue(gitutil.is_clean(repo))

    def test_review_bundle_includes_untracked_new_file_content(self):
        import tempfile
        seen = {}
        def reviewer(bundle, item, cfg):
            seen["bundle"] = bundle
            return {"approved": True, "issues": []}
        with tempfile.TemporaryDirectory() as td:
            repo, summary = self._run(td, debt_fake(3, 2),
                                      reviewer=reviewer, ST4_STUB_TOUCH="src/New.java")
            self.assertEqual(summary[0]["result"], "success")
            bundle = seen["bundle"]
            self.assertIn("--- new file: src/New.java ---", bundle)
            self.assertIn("edited by stub worker for CICS-FIRST", bundle)


class WorkerArgvTest(unittest.TestCase):
    """default_worker_runner builds the documented claude argv (no real call)."""

    def test_argv_shape(self):
        import tempfile
        captured = {}

        class FakeCompleted:
            returncode = 0
            stdout = '{"type":"result"}'
            stderr = ""

        def fake_run(argv, **kwargs):
            captured["argv"] = argv
            captured["kwargs"] = kwargs
            return FakeCompleted()

        import st4loop.controller as c
        orig = c.subprocess.run
        c.subprocess.run = fake_run
        self.addCleanup(lambda: setattr(c.subprocess, "run", orig))

        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            cfg = cfg_for(repo, td)
            cfg.claude_cmd = ["claude"]  # assert the real default argv shape
            cfg.model = "sonnet"
            out = c.default_worker_runner("PROMPT", cfg)
        c.subprocess.run = orig

        argv = captured["argv"]
        self.assertEqual(argv[0], "claude")
        self.assertIn("-p", argv)
        self.assertIn("--output-format", argv)
        self.assertEqual(argv[argv.index("--output-format") + 1], "json")
        self.assertIn("--json-schema", argv)
        self.assertEqual(argv[argv.index("--permission-mode") + 1], "auto")
        self.assertNotIn("--dangerously-skip-permissions", argv)
        self.assertIn("--disallowedTools", argv)
        self.assertEqual(argv[argv.index("--model") + 1], "sonnet")
        self.assertEqual(argv[-1], "PROMPT")          # prompt is the trailing positional
        self.assertEqual(out, '{"type":"result"}')


if __name__ == "__main__":
    unittest.main()
