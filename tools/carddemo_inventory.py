#!/usr/bin/env python3
"""Generate and verify the pinned CardDemo acceptance inventory."""

from __future__ import annotations

import argparse
import hashlib
import json
import subprocess
import sys
from collections import Counter
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
CORPUS = ROOT / "naca-rt-tests/src/test/resources/carddemo"
INVENTORY = CORPUS / "ACCEPTANCE_INVENTORY.json"
PROVENANCE = CORPUS / "PROVENANCE.json"
STATUSES = {"strictComplete", "stageFeasible", "blocked", "notStarted"}


def summary(programs: list[dict[str, object]]) -> dict[str, object]:
    counts = Counter(str(program["status"]) for program in programs)
    total = len(programs)
    strict = counts["strictComplete"]
    return {
        "total": total,
        "strictComplete": strict,
        "stageFeasible": counts["stageFeasible"],
        "blocked": counts["blocked"],
        "notStarted": counts["notStarted"],
        "completionRate": {
            "numerator": strict,
            "denominator": total,
            "percent": round(strict * 100 / total, 2) if total else 0,
        },
    }


def automation_summary(programs: list[dict[str, object]]) -> dict[str, object]:
    strict = sum(program.get("status") == "strictComplete" for program in programs)
    expected_blockers = sum(
        program.get("status") == "blocked"
        and bool(program.get("probeEvidence", {}).get("automatedRegression"))
        for program in programs
    )
    return {
        "definition": (
            "JUnit scenarios in CardDemoEndToEndAcceptanceTest: strict successes "
            "plus deterministic expected-blocker regressions."
        ),
        "executableScenarios": strict + expected_blockers,
        "strictSuccessScenarios": strict,
        "expectedBlockerScenarios": expected_blockers,
    }


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(65536), b""):
            digest.update(chunk)
    return digest.hexdigest()


def vendored_assets() -> dict[str, Path]:
    assets = {"LICENSE": CORPUS / "LICENSE"}
    app = CORPUS / "app"
    if app.is_dir():
        assets.update({
            path.relative_to(CORPUS).as_posix(): path
            for path in app.rglob("*")
            if path.is_file()
        })
    return dict(sorted(assets.items()))


def require(condition: bool, message: str, errors: list[str]) -> None:
    if not condition:
        errors.append(message)


def validate_upstream(report: dict[str, object], upstream: Path,
                      errors: list[str]) -> None:
    expected_commit = str(report["source"]["commit"])
    result = subprocess.run(
        ["git", "-C", str(upstream), "rev-parse", "HEAD"],
        check=False, capture_output=True, text=True,
    )
    require(result.returncode == 0, "upstream is not a Git checkout", errors)
    if result.returncode != 0:
        return
    require(result.stdout.strip() == expected_commit,
            "upstream commit does not match the pinned report commit", errors)
    actual = sorted(
        path.relative_to(upstream).as_posix()
        for path in (upstream / "app").rglob("*")
        if path.is_file() and path.suffix.lower() == ".cbl"
    )
    expected = sorted(str(item["path"]) for item in report["programs"])
    require(actual == expected,
            "upstream COBOL program set does not match the report denominator", errors)


