#!/usr/bin/env python3
"""Generate and validate the pinned CardDemo static capability inventory."""

from __future__ import annotations

import argparse
import json
import re
import sys
from collections import Counter
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
CORPUS = ROOT / "naca-rt-tests/src/test/resources/carddemo"
APP = CORPUS / "app"
ACCEPTANCE = CORPUS / "ACCEPTANCE_INVENTORY.json"
PROVENANCE = CORPUS / "PROVENANCE.json"
OUTPUT = CORPUS / "CAPABILITY_INVENTORY.json"


def read_json(path: Path) -> dict[str, object]:
    return json.loads(path.read_text(encoding="utf-8"))


def source_path(path: Path) -> str:
    return path.relative_to(CORPUS).as_posix()


def profile(path: Path) -> str:
    parts = path.relative_to(APP).parts
    if parts[0] == "app-transaction-type-db2":
        return "transaction-type-db2"
    if parts[0] == "app-authorization-ims-db2-mq":
        return "authorization-ims-db2-mq"
    if parts[0] == "app-vsam-mq":
        return "vsam-mq"
    return "base"


def normalized_cobol(path: Path) -> str:
    lines: list[str] = []
    for raw in path.read_text(encoding="iso-8859-1").splitlines():
        if len(raw) > 6 and raw[6] in "*/":
            continue
        if len(raw) > 6 and re.fullmatch(r"[ 0-9]{6}", raw[:6]):
            raw = raw[7:72]
        raw = re.sub(r"\*>.*$", "", raw)
        if raw.strip():
            lines.append(raw)
    return "\n".join(lines)


def command_name(family: str, body: str) -> str:
    words = re.findall(r"[A-Z0-9-]+", body.upper())
    if not words:
        return "UNKNOWN"
    command = words[0]
    if family == "CICS" and command in {"SEND", "RECEIVE"}:
        if len(words) > 1 and words[1] in {"MAP", "TEXT"}:
            command += "_" + words[1]
    if family == "CICS" and command in {"READQ", "WRITEQ", "DELETEQ"}:
        if len(words) > 1 and words[1] in {"TS", "TD"}:
            command += "_" + words[1]
    return command


def extract_exec_blocks(text: str, family: str) -> list[dict[str, object]]:
    pattern = re.compile(
        rf"\bEXEC\s+{family}\s+(.*?)\bEND-EXEC\b",
        re.IGNORECASE | re.DOTALL,
    )
    blocks: list[dict[str, object]] = []
    for match in pattern.finditer(text):
        body = " ".join(match.group(1).split())
        item: dict[str, object] = {
            "command": command_name(family, body),
            "statement": body,
        }
        if family == "CICS":
            item["mapSets"] = sorted(set(
                value.strip().strip("'\"").upper()
                for value in re.findall(
                    r"\bMAPSET\s*\(([^)]+)\)", body, re.IGNORECASE)
            ))
            item["files"] = sorted(set(
                value.strip().strip("'\"").upper()
                for value in re.findall(
                    r"\bFILE\s*\(([^)]+)\)", body, re.IGNORECASE)
            ))
        blocks.append(item)
    return blocks


def program_inventory(path: Path, acceptance: dict[str, dict[str, object]]) -> dict[str, object]:
    text = normalized_cobol(path)
    program_match = re.search(
        r"\bPROGRAM-ID\.?\s+([A-Z0-9-]+)", text, re.IGNORECASE)
    program_id = program_match.group(1).upper() if program_match else path.stem.upper()
    cics = extract_exec_blocks(text, "CICS")
    sql = extract_exec_blocks(text, "SQL")
    calls = sorted(set(match.upper() for match in re.findall(
        r"\bCALL\s+['\"]([^'\"]+)['\"]", text, re.IGNORECASE)))
    copies = sorted(set(match.upper() for match in re.findall(
        r"\bCOPY\s+([A-Z0-9-]+)", text, re.IGNORECASE)))
    accepted = acceptance.get(source_path(path), {})
    return {
        "name": program_id,
        "path": source_path(path),
        "profile": profile(path),
        "online": bool(cics),
        "acceptanceStatus": accepted.get("status", "untracked"),
        "firstBlocker": accepted.get("firstBlocker"),
        "copybooks": copies,
        "externalCalls": calls,
        "cics": cics,
        "sql": sql,
    }


