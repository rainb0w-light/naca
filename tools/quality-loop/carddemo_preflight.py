#!/usr/bin/env python3
"""Offline provenance and preflight checks for the pinned CardDemo corpus."""
import argparse
import hashlib
import json
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MANIFEST = ROOT / "docs/quality-governance/carddemo-corpus.json"
PINNED = "59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e"
URL = "https://github.com/aws-samples/aws-mainframe-modernization-carddemo.git"


def fail(message):
    print(json.dumps({"status": "FAIL", "error": message}, sort_keys=True))
    return 1


def read_manifest(path=MANIFEST):
    try:
        return json.loads(Path(path).read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as error:
        raise ValueError(f"invalid manifest: {error}") from error


def safe_path(value):
    path = Path(value)
    return not path.is_absolute() and ".." not in path.parts and str(path) == value


def validate(data):
    if data.get("schemaVersion") != 1 or set(data) != {"schemaVersion", "source", "candidates"}:
        raise ValueError("schemaVersion or top-level fields invalid")
    source = data["source"]
    if source.get("url") != URL or source.get("commit") != PINNED:
        raise ValueError("source URL or pinned commit invalid")
    license_info = source.get("license", {})
    if license_info.get("spdx") != "Apache-2.0" or not safe_path(license_info.get("path", "")) or not re.fullmatch(r"[0-9a-f]{64}", license_info.get("sha256", "")):
        raise ValueError("license provenance invalid")
    candidates = data["candidates"]
    if not isinstance(candidates, list) or len(candidates) != 2 or {item.get("path") for item in candidates} != {"app/cbl/CBACT02C.cbl", "app/cbl/CBCUS01C.cbl"}:
        raise ValueError("candidate set invalid")
    for item in candidates:
        required = {"path", "sha256", "bytes", "lines", "copyDependencies", "externalCalls", "organization", "selectionReason", "expectedFirstStage"}
        if set(item) != required or not safe_path(item["path"]) or not re.fullmatch(r"[0-9a-f]{64}", item["sha256"]):
            raise ValueError("candidate fields invalid")
        if (not isinstance(item["bytes"], int) or item["bytes"] <= 0 or not isinstance(item["lines"], int) or item["lines"] <= 0
                or not isinstance(item["copyDependencies"], list) or not item["copyDependencies"]
                or any(not isinstance(value, str) or not value for value in item["copyDependencies"])
                or len(set(item["copyDependencies"])) != len(item["copyDependencies"])
                or not isinstance(item["externalCalls"], list) or not item["externalCalls"]
                or any(not isinstance(value, str) or not value for value in item["externalCalls"])
                or len(set(item["externalCalls"])) != len(item["externalCalls"])
                or any(not isinstance(item[field], str) or not item[field] for field in ("organization", "selectionReason", "expectedFirstStage"))):
            raise ValueError("candidate measurements invalid")


def git_root(checkout):
    return Path(subprocess.check_output(["git", "-C", str(checkout), "rev-parse", "--show-toplevel"], text=True).strip())


def source_facts(path):
    content = path.read_bytes()
    text = content.decode("latin-1")
    code_lines = [line for line in text.splitlines() if len(line) < 7 or line[6] != "*"]
    code = "\n".join(code_lines)
    copies = sorted(set(re.findall(r"\bCOPY\s+([A-Z0-9_-]+)", code, re.IGNORECASE)))
    calls = sorted(set(re.findall(r"\bCALL\s+'([^']+)'", code, re.IGNORECASE)))
    return {"sha256": hashlib.sha256(content).hexdigest(), "bytes": len(content), "lines": len(text.splitlines()), "copyDependencies": copies, "externalCalls": calls}


def verify(checkout, data):
    root = git_root(checkout)
    actual_commit = subprocess.check_output(["git", "-C", str(root), "rev-parse", "HEAD"], text=True).strip()
    if actual_commit != data["source"]["commit"]:
        raise ValueError(f"checkout commit mismatch: {actual_commit}")
    license_path = root / data["source"]["license"]["path"]
    if not license_path.is_file() or hashlib.sha256(license_path.read_bytes()).hexdigest() != data["source"]["license"]["sha256"]:
        raise ValueError("license mismatch")
    for item in data["candidates"]:
        path = root / item["path"]
        if not path.is_file():
            raise ValueError(f"candidate missing: {item['path']}")
        facts = source_facts(path)
        for key in ("sha256", "bytes", "lines", "copyDependencies", "externalCalls"):
            if facts[key] != item[key]:
                raise ValueError(f"{item['path']} {key} mismatch")


def main(argv=None):
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("validate-manifest")
    verify_parser = sub.add_parser("verify-source")
    verify_parser.add_argument("checkout", type=Path)
    args = parser.parse_args(argv)
    try:
        data = read_manifest()
        validate(data)
        if args.command == "verify-source":
            verify(args.checkout, data)
        print(json.dumps({"status": "PASS", "command": args.command}, sort_keys=True))
        return 0
    except (OSError, ValueError, subprocess.CalledProcessError) as error:
        return fail(str(error))


if __name__ == "__main__":
    sys.exit(main())