def validate(report: dict[str, object], provenance: dict[str, object],
             upstream: Path | None) -> list[str]:
    errors: list[str] = []
    programs = report.get("programs", [])
    require(isinstance(programs, list), "programs must be an array", errors)
    if not isinstance(programs, list):
        return errors

    paths = [str(program.get("path")) for program in programs]
    require(len(paths) == len(set(paths)), "program paths must be unique", errors)
    require(all(path.startswith("app/") and path.lower().endswith(".cbl")
                for path in paths),
            "every denominator entry must be an app/**/*.cbl path", errors)
    statuses = [str(program.get("status")) for program in programs]
    require(set(statuses) <= STATUSES, "unknown program status", errors)
    require(report.get("summary") == summary(programs),
            "summary/count/rate fields are not generated from programs", errors)
    require(report.get("automation") == automation_summary(programs),
            "automation counts are not generated from program evidence", errors)
    require(report.get("denominator", {}).get("total") == len(programs),
            "denominator.total does not match programs", errors)

    report_source = report.get("source", {})
    provenance_source = provenance.get("source", {})
    require(report_source.get("url") == provenance_source.get("url"),
            "report and provenance source URLs differ", errors)
    require(report_source.get("commit") == provenance_source.get("commit"),
            "report and provenance commits differ", errors)

    hashes = provenance.get("files", {})
    assets = vendored_assets()
    require(set(hashes) == set(assets),
            "provenance files must enumerate every vendored app asset and LICENSE",
            errors)
    for relative, expected_hash in hashes.items():
        asset = assets.get(relative, CORPUS / relative)
        require(asset.is_file(), f"missing vendored asset: {relative}", errors)
        if asset.is_file():
            require(sha256(asset) == expected_hash,
                    f"vendored asset hash mismatch: {relative}", errors)

    for program in programs:
        name = str(program.get("name"))
        blocker = program.get("firstBlocker")
        require(isinstance(blocker, dict) and bool(blocker.get("code"))
                and bool(blocker.get("detail")),
                f"{name} must have a machine-readable firstBlocker", errors)
        if program.get("status") == "strictComplete":
            require(blocker.get("code") == "NONE",
                    f"{name} strict completion cannot have a blocker", errors)
            evidence = program.get("strictEvidence", {})
            required_pipeline = {
                "vendoredSourceAndProvenance", "recursiveST4",
                "copybookGeneration", "javac", "isolatedNacaRT",
                "exactBusinessOutput",
            }
            require(set(evidence.get("pipeline", [])) == required_pipeline,
                    f"{name} strict pipeline evidence is incomplete", errors)
            for asset in evidence.get("assets", []):
                require(asset in hashes,
                        f"{name} strict asset is absent from provenance: {asset}",
                        errors)
        elif program.get("status") == "notStarted":
            require(blocker.get("code") == "NOT_PROBED",
                    f"{name} notStarted blocker must be NOT_PROBED", errors)
        elif program.get("status") == "blocked":
            evidence = program.get("probeEvidence", {})
            for asset in evidence.get("assets", []):
                require(asset in hashes,
                        f"{name} blocker asset is absent from provenance: {asset}",
                        errors)

    if upstream is not None:
        validate_upstream(report, upstream.resolve(), errors)
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write", action="store_true",
                        help="recompute generated summary fields")
    parser.add_argument("--check", action="store_true",
                        help="validate inventory, provenance, and vendored hashes")
    parser.add_argument("--upstream", type=Path,
                        help="also verify a local pinned CardDemo checkout")
    args = parser.parse_args()
    if not args.write and not args.check:
        parser.error("choose --write and/or --check")

    report = json.loads(INVENTORY.read_text(encoding="utf-8"))
    if args.write:
        report["summary"] = summary(report["programs"])
        report["automation"] = automation_summary(report["programs"])
        report["denominator"]["total"] = len(report["programs"])
        INVENTORY.write_text(
            json.dumps(report, indent=2, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )

    provenance = json.loads(PROVENANCE.read_text(encoding="utf-8"))
    if args.write:
        provenance["files"] = {
            relative: sha256(path)
            for relative, path in vendored_assets().items()
        }
        PROVENANCE.write_text(
            json.dumps(provenance, indent=2, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )
    errors = validate(report, provenance, args.upstream)
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 1
    counts = report["summary"]
    automation = report["automation"]
    print(
        "CardDemo inventory valid: "
        f"strict={counts['strictComplete']}/{counts['total']} "
        f"({counts['completionRate']['percent']:.2f}%), "
        f"stageFeasible={counts['stageFeasible']}, "
        f"blocked={counts['blocked']}, notStarted={counts['notStarted']}, "
        f"automated={automation['executableScenarios']} "
        f"(strict={automation['strictSuccessScenarios']}, "
        f"expectedBlocker={automation['expectedBlockerScenarios']})"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
