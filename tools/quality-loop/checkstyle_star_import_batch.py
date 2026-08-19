#!/usr/bin/env python3
"""Expand report-located Java wildcard imports with a dependency type index."""

import argparse
import json
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile
from collections import defaultdict
from pathlib import Path


RULE_SOURCE = "com.puppycrawl.tools.checkstyle.checks.imports.AvoidStarImportCheck"
IMPORT_PATTERN = re.compile(r"^(\s*)import\s+(static\s+)?([\w.]+)\.\*\s*;+\s*$")
PACKAGE_PATTERN = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
EXPLICIT_IMPORT_PATTERN = re.compile(
    r"^\s*import\s+(?!static\s)([\w.]+)\.([A-Za-z_$][\w$]*)\s*;", re.MULTILINE
)
TYPE_PATTERN = re.compile(r"(?<![\w$.])([A-Z][A-Za-z0-9_$]*)\b")
DECLARATION_PATTERN = re.compile(
    r"\b(?:class|interface|enum|record|@interface)\s+([A-Za-z_$][\w$]*)"
)


def repo_root():
    """Return the active Git worktree root."""
    return Path(
        subprocess.check_output(["git", "rev-parse", "--show-toplevel"], text=True).strip()
    ).resolve()


def collect_targets(root):
    """Collect wildcard-import report locations grouped by source file."""
    targets = defaultdict(set)
    for report in sorted(root.glob("*/build/reports/checkstyle/*.xml")):
        for file_element in ET.parse(report).getroot().findall("file"):
            path = Path(file_element.attrib["name"]).resolve()
            try:
                path.relative_to(root)
            except ValueError:
                continue
            for error in file_element.findall("error"):
                if error.attrib.get("source") == RULE_SOURCE:
                    targets[path].add(int(error.attrib["line"]) - 1)
    return targets


def mask_java(source):
    """Mask Java comments and literals while preserving positions and newlines."""
    output = list(source)
    state = "code"
    escaped = False
    index = 0
    while index < len(source):
        if state == "code":
            if source.startswith("//", index):
                state = "line-comment"
            elif source.startswith("/*", index):
                state = "block-comment"
            elif source.startswith('"""', index):
                state = "text-block"
                for offset in range(3):
                    output[index + offset] = " "
                index += 3
                continue
            elif source[index] == '"':
                state = "string"
                output[index] = " "
            elif source[index] == "'":
                state = "char"
                output[index] = " "
        elif state == "line-comment":
            if source[index] == "\n":
                state = "code"
            else:
                output[index] = " "
        elif state == "block-comment":
            if source.startswith("*/", index):
                output[index] = output[index + 1] = " "
                index += 2
                state = "code"
                continue
            if source[index] != "\n":
                output[index] = " "
        elif state == "text-block":
            if source.startswith('"""', index):
                for offset in range(3):
                    output[index + offset] = " "
                index += 3
                state = "code"
                continue
            if source[index] != "\n":
                output[index] = " "
        else:
            output[index] = " " if source[index] != "\n" else "\n"
            if escaped:
                escaped = False
            elif source[index] == "\\":
                escaped = True
            elif (state == "string" and source[index] == '"') or (
                state == "char" and source[index] == "'"
            ):
                state = "code"
        index += 1
    return "".join(output)


def source_type_index(root, wanted_packages):
    """Index top-level source type names by package."""
    index = defaultdict(set)
    for path in root.glob("*/src/**/*.java"):
        source = path.read_text(encoding="latin1")
        package = PACKAGE_PATTERN.search(source)
        if package and package.group(1) in wanted_packages:
            index[package.group(1)].add(path.stem)
    return index


