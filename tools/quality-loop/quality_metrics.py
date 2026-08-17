#!/usr/bin/env python3
"""Collect and compare deterministic quality-ratchet metrics."""
import argparse
import json
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


METRICS = {
    "checkstyleMain": ("checkstyle/main.xml", "error"),
    "checkstyleTest": ("checkstyle/test.xml", "error"),
    "pmdMain": ("pmd/main.xml", "violation"),
    "pmdTest": ("pmd/test.xml", "violation"),
    "spotbugsMain": ("spotbugs/spotbugsMain.xml", "BugInstance"),
    "spotbugsTest": ("spotbugs/spotbugsTest.xml", "BugInstance"),
}


def repo_root():
    return Path(subprocess.check_output(["git", "rev-parse", "--show-toplevel"], text=True).strip())


def local_name(tag):
    return tag.rsplit("}", 1)[-1]


def parse_xml(path):
    try:
        return ET.parse(path).getroot()
    except (ET.ParseError, OSError) as error:
        raise ValueError(f"malformed XML: {path}: {error}") from error


def count_element(path, wanted):
    return sum(1 for element in parse_xml(path).iter() if local_name(element.tag) == wanted)


def line_counter(path):
    counters = [element for element in list(parse_xml(path))
                if local_name(element.tag) == "counter" and element.attrib.get("type") == "LINE"]
    if len(counters) != 1:
        raise ValueError(f"expected exactly one root LINE counter: {path}")
    try:
        return int(counters[0].attrib["missed"]), int(counters[0].attrib["covered"])
    except (KeyError, ValueError) as error:
        raise ValueError(f"invalid root LINE counter: {path}") from error


def load_baseline(root):
    path = root / "docs/project-quality-baseline.json"
    try:
        return json.loads(path.read_text())
    except (OSError, json.JSONDecodeError) as error:
        raise ValueError(f"cannot read baseline: {error}") from error


def collect(root, baseline):
    static = {}
    missing = []
    for module, expected in baseline["staticAnalysis"].items():
        module_metrics = {}
        for metric, (relative, element) in METRICS.items():
            if metric not in expected:
                continue
            report = root / module / "build/reports" / relative
            if not report.is_file():
                missing.append(str(report.relative_to(root)))
            else:
                module_metrics[metric] = count_element(report, element)
        static[module] = module_metrics
    cpd = root / "build/reports/pmd/cpd.xml"
    if not cpd.is_file():
        missing.append(str(cpd.relative_to(root)))
    missed = covered = 0
    for module in baseline["staticAnalysis"]:
        report = root / module / "build/reports/jacoco/test/jacocoTestReport.xml"
        if not report.is_file():
            missing.append(str(report.relative_to(root)))
        else:
            module_missed, module_covered = line_counter(report)
            missed += module_missed
            covered += module_covered
    if missing:
        raise ValueError("missing quality reports: " + ", ".join(sorted(missing)))
    return {"staticAnalysis": static, "cpdDuplications": count_element(cpd, "duplication"),
            "aggregateLineCoverage": covered / (covered + missed) if covered + missed else 0.0}


def compare(current, baseline):
    failures = []
    improvements = []
    for module, expected in baseline["staticAnalysis"].items():
        actual = current.get("staticAnalysis", {}).get(module, {})
        for metric in expected:
            if metric not in actual:
                failures.append(f"missing current metric: {module}.{metric}")
            elif actual[metric] > expected[metric]:
                failures.append(f"{module}.{metric} grew from {expected[metric]} to {actual[metric]}")
            elif actual[metric] < expected[metric]:
                improvements.append(f"{module}.{metric} decreased from {expected[metric]} to {actual[metric]}")
    if "cpdDuplications" not in current:
        failures.append("missing current metric: cpdDuplications")
    elif current["cpdDuplications"] > baseline["cpdDuplications"]:
        failures.append("CPD duplications grew")
    elif current["cpdDuplications"] < baseline["cpdDuplications"]:
        improvements.append("CPD duplications decreased")
    minimum = baseline["aggregateLineCoverageMinimum"]
    if "aggregateLineCoverage" not in current:
        failures.append("missing current metric: aggregateLineCoverage")
    elif current["aggregateLineCoverage"] < minimum:
        failures.append("aggregate line coverage decreased")
    elif current["aggregateLineCoverage"] > minimum:
        improvements.append("aggregate line coverage improved")
    return {"status": "PASS" if not failures else "FAIL", "regressions": failures, "improvements": improvements}


def main(argv=None):
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)
    collect_parser = sub.add_parser("collect")
    collect_parser.add_argument("--output", type=Path)
    compare_parser = sub.add_parser("compare")
    compare_parser.add_argument("current", type=Path)
    compare_parser.add_argument("--baseline", type=Path)
    args = parser.parse_args(argv)
    try:
        root = repo_root()
        baseline = load_baseline(root)
        if args.command == "collect":
            result = collect(root, baseline)
            output = json.dumps(result, sort_keys=True, indent=2) + "\n"
            if args.output:
                args.output.write_text(output)
            else:
                print(output, end="")
            return 0
        current = json.loads(args.current.read_text())
        if args.baseline:
            baseline = json.loads(args.baseline.read_text())
        comparison = compare(current, baseline)
        print(json.dumps(comparison, sort_keys=True))
        return 0 if comparison["status"] == "PASS" else 1
    except (OSError, ValueError, subprocess.CalledProcessError, KeyError, TypeError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1


if __name__ == "__main__":
    sys.exit(main())
