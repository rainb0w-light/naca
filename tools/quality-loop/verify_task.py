#!/usr/bin/env python3
"""Verify a task's changed-file boundary and declared commands."""
import argparse
import fnmatch
import json
import subprocess
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parent))
from quality_loop import load

PROTECTED = ["docs/project-quality-baseline.json", "**/checkstyle*", "**/pmd*", "**/spotbugs*", "**/*suppressions*", ".st4-loop/**"]
def matches(path, pattern):
    return fnmatch.fnmatch(path, pattern) or fnmatch.fnmatch(path, pattern.replace("/**", "/*"))
def fail(kind, **details):
    print(json.dumps({"status": "FAIL", "reason": kind, **details}, sort_keys=True)); return 1
def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("id")
    parser.add_argument("--base-ref", required=True)
    parser.add_argument("ledger", nargs="?", type=Path)
    args = (parser.parse_intermixed_args() if hasattr(parser, "parse_intermixed_args") else parser.parse_args())
    ledger = args.ledger or Path(__file__).resolve().parents[2] / "docs/quality-governance/ledger.json"
    try: data = load(ledger)
    except ValueError as e: return fail("ledger", message=str(e))
    task = next((item for item in data["tasks"] if item["id"] == args.id), None)
    if not task: return fail("unknown-task")
    try:
        repo_root = Path(subprocess.check_output(["git", "rev-parse", "--show-toplevel"], text=True).strip())
    except subprocess.CalledProcessError as e: return fail("git-root", message=str(e))
    try:
        raw = subprocess.check_output(["git", "diff", "--name-only", f"{args.base_ref}...HEAD"], cwd=repo_root, text=True, stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError as e: return fail("git-diff", message=e.output.strip())
    changed = [x.strip() for x in raw.splitlines() if x.strip()]
    bad = []
    for path in changed:
        allowed = any(matches(path, p) for p in task["allowedPaths"])
        explicit_forbidden = any(matches(path, p) for p in task["forbiddenPaths"])
        protected = any(matches(path, p) for p in PROTECTED)
        authorized = any(matches(path, p) for p in task["authorizedProtectedPaths"])
        if not allowed or explicit_forbidden or (protected and not authorized): bad.append(path)
    if bad: return fail("paths", files=sorted(bad))
    if len(changed) > task["maxFiles"]: return fail("maxFiles", count=len(changed), limit=task["maxFiles"])
    for command in task["verification"]:
        result = subprocess.run(command, shell=True, cwd=repo_root, text=True, capture_output=True)
        if result.returncode: return fail("verification", command=command, exit=result.returncode)
    print(json.dumps({"status": "PASS", "id": args.id, "files": changed,
                      "verification": len(task["verification"])}, sort_keys=True))
    return 0
if __name__ == "__main__": sys.exit(main())
