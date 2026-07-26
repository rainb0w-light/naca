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
        # default worker permission mode is acceptEdits (not auto, never bypass)
        self.assertEqual(argv[argv.index("--permission-mode") + 1], "acceptEdits")
        self.assertNotIn("--dangerously-skip-permissions", argv)
        self.assertNotIn("bypassPermissions", argv)
        self.assertEqual(argv[argv.index("--model") + 1], "sonnet")
        # --disallowedTools is variadic: each deny rule is its OWN argv element...
        self.assertIn("--disallowedTools", argv)
        dt = argv.index("--disallowedTools")
        self.assertEqual(argv[dt + 1], "Bash(git push:*)")
        self.assertIn("Bash(git commit:*)", argv[dt + 1:])
        # ...and the deny list is LAST (nothing for the variadic flag to swallow).
        self.assertEqual(dt + 1 + len(controller.DEFAULT_DISALLOWED_TOOLS), len(argv))
        # The prompt is NOT a positional argv token (the variadic bug): it is stdin.
        self.assertNotIn("PROMPT", argv)
        self.assertEqual(captured["kwargs"].get("input"), "PROMPT")
        self.assertEqual(out, '{"type":"result"}')

    def test_prompt_is_never_a_tool_or_deny_value(self):
        """Regression for the variadic-swallow bug: the prompt must not appear
        anywhere in argv, and must be the stdin input instead."""
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

        prompt = "MIGRATE CICS-RETURN now"
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            cfg = cfg_for(repo, td)
            cfg.claude_cmd = ["claude"]
            c.default_worker_runner(prompt, cfg)

        argv = captured["argv"]
        # no argv element equals or contains the prompt text
        self.assertFalse(any(prompt == a or prompt in a for a in argv),
                         f"prompt leaked into argv: {argv}")
        self.assertEqual(captured["kwargs"].get("input"), prompt)

    def test_reviewer_argv_uses_stdin_prompt(self):
        """The reviewer's variadic --tools must not consume the review prompt."""
        import tempfile
        captured = {}

        class FakeCompleted:
            returncode = 0
            stdout = '{"type":"result","structured_output":{"approved":true,"issues":[]}}'
            stderr = ""

        def fake_run(argv, **kwargs):
            captured["argv"] = argv
            captured["kwargs"] = kwargs
            return FakeCompleted()

        import st4loop.controller as c
        orig = c.subprocess.run
        c.subprocess.run = fake_run
        self.addCleanup(lambda: setattr(c.subprocess, "run", orig))

        review_prompt = "REVIEW THIS DIFF"
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            cfg = cfg_for(repo, td)
            cfg.claude_cmd = ["claude"]
            verdict = c.default_reviewer_runner(review_prompt, {"id": "X"}, cfg)

        argv = captured["argv"]
        self.assertIn("--agent", argv)
        self.assertEqual(argv[argv.index("--agent") + 1], "st4-reviewer")
        self.assertIn("--tools", argv)
        self.assertEqual(argv[argv.index("--tools") + 1], "Read,Grep,Glob")
        # --tools value is the LAST token; prompt is stdin, not a positional.
        self.assertEqual(argv.index("--tools") + 2, len(argv))
        stdin_input = captured["kwargs"].get("input")
        # the (wrapped) review prompt is delivered via stdin and CONTAINS the diff...
        self.assertIn(review_prompt, stdin_input)
        # ...and NO argv element is the prompt (raw or wrapped) -> variadic can't eat it.
        self.assertFalse(any(review_prompt == a or stdin_input == a for a in argv),
                         f"review prompt leaked into argv: {argv}")
        self.assertTrue(verdict["approved"])


class PermissionModeAndTimeoutTest(unittest.TestCase):
    """Permission mode is configurable with a safe acceptEdits default; bypass is
    refused; the default worker timeout is 1800s (not an hour-long dead session)."""

    def test_config_defaults(self):
        self.assertEqual(controller.DEFAULT_WORKER_PERMISSION_MODE, "acceptEdits")
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            # construct directly (cfg_for overrides worker_timeout for fast tests)
            cfg = controller.Config(
                repo_root=repo,
                ledger_path=repo / "docs" / "migration-ledger.json",
                worker_prompt_path=repo / ".claude" / "st4-loop" / "worker.md",
                result_schema_path=repo / ".claude" / "st4-loop" / "result.schema.json",
                review_schema_path=repo / ".claude" / "st4-loop" / "review.schema.json",
                log_dir=repo / ".st4-loop" / "logs",
            )
            self.assertEqual(cfg.permission_mode, "acceptEdits")
            self.assertEqual(cfg.worker_timeout, 1800)

    def test_config_refuses_bypass_and_dontask(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            for bad in ("bypassPermissions", "dontAsk"):
                with self.assertRaises(controller.LoopError):
                    cfg_for(repo, td)  # build a valid base first
                    controller.Config(
                        repo_root=repo,
                        ledger_path=repo / "docs" / "migration-ledger.json",
                        worker_prompt_path=repo / ".claude" / "st4-loop" / "worker.md",
                        result_schema_path=repo / ".claude" / "st4-loop" / "result.schema.json",
                        review_schema_path=repo / ".claude" / "st4-loop" / "review.schema.json",
                        log_dir=repo / ".st4-loop" / "logs",
                        permission_mode=bad,
                    )

    def test_worker_runner_defense_in_depth_refuses_bypass(self):
        import tempfile
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            cfg = cfg_for(repo, td)
            cfg.permission_mode = "bypassPermissions"  # smuggle past construction
            with self.assertRaises(controller.LoopError):
                controller.default_worker_runner("PROMPT", cfg)

    def test_worker_argv_uses_configured_mode(self):
        import tempfile
        captured = {}

        class FakeCompleted:
            returncode = 0
            stdout = '{"type":"result"}'
            stderr = ""

        def fake_run(argv, **kwargs):
            captured["argv"] = argv
            return FakeCompleted()

        import st4loop.controller as c
        orig = c.subprocess.run
        c.subprocess.run = fake_run
        self.addCleanup(lambda: setattr(c.subprocess, "run", orig))
        with tempfile.TemporaryDirectory() as td:
            repo = make_repo(Path(td) / "repo", ledger_two_items())
            cfg = cfg_for(repo, td)
            cfg.claude_cmd = ["claude"]
            cfg.permission_mode = "manual"
            c.default_worker_runner("PROMPT", cfg)
        argv = captured["argv"]
        self.assertEqual(argv[argv.index("--permission-mode") + 1], "manual")

    def test_cli_parser_defaults_and_choices(self):
        import run_loop
        parser = run_loop._build_parser()
        ns = parser.parse_args([])
        self.assertEqual(ns.permission_mode, "acceptEdits")
        self.assertEqual(ns.worker_timeout, 1800)
        ns2 = parser.parse_args(["--permission-mode", "auto", "--worker-timeout", "900"])
        self.assertEqual(ns2.permission_mode, "auto")
        self.assertEqual(ns2.worker_timeout, 900)
        # bypassPermissions is not an allowed choice -> argparse exits non-zero
        with self.assertRaises(SystemExit):
            parser.parse_args(["--permission-mode", "bypassPermissions"])


if __name__ == "__main__":
    unittest.main()
