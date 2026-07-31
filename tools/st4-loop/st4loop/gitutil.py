"""Small git helpers. All mutations are scoped (explicit paths) — never broad."""

import shutil
import subprocess
from pathlib import Path


class GitError(Exception):
    pass


def run_git(root, *args, check=True):
    result = subprocess.run(
        ["git", *args],
        cwd=str(root),
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    if check and result.returncode != 0:
        raise GitError(
            f"git {' '.join(args)} failed ({result.returncode}): "
            f"{result.stderr.strip() or result.stdout.strip()}"
        )
    return result


def is_clean(root):
    """True when the worktree has no uncommitted changes (tracked or untracked)."""
    result = run_git(root, "status", "--porcelain", "--untracked-files=all")
    return result.stdout.strip() == ""


def porcelain(root):
    """Return [(XY, path), ...] for every dirty file path.

    Git's default normal-mode status collapses a wholly-untracked directory to
    one directory entry. The loop compares this inventory with the worker's
    file-level declaration, so always enumerate every untracked file.
    """
    result = run_git(root, "status", "--porcelain", "--untracked-files=all")
    out = []
    for line in result.stdout.splitlines():
        if not line.strip():
            continue
        code, path = line[:2], line[3:].strip()
        if path.startswith('"') and path.endswith('"'):
            path = path[1:-1]
        out.append((code, path))
    return out


def dirty_paths(root):
    return [path for _, path in porcelain(root)]


def scoped_revert(root, paths, protect=()):
    """Revert exactly the given paths (modified/deleted -> checkout, untracked -> remove).

    `protect` is a set of repo-relative paths to never touch (e.g. the ledger and
    log dir). This is deliberately NOT a broad `git reset --hard`/`git clean -fd`.
    """
    protect = {str(p) for p in protect}
    # porcelain yields (code, path); map path -> code so we can look up by path.
    codes = {path: code for code, path in porcelain(root)}
    for path in paths:
        if path in protect:
            continue
        code = codes.get(path)
        abs_path = Path(root) / path
        if code is None:
            continue  # not dirty; leave alone
        if code == "??":
            # untracked: remove only this specific path
            if abs_path.is_dir():
                shutil.rmtree(abs_path, ignore_errors=True)
            elif abs_path.exists():
                abs_path.unlink()
        else:
            run_git(root, "checkout", "--", path, check=False)


def diff(root, staged=False):
    args = ["diff"]
    if staged:
        args.append("--cached")
    return run_git(root, *args).stdout


def head_sha(root):
    return run_git(root, "rev-parse", "HEAD").stdout.strip()


def stage_and_commit(root, paths, message):
    """Commit EXACTLY the given paths. Returns the new commit sha (or None if nothing)."""
    paths = [p for p in paths if p]
    if not paths:
        return None
    run_git(root, "add", "--", *paths)
    # If nothing ended up staged (e.g. identical content), report no commit.
    staged = run_git(root, "diff", "--cached", "--name-only").stdout.strip()
    if not staged:
        return None
    run_git(root, "commit", "-m", message)
    return head_sha(root)
