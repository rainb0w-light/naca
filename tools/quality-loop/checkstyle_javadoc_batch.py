#!/usr/bin/env python3
"""Insert concise report-driven Javadocs for public Java APIs."""

import argparse
import json
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path


METHOD_RULE = "com.puppycrawl.tools.checkstyle.checks.javadoc.MissingJavadocMethodCheck"
TYPE_RULE = "com.puppycrawl.tools.checkstyle.checks.javadoc.MissingJavadocTypeCheck"
NAME_PATTERN = re.compile(r"'([^']+)'")


def repo_root():
    return Path(subprocess.check_output(
        ["git", "rev-parse", "--show-toplevel"], text=True
    ).strip()).resolve()


def collect_targets(root):
    targets = defaultdict(dict)
    for report in sorted(root.glob("*/build/reports/checkstyle/*.xml")):
        for file_element in ET.parse(report).getroot().findall("file"):
            path = Path(file_element.attrib["name"]).resolve()
            try:
                path.relative_to(root)
            except ValueError:
                continue
            for error in file_element.findall("error"):
                source = error.attrib.get("source")
                if source not in {METHOD_RULE, TYPE_RULE}:
                    continue
                match = NAME_PATTERN.search(error.attrib.get("message", ""))
                name = match.group(1) if match else "API"
                targets[path][int(error.attrib["line"]) - 1] = (source, name)
    return targets


def words(name):
    spaced = re.sub(r"([a-z0-9])([A-Z])", r"\1 \2", name.replace("_", " "))
    return re.sub(r"\s+", " ", spaced).strip().lower()


def method_summary(name):
    label = words(name)
    if name.startswith("get") and len(name) > 3:
        return f"Returns the {words(name[3:])}."
    if name.startswith(("is", "has", "can")) and len(name) > 2:
        prefix = 2 if name.startswith(("is", "has")) else 3
        return f"Returns whether {words(name[prefix:])}."
    actions = {
        "set": "Sets", "add": "Adds", "remove": "Removes", "create": "Creates",
        "new": "Creates", "parse": "Parses", "render": "Renders", "export": "Exports",
        "clear": "Clears", "reset": "Resets", "close": "Closes", "load": "Loads",
        "read": "Reads", "write": "Writes", "find": "Finds", "build": "Builds",
        "validate": "Validates", "convert": "Converts", "update": "Updates",
    }
    for prefix, verb in actions.items():
        if name.lower().startswith(prefix) and len(name) > len(prefix):
            return f"{verb} the {words(name[len(prefix):])}."
    if name == "toString":
        return "Returns a string representation of this value."
    if name == "run":
        return "Runs this operation."
    return f"Executes the {label} operation."


def type_summary(name, declaration):
    label = words(name)
    if " interface " in f" {declaration} ":
        return f"Defines the contract for {label}."
    if re.search(r"\benum\b", declaration):
        return f"Enumerates supported {label} values."
    if name.endswith("Exception"):
        return f"Signals a {label} condition."
    return f"Provides {label} behavior."


def annotation_anchor(lines, declaration_index):
    anchor = declaration_index
    candidate = None
    for index in range(declaration_index - 1, max(-1, declaration_index - 21), -1):
        stripped = lines[index].strip()
        if (not stripped or stripped in {"{", "}"} or ";" in stripped
                or re.search(r"\b(class|interface|enum|record)\b", stripped)):
            break
        if stripped.startswith("@"):
            candidate = index
            anchor = index
            continue
        if candidate is not None:
            break
    return anchor


def apply_batch(targets, apply=False):
    metrics = {"observed": sum(len(rows) for rows in targets.values()),
               "inserted": 0, "filesChanged": 0, "unresolved": 0}
    for path in sorted(targets):
        source = path.read_text(encoding="latin1")
        lines = source.splitlines()
        changed = False
        for line_index, (rule, name) in sorted(targets[path].items(), reverse=True):
            if line_index >= len(lines):
                metrics["unresolved"] += 1
                continue
            if rule == METHOD_RULE and not re.search(
                    rf"\b{re.escape(name)}\s*\(", lines[line_index]
            ):
                relocated = next((index for index in range(line_index + 1,
                    min(len(lines), line_index + 12))
                    if re.search(rf"\b{re.escape(name)}\s*\(", lines[index])), None)
                if relocated is not None:
                    line_index = relocated
            declaration = lines[line_index]
            indentation = declaration[:len(declaration) - len(declaration.lstrip())]
            if rule == TYPE_RULE:
                summary = type_summary(name, declaration)
            elif re.search(
                    rf"\b(?:public|protected|private)\s+{re.escape(name)}\s*\(",
                    declaration,
            ):
                summary = f"Creates a new {words(name)} instance."
            else:
                summary = method_summary(name)
            anchor = annotation_anchor(lines, line_index)
            lines.insert(anchor, f"{indentation}/** {summary} */")
            metrics["inserted"] += 1
            changed = True
        if changed:
            metrics["filesChanged"] += 1
            if apply:
                path.write_text("\n".join(lines) + ("\n" if source.endswith("\n") else ""),
                                encoding="latin1")
    return metrics


def main(argv=None):
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args(argv)
    try:
        result = apply_batch(collect_targets(repo_root()), apply=args.apply)
        result["applied"] = args.apply
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
