#!/usr/bin/env python3
"""Eliminate report-located FileTabCharacter findings without changing literals."""

import argparse
import json
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


FILE_TAB_SOURCE = (
    "com.puppycrawl.tools.checkstyle.checks.whitespace.FileTabCharacterCheck"
)


def repo_root():
    """Return the active Git worktree root."""
    output = subprocess.check_output(
        ["git", "rev-parse", "--show-toplevel"], text=True
    )
    return Path(output.strip()).resolve()


def checkstyle_reports(root):
    """Find generated Checkstyle reports in stable order."""
    return sorted(root.glob("*/build/reports/checkstyle/*.xml"))


def collect_files(root, reports):
    """Return source files with an observed FileTabCharacter finding."""
    root = root.resolve()
    paths = set()
    for report in reports:
        document = ET.parse(report).getroot()
        for file_element in document.findall("file"):
            if not any(
                error.attrib.get("source") == FILE_TAB_SOURCE
                for error in file_element.findall("error")
            ):
                continue
            path = Path(file_element.attrib["name"]).resolve()
            try:
                path.relative_to(root)
            except ValueError:
                continue
            paths.add(path)
    return sorted(paths)


def transform_tabs(source, tab_width=4):
    """Expand non-literal tabs and escape tabs in ordinary Java literals."""
    output = []
    state = "code"
    escaped = False
    column = 0
    index = 0
    metrics = {
        "expanded": 0,
        "escapedInString": 0,
        "escapedInChar": 0,
        "trailingWhitespaceLines": 0,
    }

    def append(value):
        nonlocal column
        output.append(value)
        if value.endswith("\n"):
            column = len(value.rsplit("\n", 1)[-1])
        else:
            column += len(value)

    while index < len(source):
        current = source[index]
        if state == "code":
            if source.startswith("//", index):
                append("//")
                state = "line_comment"
                index += 2
                continue
            if source.startswith("/*", index):
                append("/*")
                state = "block_comment"
                index += 2
                continue
            if source.startswith('"""', index):
                append('"""')
                state = "text_block"
                index += 3
                continue
            if current == '"':
                append(current)
                state = "string"
                escaped = False
                index += 1
                continue
            if current == "'":
                append(current)
                state = "char"
                escaped = False
                index += 1
                continue
        elif state == "line_comment" and current == "\n":
            state = "code"
        elif state == "block_comment" and source.startswith("*/", index):
            append("*/")
            state = "code"
            index += 2
            continue
        elif state in {"string", "char"}:
            if current == "\t":
                append("\\t")
                metrics["escapedInString" if state == "string" else "escapedInChar"] += 1
                index += 1
                continue
            if escaped:
                escaped = False
            elif current == "\\":
                escaped = True
            elif (state == "string" and current == '"') or (
                state == "char" and current == "'"
            ):
                state = "code"
        elif state == "text_block":
            if current == "\t":
                raise ValueError("tab inside Java text block requires semantic review")
            if source.startswith('"""', index):
                append('"""')
                state = "code"
                index += 3
                continue

        if current == "\t":
            spaces = tab_width - column % tab_width
            append(" " * spaces)
            metrics["expanded"] += 1
        else:
            append(current)
        index += 1

    if state in {"string", "char", "text_block", "block_comment"}:
        raise ValueError(f"unterminated Java lexical state: {state}")
    rendered, trimmed = trim_trailing_whitespace("".join(output))
    metrics["trailingWhitespaceLines"] = trimmed
    return rendered, metrics


def trim_trailing_whitespace(source):
    """Trim line-end spaces except inside Java text blocks."""
    output = []
    state = "code"
    trimmed = 0
    for line in source.splitlines(keepends=True):
        has_newline = line.endswith("\n")
        content = line[:-1] if has_newline else line
        index = 0
        escaped = False
        line_comment = False
        while index < len(content):
            current = content[index]
            if line_comment:
                break
            if state == "code":
                if content.startswith("//", index):
                    line_comment = True
                    break
                if content.startswith("/*", index):
                    state = "block_comment"
                    index += 2
                    continue
                if content.startswith('"""', index):
                    state = "text_block"
                    index += 3
                    continue
                if current == '"':
                    state = "string"
                    escaped = False
                elif current == "'":
                    state = "char"
                    escaped = False
            elif state == "block_comment":
                if content.startswith("*/", index):
                    state = "code"
                    index += 2
                    continue
            elif state in {"string", "char"}:
                if escaped:
                    escaped = False
                elif current == "\\":
                    escaped = True
                elif (state == "string" and current == '"') or (
                    state == "char" and current == "'"
                ):
                    state = "code"
            elif state == "text_block" and content.startswith('"""', index):
                state = "code"
                index += 3
                continue
            index += 1
        if state != "text_block":
            cleaned = content.rstrip(" ")
            if cleaned != content:
                trimmed += 1
            content = cleaned
        output.append(content + ("\n" if has_newline else ""))
    return "".join(output), trimmed


def apply_batch(paths, tab_width=4, apply=False):
    """Plan or apply the tab normalization batch."""
    metrics = {
        "filesObserved": len(paths),
        "filesChanged": 0,
        "tabsExpanded": 0,
        "tabsEscapedInString": 0,
        "tabsEscapedInChar": 0,
        "trailingWhitespaceLinesTrimmed": 0,
        "remainingTabs": 0,
    }
    for path in paths:
        source = path.read_text(encoding="latin1")
        rendered, transformed = transform_tabs(source, tab_width)
        metrics["tabsExpanded"] += transformed["expanded"]
        metrics["tabsEscapedInString"] += transformed["escapedInString"]
        metrics["tabsEscapedInChar"] += transformed["escapedInChar"]
        metrics["trailingWhitespaceLinesTrimmed"] += transformed[
            "trailingWhitespaceLines"
        ]
        metrics["remainingTabs"] += rendered.count("\t")
        if rendered != source:
            metrics["filesChanged"] += 1
            if apply:
                path.write_text(rendered, encoding="latin1")
    return metrics


def main(argv=None):
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true")
    parser.add_argument("--tab-width", type=int, default=4)
    parser.add_argument("--output", type=Path)
    args = parser.parse_args(argv)
    try:
        if args.tab_width < 1:
            raise ValueError("tab width must be positive")
        root = repo_root()
        reports = checkstyle_reports(root)
        if not reports:
            raise ValueError("no Checkstyle reports found; run Checkstyle first")
        result = apply_batch(
            collect_files(root, reports), args.tab_width, apply=args.apply
        )
        result.update({"applied": args.apply, "tabWidth": args.tab_width})
        payload = json.dumps(result, indent=2, sort_keys=True) + "\n"
        if args.output:
            args.output.write_text(payload, encoding="utf-8")
        print(payload, end="")
        return 0 if result["remainingTabs"] == 0 else 2
    except (OSError, ValueError, ET.ParseError, subprocess.CalledProcessError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1


if __name__ == "__main__":
    sys.exit(main())