def archive_paths():
    """Yield local dependency archives and JDK module archives."""
    gradle_homes = []
    configured_gradle_home = os.environ.get("GRADLE_USER_HOME")
    if configured_gradle_home:
        gradle_homes.append(Path(configured_gradle_home))
    gradle_homes.extend((Path.home() / ".gradle", Path("/Volumes/AppData/.gradle")))
    seen = set()
    for gradle_home in gradle_homes:
        cache = gradle_home / "caches/modules-2/files-2.1"
        if cache in seen or not cache.is_dir():
            continue
        seen.add(cache)
        yield from cache.glob("**/*.jar")
    maven_homes = (Path.home() / ".m2/repository", Path("/Volumes/AppData/.m2/repository"))
    for repository in maven_homes:
        if repository.is_dir():
            yield from repository.glob("**/*.jar")
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        home = Path(java_home)
    else:
        javac = Path(subprocess.check_output(["/usr/bin/which", "javac"], text=True).strip())
        resolved = javac.resolve()
        home = resolved.parents[1]
    jmods = home / "jmods"
    if not jmods.is_dir():
        jmods = home / "libexec/openjdk.jdk/Contents/Home/jmods"
    yield from jmods.glob("*.jmod")


def add_archive_types(index, wanted_packages, archives=None):
    """Add top-level types from dependency archives for requested packages only."""
    for archive in archives if archives is not None else archive_paths():
        try:
            with zipfile.ZipFile(archive) as jar:
                for entry in jar.namelist():
                    name = entry.removeprefix("classes/")
                    if not name.endswith(".class") or "$" in name:
                        continue
                    package = name.rsplit("/", 1)[0].replace("/", ".") if "/" in name else ""
                    if package in wanted_packages:
                        index[package].add(Path(name).stem)
        except (OSError, zipfile.BadZipFile):
            continue


def referenced_types(source):
    """Return unqualified type-like identifiers needed by imports."""
    masked = mask_java(source)
    code = "\n".join(
        line for line in masked.splitlines()
        if not re.match(r"^\s*(?:package|import)\b", line)
    )
    return set(TYPE_PATTERN.findall(code))


def imported_packages(targets):
    """Return non-static wildcard packages from report-scoped files."""
    packages = set()
    for path in targets:
        for line in path.read_text(encoding="latin1").splitlines():
            match = IMPORT_PATTERN.match(line)
            if match and not match.group(2):
                packages.add(match.group(3))
    return packages


def apply_batch(targets, type_index, apply=False):
    """Expand report-located non-static wildcard imports."""
    metrics = {
        "observed": sum(len(lines) for lines in targets.values()),
        "expanded": 0,
        "staticDeferred": 0,
        "unresolved": 0,
        "filesChanged": 0,
    }
    for path in sorted(targets):
        source = path.read_text(encoding="latin1")
        lines = source.splitlines()
        candidates = referenced_types(source)
        declared = set(DECLARATION_PATTERN.findall(mask_java(source)))
        explicit = {match.group(2) for match in EXPLICIT_IMPORT_PATTERN.finditer(source)}
        changed = False
        # Earlier batches can move imports away from the line recorded in the XML
        # report.  The report still scopes the eligible files; rescan their import
        # blocks so this batch composes safely with those edits.
        wildcard_lines = [
            index for index, line in enumerate(lines) if IMPORT_PATTERN.match(line)
        ]
        metrics["unresolved"] += abs(len(targets[path]) - len(wildcard_lines))
        for line_index in reversed(wildcard_lines):
            match = IMPORT_PATTERN.match(lines[line_index])
            indentation, static_marker, package = match.groups()
            if static_marker:
                metrics["staticDeferred"] += 1
                continue
            names = sorted((candidates & type_index.get(package, set())) - declared - explicit)
            replacement = [f"{indentation}import {package}.{name};" for name in names]
            lines[line_index : line_index + 1] = replacement
            metrics["expanded"] += 1
            changed = True
        if changed:
            metrics["filesChanged"] += 1
            if apply:
                path.write_text("\n".join(lines) + ("\n" if source.endswith("\n") else ""), encoding="latin1")
    return metrics


def main(argv=None):
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args(argv)
    try:
        root = repo_root()
        targets = collect_targets(root)
        packages = imported_packages(targets)
        index = source_type_index(root, packages)
        add_archive_types(index, packages)
        result = apply_batch(targets, index, apply=args.apply)
        result.update({"applied": args.apply, "packagesIndexed": len(packages)})
        payload = json.dumps(result, indent=2, sort_keys=True) + "\n"
        if args.output:
            args.output.write_text(payload, encoding="utf-8")
        print(payload, end="")
        return 0 if result["unresolved"] == 0 else 2
    except (OSError, ET.ParseError, subprocess.CalledProcessError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1


if __name__ == "__main__":
    sys.exit(main())
