#!/usr/bin/env python3
"""Small, deterministic quality-governance control plane (Python stdlib only)."""
import argparse
import json
import os
import sys
import tempfile
from pathlib import Path

STATES = {"READY", "RUNNING", "REVIEW", "VERIFIED", "REJECTED", "BLOCKED"}
TRANSITIONS = {"READY": {"RUNNING", "BLOCKED"}, "RUNNING": {"REVIEW", "REJECTED", "BLOCKED"},
               "REVIEW": {"RUNNING", "VERIFIED", "REJECTED", "BLOCKED"},
               "VERIFIED": set(), "REJECTED": {"READY", "BLOCKED"}, "BLOCKED": {"READY"}}
REQUIRED = {"id", "objective", "stream", "priority", "dependencies", "allowedPaths", "forbiddenPaths",
            "authorizedProtectedPaths", "maxFiles", "verification", "state", "attempts"}

def default_ledger():
    return Path(__file__).resolve().parents[2] / "docs/quality-governance/ledger.json"

def load(path):
    try:
        data = json.loads(Path(path).read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as e:
        raise ValueError(f"cannot read ledger: {e}")
    validate_data(data)
    return data

def validate_data(data):
    if not isinstance(data, dict) or data.get("version") != 1 or not isinstance(data.get("tasks"), list):
        raise ValueError("ledger must contain version 1 and a tasks array")
    ids = set()
    for task in data["tasks"]:
        if not isinstance(task, dict) or not REQUIRED.issubset(task):
            raise ValueError("task missing required field")
        ident = task["id"]
        if not isinstance(ident, str) or not ident or ident in ids:
            raise ValueError(f"duplicate or invalid id: {ident}")
        ids.add(ident)
        if not isinstance(task["objective"], str) or not task["objective"]: raise ValueError(f"invalid objective for {ident}")
        if task["state"] not in STATES:
            raise ValueError(f"invalid state for {ident}")
        if not isinstance(task["priority"], int) or isinstance(task["priority"], bool): raise ValueError(f"invalid priority for {ident}")
        if not isinstance(task["dependencies"], list) or any(not isinstance(x, str) for x in task["dependencies"]):
            raise ValueError(f"invalid dependencies for {ident}")
        for field in ("allowedPaths", "forbiddenPaths", "authorizedProtectedPaths"):
            if not isinstance(task[field], list) or any(not isinstance(x, str) or not x for x in task[field]):
                raise ValueError(f"invalid {field} for {ident}")
        if not isinstance(task["verification"], list) or any(not isinstance(x, str) or not x for x in task["verification"]):
            raise ValueError(f"invalid verification for {ident}")
        if (not isinstance(task["maxFiles"], int) or isinstance(task["maxFiles"], bool)
                or task["maxFiles"] < 0 or not isinstance(task["attempts"], int)
                or isinstance(task["attempts"], bool) or task["attempts"] < 0):
            raise ValueError(f"invalid limits for {ident}")
    for task in data["tasks"]:
        unknown = set(task["dependencies"]) - ids
        if unknown:
            raise ValueError(f"unknown dependency for {task['id']}: {sorted(unknown)}")

def write(path, data):
    path = Path(path)
    fd, temp = tempfile.mkstemp(prefix=path.name + ".", dir=path.parent)
    try:
        with os.fdopen(fd, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
            f.write("\n")
            f.flush()
            os.fsync(f.fileno())
        os.replace(temp, path)
    finally:
        if os.path.exists(temp): os.unlink(temp)

def main(argv=None):
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)
    for name in ("validate", "next"):
        sub.add_parser(name).add_argument("ledger", nargs="?", type=Path)
    transition = sub.add_parser("transition")
    transition.add_argument("id")
    transition.add_argument("state", choices=sorted(STATES))
    transition.add_argument("--evidence", action="append", default=[])
    transition.add_argument("ledger", nargs="?", type=Path)
    args = parser.parse_args(argv)
    path = getattr(args, "ledger", None) or default_ledger()
    try:
        data = load(path)
    except ValueError as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1
    if args.command == "validate":
        print(json.dumps({"status": "PASS", "ledger": str(path), "tasks": len(data["tasks"])}))
        return 0
    if args.command == "next":
        ready = [task for task in data["tasks"] if task["state"] == "READY" and all(
            next(item for item in data["tasks"] if item["id"] == dependency)["state"] == "VERIFIED"
            for dependency in task["dependencies"])]
        if not ready:
            print(json.dumps({"status": "NONE", "message": "no eligible READY task"}))
            return 0
        print(json.dumps(min(ready, key=lambda x: (x["priority"], x["id"])), sort_keys=True))
        return 0
    task = next((item for item in data["tasks"] if item["id"] == args.id), None)
    if not task:
        print(json.dumps({"status": "FAIL", "error": "unknown task"}))
        return 1
    if args.state not in TRANSITIONS[task["state"]]:
        print(json.dumps({"status": "FAIL", "error": f"illegal transition {task['state']} -> {args.state}"}))
        return 1
    old = task["state"]
    task["state"] = args.state
    if args.state == "RUNNING":
        task["attempts"] += 1
    if args.evidence:
        task["evidence"] = args.evidence
    write(path, data)
    print(json.dumps({"status": "PASS", "id": args.id, "from": old,
                      "state": args.state, "attempts": task["attempts"]}))
    return 0

if __name__ == "__main__": sys.exit(main())
