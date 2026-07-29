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
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path


BACKEND_PATTERN = re.compile(
    r"\bpublic\s+(?:abstract\s+)?class\s+(\w+)\s+extends\s+"
    r"((?:CEntity\w*|CBaseActionEntity|CDataEntity))\b"
)
PACKAGE_PATTERN = re.compile(r"^package\s+([\w.]+);", re.MULTILINE)
IMPORT_PATTERN = re.compile(r"^import\s+([\w.]+);", re.MULTILINE)
MANAGED_KIND = "DIRECT_BACKEND_RETIREMENT"
ARCHITECTURE_KIND = "ARCHITECTURE_DEBT"

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


@dataclass(frozen=True)
class ArchitectureDebt:
    item_id: str
    scope: str
    priority: int
    source_path: str
    test_name: str
    semantic_fqn: str | None


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
                # Priority is a stable task identity attribute once assigned.
                # Recomputing ordinals after an earlier backend is deleted would
                # renumber every later entry and make an in-flight retirement
                # fail the byte-for-byte synchronization gate.
                "priority",
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


def _architecture_scope(test_name: str) -> str:
    if "/semantic/SQL/" in test_name:
        return "EMBEDDED_SQL"
    if "/semantic/CICS/" in test_name:
        return "EMBEDDED_CICS"
    return "COBOL_CORE"


def _architecture_id(scope: str, test_name: str) -> str:
    if test_name == "backendHasACompleteDeclarativeSemanticTemplateManifest()":
        return "ARCH-COBOL-MANIFEST-COVERAGE"
    stem = Path(test_name).stem
    label = {
        "COBOL_CORE": "COBOL",
        "EMBEDDED_SQL": "SQL",
        "EMBEDDED_CICS": "CICS",
    }[scope]
    category = "SEMANTIC" if "/semantic/" in test_name else "INFRA"
    safe = re.sub(r"[^A-Z0-9]+", "-", stem.upper()).strip("-")
    return f"ARCH-{label}-{category}-{safe}"


def discover_architecture_debt(repo_root: Path) -> list[ArchitectureDebt] | None:
    """Read the latest scoped finalArchitectureCheck report, when available.

    Build output is deliberately optional: a clean clone uses the checked-in
    ledger, while a verifier run refreshes this generated queue from the
    authoritative JUnit report.
    """
    report = (
        repo_root
        / "naca-trans/build/test-results/finalArchitectureCheck"
        / "TEST-architecture.FinalArchitectureContractTest.xml"
    )
    if not report.is_file():
        return None

    root = ET.parse(report).getroot()
    raw: list[tuple[int, str]] = []
    for testcase in root.findall("testcase"):
        failure = testcase.find("failure")
        if failure is None:
            continue
        name = testcase.get("name", "")
        # The scoped contract already excludes BMS/forms and FPac. Refuse to
        # enqueue them if an older report is encountered.
        if "/forms/" in name or "FPac" in name:
            continue
        message = failure.get("message", "")
        count_match = re.search(r"violations \((\d+)\)", message)
        violation_count = int(count_match.group(1)) if count_match else 1
        raw.append((violation_count, name))

    scope_rank = {"EMBEDDED_CICS": 0, "EMBEDDED_SQL": 1, "COBOL_CORE": 2}
    raw.sort(key=lambda row: (
        row[0], scope_rank[_architecture_scope(row[1])], row[1]))
    debts: list[ArchitectureDebt] = []
    for ordinal, (_count, name) in enumerate(raw):
        scope = _architecture_scope(name)
        if name == "backendHasACompleteDeclarativeSemanticTemplateManifest()":
            source_path = (
                "naca-trans/src/main/resources/templates/java/"
                "semantic-bindings.properties"
            )
            semantic_fqn = None
        else:
            source_path = f"naca-trans/{name}"
            semantic_fqn = None
            marker = "src/main/java/semantic/"
            if marker in name:
                suffix = name.split(marker, 1)[1].removesuffix(".java")
                semantic_fqn = "semantic." + suffix.replace("/", ".")
        debts.append(ArchitectureDebt(
            item_id=_architecture_id(scope, name),
            scope=scope,
            priority=1000 + ordinal,
            source_path=source_path,
            test_name=name,
            semantic_fqn=semantic_fqn,
        ))
    return debts


def generated_architecture_entry(debt: ArchitectureDebt) -> dict:
    return {
        "id": debt.item_id,
        "kind": ARCHITECTURE_KIND,
        "priority": debt.priority,
        "dependencies": [],
        "verification": [],
        "expectedDebtDelta": {"failures": -1},
        "attempts": 0,
        "blocked": False,
        "scope": debt.scope,
        "sourceSyntax": f"finalArchitectureCheck: {debt.test_name}",
        "parserNode": None,
        "semanticNode": debt.semantic_fqn,
        "assemblerRole": None,
        "manifestBinding": None,
        "template": None,
        "runtimeOperation": None,
        "fixture": None,
        "directBackend": None,
        "sourcePath": debt.source_path,
        "productionReachable": True,
        "status": "semantic-built",
        "blocker": (
            "Remove every finalArchitectureCheck violation reported for this "
            "single source artifact while preserving behavior. Semantic code "
            "must retain only target-neutral state/getters; Java formatting and "
            "selection belong in the recursive ST4 assembler."
        ),
        "evidence": [debt.test_name],
    }


def synchronize_architecture(
    data: dict, debts: list[ArchitectureDebt] | None
) -> dict:
    if debts is None:
        return data
    old_entries = data["entries"]
    existing = {
        entry["id"]: entry
        for entry in old_entries
        if entry.get("kind") == ARCHITECTURE_KIND
    }
    live: list[dict] = []
    live_ids: set[str] = set()
    for debt in debts:
        fresh = generated_architecture_entry(debt)
        live_ids.add(debt.item_id)
        old = existing.get(debt.item_id)
        if old is not None:
            for key in (
                "priority",
                "status",
                "attempts",
                "blocked",
                "blockedReason",
                "lastAttemptCommit",
                "evidence",
            ):
                if key in old:
                    fresh[key] = old[key]
        live.append(fresh)

    historical = [
        entry for item_id, entry in existing.items()
        if item_id not in live_ids
    ]
    architecture = historical + live
    architecture.sort(key=lambda entry: (
        entry.get("priority", 100000), entry["id"]))
    data["entries"] = [
        entry for entry in old_entries
        if entry.get("kind") != ARCHITECTURE_KIND
    ] + architecture
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
    data = synchronize(data, discover(repo_root))
    data = synchronize_architecture(data, discover_architecture_debt(repo_root))
    after = rendered(data)

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