def csd_transactions() -> list[dict[str, object]]:
    transactions: dict[str, dict[str, object]] = {}
    definition = re.compile(
        r"(?=^\s*DEFINE\s+)", re.IGNORECASE | re.MULTILINE)
    for path in sorted(APP.rglob("*.csd")) + sorted(APP.rglob("*.CSD")):
        text = path.read_text(encoding="iso-8859-1")
        for block in definition.split(text):
            trans_match = re.search(
                r"\bDEFINE\s+TRANSACTION\s*\(([^)]+)\)",
                block, re.IGNORECASE)
            if trans_match is None:
                continue
            program_match = re.search(
                r"\bPROGRAM\s*\(([^)]+)\)", block, re.IGNORECASE)
            transaction_id = trans_match.group(1).strip().upper()
            program_id = (
                program_match.group(1).strip().upper()
                if program_match is not None else None
            )
            current = {
                "transactionId": transaction_id,
                "program": program_id,
                "source": source_path(path),
            }
            previous = transactions.get(transaction_id)
            if previous is not None and previous["program"] != program_id:
                raise ValueError(
                    f"conflicting CSD transaction {transaction_id}: "
                    f"{previous['program']} vs {program_id}")
            transactions[transaction_id] = current
    return [transactions[key] for key in sorted(transactions)]


def generate() -> dict[str, object]:
    acceptance_report = read_json(ACCEPTANCE)
    provenance = read_json(PROVENANCE)
    acceptance = {
        str(item["path"]): item
        for item in acceptance_report["programs"]
    }
    paths = sorted(
        path for path in APP.rglob("*")
        if path.is_file() and path.suffix.lower() == ".cbl"
    )
    programs = [program_inventory(path, acceptance) for path in paths]
    cics_counts = Counter(
        str(command["command"])
        for program in programs for command in program["cics"]
    )
    sql_counts = Counter(
        str(statement["command"])
        for program in programs for statement in program["sql"]
    )
    call_counts = Counter(
        str(call)
        for program in programs for call in program["externalCalls"]
    )
    map_sets = sorted(
        path.stem.upper()
        for path in APP.rglob("*")
        if path.is_file() and path.suffix.lower() == ".bms"
    )
    return {
        "schemaVersion": 1,
        "reportKind": "CardDemoStaticCapabilityInventory",
        "source": provenance["source"],
        "scope": {
            "definition": "Every vendored app/**/*.cbl at the pinned commit.",
            "programs": len(programs),
            "transactions": len(csd_transactions()),
            "mapSets": len(map_sets),
        },
        "summary": {
            "onlinePrograms": sum(bool(program["online"]) for program in programs),
            "cicsCommands": dict(sorted(cics_counts.items())),
            "sqlCommands": dict(sorted(sql_counts.items())),
            "externalCalls": dict(sorted(call_counts.items())),
        },
        "mapSets": map_sets,
        "transactions": csd_transactions(),
        "programs": programs,
    }


def validate(report: dict[str, object]) -> list[str]:
    errors: list[str] = []
    expected = generate()
    if report != expected:
        errors.append(
            "capability inventory is stale; regenerate with --write")
    acceptance = read_json(ACCEPTANCE)
    expected_paths = sorted(str(item["path"]) for item in acceptance["programs"])
    actual_paths = sorted(str(item["path"]) for item in report.get("programs", []))
    if actual_paths != expected_paths:
        errors.append(
            "capability inventory program set differs from acceptance denominator")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write", action="store_true")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    if not args.write and not args.check:
        parser.error("choose --write and/or --check")

    report = generate()
    if args.write:
        OUTPUT.write_text(
            json.dumps(report, indent=2, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )
    if args.check:
        if not OUTPUT.is_file():
            print("ERROR: capability inventory is missing", file=sys.stderr)
            return 1
        errors = validate(read_json(OUTPUT))
        if errors:
            for error in errors:
                print(f"ERROR: {error}", file=sys.stderr)
            return 1
    print(
        "CardDemo capabilities valid: "
        f"programs={report['scope']['programs']}, "
        f"online={report['summary']['onlinePrograms']}, "
        f"transactions={report['scope']['transactions']}, "
        f"mapSets={report['scope']['mapSets']}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
