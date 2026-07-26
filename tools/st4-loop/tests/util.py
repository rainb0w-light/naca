"""Shared helpers for controller tests: a throwaway git repo with a ledger."""
import json
import subprocess
import sys
from pathlib import Path

TESTS_DIR = Path(__file__).resolve().parent
FIXTURES = TESTS_DIR / "fixtures"
STUB_WORKER = FIXTURES / "stub_worker.py"

# make `st4loop` importable
sys.path.insert(0, str(TESTS_DIR.parent))


def git(repo, *args):
    subprocess.run(["git", *args], cwd=str(repo), check=True,
                   capture_output=True, text=True)


def make_repo(tmp_path, ledger_data):
    """Create a temp git repo containing a ledger + worker prompt + one source file."""
    repo = Path(tmp_path)
    (repo / "docs").mkdir(parents=True)
    (repo / ".claude" / "st4-loop").mkdir(parents=True)
    (repo / "src").mkdir(parents=True)

    (repo / "docs" / "migration-ledger.json").write_text(
        json.dumps(ledger_data, indent=2), encoding="utf-8")
    (repo / ".claude" / "st4-loop" / "worker.md").write_text(
        "# stub worker prompt\n", encoding="utf-8")
    (repo / ".claude" / "st4-loop" / "result.schema.json").write_text(
        "{}", encoding="utf-8")
    (repo / ".claude" / "st4-loop" / "review.schema.json").write_text(
        "{}", encoding="utf-8")
    (repo / "src" / "App.java").write_text("class App {}\n", encoding="utf-8")
    # mirror the real repo: loop runtime logs live outside tracked source
    (repo / ".gitignore").write_text(".st4-loop/\n", encoding="utf-8")

    git(repo, "init", "-q")
    git(repo, "config", "user.email", "test@example.com")
    git(repo, "config", "user.name", "Test")
    git(repo, "config", "commit.gpgsign", "false")
    git(repo, "add", "-A")
    git(repo, "commit", "-qm", "init")
    return repo
