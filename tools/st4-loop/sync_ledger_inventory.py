#!/usr/bin/env python3
"""Synchronize the direct-backend retirement queue with the Java source tree.

The source inventory is authoritative: every CJava* class under generate/java
that still subclasses a semantic entity gets exactly one ledger entry. Existing
workflow state is preserved, including terminal entries for already-deleted
backends. BMS/form backends are recorded but excluded from the COBOL queue by
their BMS_ARTIFACT scope.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from dataclasses import dataclass
from pathlib import Path


BACKEND_PATTERN = re.compile(
    r"\bpublic\s+(?:abstract\s+)?class\s+(\w+)\s+extends\s+"
    r"((?:CEntity\w*|CBaseActionEntity|CDataEntity))\b"
)
PACKAGE_PATTERN = re.compile(r"^package\s+([\w.]+);", re.MULTILINE)
IMPORT_PATTERN = re.compile(r"^import\s+([\w.]+);", re.MULTILINE)
MANAGED_KIND = "DIRECT_BACKEND_RETIREMENT"

AREA_CONFIG = {
    "SQL": ("EMBEDDED_SQL", 100, "SQL"),
    "CICS": ("EMBEDDED_CICS", 200, "CICS"),
    "st": ("COBOL_CORE", 300, "COBOL-ST"),
    "verbs": ("COBOL_CORE", 400, "COBOL-VERB"),
    "expressions": ("COBOL_CORE", 500, "COBOL-EXPRESSION"),
    "root": ("COBOL_CORE", 600, "COBOL-DATA"),
    # The forms package is the separate BMS/map-resource pipeline. It remains
    # inventoried, but the scheduler deliberately excludes BMS_ARTIFACT.
    "forms": ("BMS_ARTIFACT", 9000, "BMS"),
}


@dataclass(frozen=True)
class Backend:
    area: str
    class_name: str
    backend_fqn: str
    semantic_fqn: str
    source_path: str


def read_java(path: Path) -> str:
    return path.read_text(encoding="iso-8859-1")


def semantic_index(java_root: Path) -> dict[str, str]:
    result: dict[str, str] = {}
    duplicates: set[str] = set()
    for path in java_root.rglob("*.java"):
        text = read_java(path)
        package = PACKAGE_PATTERN.search(text)
        if package is None:
            continue
        simple = path.stem
        fqn = f"{package.group(1)}.{simple}"
        if simple in result and result[simple] != fqn:
            duplicates.add(simple)
        else:
            result[simple] = fqn
    for simple in duplicates:
        result.pop(simple, None)
    return result


def discover(repo_root: Path) -> list[Backend]:
    java_root = repo_root / "naca-trans/src/main/java"
    direct_root = java_root / "generate/java"
    semantics = semantic_index(java_root)
    result: list[Backend] = []

    for path in sorted(direct_root.rglob("*.java")):
        text = read_java(path)
        match = BACKEND_PATTERN.search(text)
        if match is None:
            continue
        class_name, superclass = match.groups()
        package = PACKAGE_PATTERN.search(text)
        if package is None:
            raise ValueError(f"missing package declaration: {path}")
        imports = {
            fqn.rsplit(".", 1)[-1]: fqn
            for fqn in IMPORT_PATTERN.findall(text)
        }
        semantic_fqn = imports.get(superclass) or semantics.get(superclass)
        if semantic_fqn is None:
            raise ValueError(
                f"cannot resolve semantic superclass {superclass} in {path}"
            )
        relative = path.relative_to(direct_root)
        area = relative.parts[0] if len(relative.parts) > 1 else "root"
        result.append(
            Backend(
                area=area,
                class_name=class_name,
                backend_fqn=f"{package.group(1)}.{class_name}",
                semantic_fqn=semantic_fqn,
                source_path=path.relative_to(repo_root).as_posix(),
            )
        )
    return result


def entry_id(backend: Backend) -> str:
    _scope, _priority, label = AREA_CONFIG[backend.area]
    prefix = "BMS-BACKEND" if backend.area == "forms" else "BACKEND"
    if backend.area == "forms":
        return f"{prefix}-{backend.class_name.upper()}"
    return f"{prefix}-{label}-{backend.class_name.upper()}"


def generated_entry(
    backend: Backend,
    ordinal: int,
    dependency: str | None,
) -> dict:
    scope, priority, _label = AREA_CONFIG[backend.area]
    item_id = entry_id(backend)
    status = "production-wired" if backend.area == "st" else "semantic-built"
    if backend.area == "forms":
        blocker = (
            "Tracked as separate BMS/map-resource pipeline debt; excluded from "
            "the COBOL/SQL/CICS migration scheduler."
        )
    else:
        blocker = (
            "Retire this one legacy backend through the unified semantic-tree + "
            "recursive-ST4 assembler; preserve behavior and reduce directBackends by one."
        )
    return {
        "id": item_id,
        "kind": MANAGED_KIND,
        "priority": priority + ordinal,
        "dependencies": [dependency] if dependency else [],
        "verification": [
            './gradlew :naca-trans:test --tests "architecture.DirectBackendInventoryTest"'
        ],
        "expectedDebtDelta": {"directBackends": -1},
        "attempts": 0,
        "blocked": False,
        "scope": scope,
        "sourceSyntax": f"legacy backend {backend.class_name}",
        "parserNode": None,
        "semanticNode": backend.semantic_fqn,
        "assemblerRole": None,
        "manifestBinding": None,
        "template": None,
        "runtimeOperation": None,
        "fixture": None,
        "directBackend": backend.backend_fqn,
        "sourcePath": backend.source_path,
        "productionReachable": True,
        "status": status,
        "blocker": blocker,
        "evidence": [backend.source_path],
    }


def synchronize(data: dict, backends: list[Backend]) -> dict:
    old_entries = data["entries"]
    existing_managed = {
        entry["id"]: entry
        for entry in old_entries
        if entry.get("kind") == MANAGED_KIND
    }
    semantic_to_st = {
        backend.semantic_fqn: entry_id(backend)
        for backend in backends
        if backend.area == "st"
    }

    area_ordinals: dict[str, int] = {}
    live_entries: list[dict] = []
    live_ids: set[str] = set()
    for backend in backends:
        ordinal = area_ordinals.get(backend.area, 0)
        area_ordinals[backend.area] = ordinal + 1
        dependency = None
        if backend.area != "st":
            dependency = semantic_to_st.get(backend.semantic_fqn)
        fresh = generated_entry(backend, ordinal, dependency)
        item_id = fresh["id"]
        live_ids.add(item_id)
        old = existing_managed.get(item_id)
        if old is not None:
            # Inventory-owned fields are refreshed; controller-owned workflow
            # state survives repeated synchronization.
            for key in (
                "status",
                "attempts",
                "blocked",
                "blockedReason",
                "lastAttemptCommit",
                "manifestBinding",
                "template",
                "runtimeOperation",
                "fixture",
                "evidence",
                "blocker",
            ):
                if key in old:
                    fresh[key] = old[key]
        live_entries.append(fresh)

    # Deleted backends retain their terminal history. A non-terminal stale entry
    # is intentionally retained too so the consistency gate can expose it.
    historical = [
        entry
        for item_id, entry in existing_managed.items()
        if item_id not in live_ids
    ]
    unmanaged = [
        entry for entry in old_entries if entry.get("kind") != MANAGED_KIND
    ]
    # Keep ordering independent of whether the currently verified worker has
    # already deleted its source file. The controller advances ledger status
    # only after verification, so synchronization must remain stable in-flight.
    managed = historical + live_entries
    managed.sort(key=lambda entry: (entry.get("priority", 100000), entry["id"]))
    data["entries"] = unmanaged + managed
    return data


def rendered(data: dict) -> str:
    return json.dumps(data, indent=2, ensure_ascii=False) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write", action="store_true", help="update the ledger in place")
    parser.add_argument(
        "--repo-root",
        type=Path,
        default=Path(__file__).resolve().parents[2],
    )
    args = parser.parse_args()
    repo_root = args.repo_root.resolve()
    ledger_path = repo_root / "docs/migration-ledger.json"
    before = ledger_path.read_text(encoding="utf-8")
    data = json.loads(before)
    after = rendered(synchronize(data, discover(repo_root)))

    if args.write:
        ledger_path.write_text(after, encoding="utf-8")
        print(f"synchronized {ledger_path}")
        return 0
    if before != after:
        print(
            "ledger inventory is stale; run "
            "tools/st4-loop/sync_ledger_inventory.py --write",
            file=sys.stderr,
        )
        return 1
    print("ledger inventory is synchronized")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
