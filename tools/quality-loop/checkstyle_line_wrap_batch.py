#!/usr/bin/env python3
"""Wrap report-located Java argument lists that exceed Checkstyle's line limit."""

import argparse
import json
import re
import subprocess
import sys
import textwrap
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path


LINE_LENGTH_SOURCE = "com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck"


def repo_root():
    """Return the active Git worktree root."""
    output = subprocess.check_output(
        ["git", "rev-parse", "--show-toplevel"], text=True
    )
    return Path(output.strip()).resolve()


def checkstyle_reports(root):
    """Find generated Checkstyle XML reports in stable order."""
    return sorted(root.glob("*/build/reports/checkstyle/*.xml"))


def collect_targets(root, reports):
    """Collect observed LineLength source locations by file."""
    root = root.resolve()
    targets = defaultdict(set)
    for report in reports:
        document = ET.parse(report).getroot()
        for file_element in document.findall("file"):
            path = Path(file_element.attrib["name"]).resolve()
            try:
                path.relative_to(root)
            except ValueError:
                continue
            for error in file_element.findall("error"):
                if error.attrib.get("source") == LINE_LENGTH_SOURCE:
                    targets[path].add(int(error.attrib["line"]) - 1)
    return targets


def parenthesis_pairs(line):
    """Return balanced parenthesis pairs outside Java literals and comments."""
    pairs = []
    stack = []
    quote = None
    escaped = False
    index = 0
    while index < len(line):
        current = line[index]
        if quote is not None:
            if escaped:
                escaped = False
            elif current == "\\":
                escaped = True
            elif current == quote:
                quote = None
            index += 1
            continue
        if current in {'"', "'"}:
            quote = current
        elif line.startswith("//", index) or line.startswith("/*", index):
            break
        elif current == "(":
            stack.append(index)
        elif current == ")":
            if not stack:
                return []
            pairs.append((stack.pop(), index))
        index += 1
    return pairs if not stack and quote is None else []


def split_top_level_arguments(body):
    """Split a parenthesized body on safe top-level commas."""
    arguments = []
    start = 0
    parentheses = brackets = braces = angles = 0
    quote = None
    escaped = False
    for index, current in enumerate(body):
        if quote is not None:
            if escaped:
                escaped = False
            elif current == "\\":
                escaped = True
            elif current == quote:
                quote = None
            continue
        if current in {'"', "'"}:
            quote = current
        elif current == "(":
            parentheses += 1
        elif current == ")":
            parentheses -= 1
        elif current == "[":
            brackets += 1
        elif current == "]":
            brackets -= 1
        elif current == "{":
            braces += 1
        elif current == "}":
            braces -= 1
        elif current == "<":
            angles += 1
        elif current == ">" and angles:
            angles -= 1
        elif (
            current == ","
            and parentheses == brackets == braces == angles == 0
        ):
            arguments.append(body[start:index].strip())
            start = index + 1
    arguments.append(body[start:].strip())
    if len(arguments) < 2 or any(not argument for argument in arguments):
        return []
    return arguments


def wrap_argument_list(line, maximum=140):
    """Return a safe multi-line argument-list replacement or an empty list."""
    indentation = line[: len(line) - len(line.lstrip())]
    candidates = []
    for opening, closing in parenthesis_pairs(line):
        arguments = split_top_level_arguments(line[opening + 1 : closing])
        if not arguments:
            continue
        prefix = line[: opening + 1].rstrip()
        suffix = line[closing + 1 :].rstrip()
        continuation = indentation + "    "
        replacement = [prefix]
        replacement.extend(
            continuation + argument + ("," if index < len(arguments) - 1 else "")
            for index, argument in enumerate(arguments)
        )
        closing_text = ")" + suffix
        replacement[-1] += closing_text
        if len(replacement[-1]) > maximum:
            replacement[-1] = continuation + arguments[-1]
            replacement.append(indentation + closing_text)
        if all(len(item) <= maximum for item in replacement):
            candidates.append(replacement)
    if not candidates:
        return []
    return min(candidates, key=lambda item: (max(map(len, item)), len(item)))


def operator_positions(line, operators):
    """Locate selected Java operators outside literals and comments."""
    positions = []
    quote = None
    escaped = False
    index = 0
    ordered = sorted(operators, key=len, reverse=True)
    while index < len(line):
        current = line[index]
        if quote is not None:
            if escaped:
                escaped = False
            elif current == "\\":
                escaped = True
            elif current == quote:
                quote = None
            index += 1
            continue
        if current in {'"', "'"}:
            quote = current
            index += 1
            continue
        if line.startswith("//", index) or line.startswith("/*", index):
            break
        matched = next((item for item in ordered if line.startswith(item, index)), None)
        if matched == "+" and (
            line.startswith("++", index)
            or (index and line[index - 1] in "+=")
            or line[index + 1 : index + 2] in {"+", "="}
        ):
            matched = None
        if matched:
            positions.append((index, matched))
            index += len(matched)
        else:
            index += 1
    return positions


def wrap_operators(line, operators, maximum=140):
    """Greedily wrap a binary-operator chain without changing associativity."""
    positions = operator_positions(line, operators)
    if not positions:
        return []
    indentation = line[: len(line) - len(line.lstrip())]
    first_position = positions[0][0]
    first = line[:first_position].rstrip()
    if not first:
        return []
    pieces = []
    for index, (position, operator) in enumerate(positions):
        end = positions[index + 1][0] if index + 1 < len(positions) else len(line)
        value = line[position + len(operator) : end].strip()
        if not value:
            return []
        pieces.append((operator, value))
    replacement = [first]
    continuation = indentation + "    "
    for operator, value in pieces:
        inline = f" {operator} {value}"
        if len(replacement[-1] + inline) <= maximum:
            replacement[-1] += inline
        else:
            replacement.append(f"{continuation}{operator} {value}")
    if len(replacement) < 2 or any(len(item) > maximum for item in replacement):
        return []
    return replacement


def wrap_continuation_operators(line, operators, maximum=140):
    """Wrap an operator chain whose first token continues a prior line."""
    positions = operator_positions(line, operators)
    indentation = line[: len(line) - len(line.lstrip())]
    if not positions or positions[0][0] != len(indentation):
        return []
    pieces = []
    for index, (position, operator) in enumerate(positions):
        end = positions[index + 1][0] if index + 1 < len(positions) else len(line)
        value = line[position + len(operator) : end].strip()
        if not value:
            return []
        pieces.append((operator, value))
    replacement = []
    for operator, value in pieces:
        inline = f" {operator} {value}"
        if replacement and len(replacement[-1] + inline) <= maximum:
            replacement[-1] += inline
        else:
            replacement.append(f"{indentation}{operator} {value}")
    if len(replacement) < 2 or any(len(item) > maximum for item in replacement):
        return []
    return replacement


def split_semicolon_statements(line, maximum=140):
    """Split multiple same-line statements, excluding for-loop headers."""
    if re.search(r"\bfor\s*\(", line):
        return []
    positions = []
    quote = None
    escaped = False
    parentheses = 0
    for index, current in enumerate(line):
        if quote is not None:
            if escaped:
                escaped = False
            elif current == "\\":
                escaped = True
            elif current == quote:
                quote = None
            continue
        if current in {'"', "'"}:
            quote = current
        elif current == "(":
            parentheses += 1
        elif current == ")":
            parentheses -= 1
        elif current == ";" and parentheses == 0:
            positions.append(index)
    if len(positions) < 2:
        return []
    indentation = line[: len(line) - len(line.lstrip())]
    statements = []
    start = 0
    for position in positions:
        statement = line[start : position + 1].strip()
        if statement:
            statements.append(indentation + statement)
        start = position + 1
    remainder = line[start:].strip()
    if remainder:
        statements.append(indentation + remainder)
    if len(statements) < 2 or any(len(item) > maximum for item in statements):
        return []
    return statements


def string_literal_spans(line):
    """Return ordinary Java string literal spans outside comments and chars."""
    spans = []
    quote = None
    start = None
    escaped = False
    index = 0
    while index < len(line):
        current = line[index]
        if quote is not None:
            if escaped:
                escaped = False
            elif current == "\\":
                escaped = True
            elif current == quote:
                if quote == '"':
                    spans.append((start, index + 1))
                quote = None
                start = None
            index += 1
            continue
        if line.startswith("//", index) or line.startswith("/*", index):
            break
        if current in {'"', "'"}:
            quote = current
            start = index
        index += 1
    return spans


def wrap_long_string_literal(line, maximum=140):
    """Split one long ordinary string at spaces, preserving its exact value."""
    spans = string_literal_spans(line)
    if not spans:
        return []
    start, end = max(spans, key=lambda span: span[1] - span[0])
    content = line[start + 1 : end - 1]
    if " " not in content:
        return []
    prefix = line[:start]
    suffix = line[end:].rstrip()
    indentation = line[: len(line) - len(line.lstrip())]
    continuation = indentation + "    "
    remaining = content
    current_prefix = prefix
    replacement = []
    while remaining:
        final = current_prefix + '"' + remaining + '"' + suffix
        if len(final) <= maximum:
            replacement.append(final)
            break
        capacity = maximum - len(current_prefix) - len('"" +')
        if capacity < 1:
            return []
        cut = remaining.rfind(" ", 0, capacity)
        if cut < 0:
            return []
        cut += 1
        replacement.append(current_prefix + '"' + remaining[:cut] + '" +')
        remaining = remaining[cut:]
        current_prefix = continuation
    if len(replacement) < 2 or any(len(item) > maximum for item in replacement):
        return []
    return replacement


def split_inline_method_body(line, maximum=140):
    """Expand a simple one-line method body without changing its statement."""
    match = re.match(r"^(\s*)(.+\))\s*\{\s*(.+;)\s*\}\s*$", line)
    if not match:
        return []
    indentation, declaration, statement = match.groups()
    replacement = [
        indentation + declaration.rstrip(),
        indentation + "{",
        indentation + "    " + statement.strip(),
        indentation + "}",
    ]
    if any(len(item) > maximum for item in replacement):
        return []
    return replacement


def split_assignment(line, maximum=140):
    """Move a long assignment RHS to a continuation line."""
    quote = None
    escaped = False
    assignment = None
    for index, current in enumerate(line):
        if quote is not None:
            if escaped:
                escaped = False
            elif current == "\\":
                escaped = True
            elif current == quote:
                quote = None
            continue
        if current in {'"', "'"}:
            quote = current
        elif line.startswith("//", index) or line.startswith("/*", index):
            break
        elif current == "=" and line[index + 1 : index + 2] != "=":
            previous = line[index - 1 : index]
            if previous in "+-*/%&|^":
                assignment = index + 1
                break
            if previous not in "!<>=":
                assignment = index
                break
    if assignment is None:
        return []
    indentation = line[: len(line) - len(line.lstrip())]
    replacement = [
        line[:assignment].rstrip(),
        indentation + "        " + line[assignment:].strip(),
    ]
    if any(len(item) > maximum for item in replacement):
        return []
    return replacement


def split_commented_assignment(line, maximum=140):
    """Split an assignment and promote its trailing line comment."""
    comment = line.find("//")
    if comment < 0:
        return []
    code = line[:comment].rstrip()
    indentation = line[: len(line) - len(line.lstrip())]
    split_code = split_assignment(code, maximum)
    if not split_code:
        return []
    replacement = [indentation + line[comment:].strip(), *split_code]
    if any(len(item) > maximum for item in replacement):
        return []
    return replacement


def split_comma_sequence(line, maximum=140):
    """Split a continued declaration/call whose opening parenthesis is prior."""
    parts = split_top_level_arguments(line.strip())
    if not parts:
        return []
    indentation = line[: len(line) - len(line.lstrip())]
    replacement = [
        indentation + part + ("," if index < len(parts) - 1 else "")
        for index, part in enumerate(parts)
    ]
    if any(len(item) > maximum for item in replacement):
        return []
    return replacement


def wrap_block_comment(line, maximum=140):
    """Wrap a line wholly contained in a block comment."""
    indentation = line[: len(line) - len(line.lstrip())]
    content = line.strip()
    wrapped = textwrap.wrap(
        content,
        width=maximum - len(indentation),
        break_long_words=False,
        break_on_hyphens=False,
    )
    replacement = [indentation + item for item in wrapped]
    if len(replacement) < 2 or any(len(item) > maximum for item in replacement):
        return []
    return replacement


def block_comment_only_lines(source):
    """Locate lines whose first non-whitespace character is block-comment text."""
    result = set()
    in_block = False
    for line_index, line in enumerate(source.splitlines()):
        stripped = line.lstrip()
        if in_block or stripped.startswith("/*"):
            result.add(line_index)
        index = 0
        quote = None
        escaped = False
        while index < len(line):
            if in_block:
                closing = line.find("*/", index)
                if closing < 0:
                    break
                in_block = False
                index = closing + 2
                continue
            current = line[index]
            if quote is not None:
                if escaped:
                    escaped = False
                elif current == "\\":
                    escaped = True
                elif current == quote:
                    quote = None
                index += 1
                continue
            if line.startswith("//", index):
                break
            if line.startswith("/*", index):
                in_block = True
                index += 2
            elif current in {'"', "'"}:
                quote = current
                index += 1
            else:
                index += 1
    return result


def comment_targets(targets):
    """Keep only report targets wholly within a block comment."""
    filtered = defaultdict(set)
    for path, line_indexes in targets.items():
        source = path.read_text(encoding="latin1")
        eligible = block_comment_only_lines(source)
        filtered[path] = line_indexes & eligible
    return filtered


def apply_batch(targets, transformer, maximum=140, apply=False):
    """Plan or apply argument-list wrapping and return batch metrics."""
    metrics = {
        "observed": sum(len(lines) for lines in targets.values()),
        "wrapped": 0,
        "skipped": 0,
        "filesChanged": 0,
    }
    for path in sorted(targets):
        source = path.read_text(encoding="latin1")
        had_final_newline = source.endswith("\n")
        lines = source.splitlines()
        changed = False
        for index in sorted(targets[path], reverse=True):
            if index >= len(lines):
                metrics["skipped"] += 1
                continue
            replacement = transformer(lines[index], maximum)
            if not replacement:
                metrics["skipped"] += 1
                continue
            lines[index : index + 1] = replacement
            metrics["wrapped"] += 1
            changed = True
        if changed:
            metrics["filesChanged"] += 1
            if apply:
                rendered = "\n".join(lines) + ("\n" if had_final_newline else "")
                path.write_text(rendered, encoding="latin1")
    return metrics


def main(argv=None):
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true")
    parser.add_argument("--maximum", type=int, default=140)
    parser.add_argument("--output", type=Path)
    parser.add_argument(
        "--mode",
        choices=(
            "arguments",
            "assignment",
            "assignment-comments",
            "boolean",
            "comments",
            "concatenation",
            "continuation-concatenation",
            "continuation-commas",
            "inline-methods",
            "semicolons",
            "strings",
            "subtraction",
        ),
        default="arguments",
    )
    args = parser.parse_args(argv)
    try:
        root = repo_root()
        reports = checkstyle_reports(root)
        if not reports:
            raise ValueError("no Checkstyle reports found; run Checkstyle first")
        transformers = {
            "arguments": wrap_argument_list,
            "assignment": split_assignment,
            "assignment-comments": split_commented_assignment,
            "boolean": lambda line, maximum: wrap_operators(
                line, {"&&", "||"}, maximum
            ),
            "comments": wrap_block_comment,
            "concatenation": lambda line, maximum: wrap_operators(
                line, {"+"}, maximum
            ),
            "continuation-concatenation": lambda line, maximum: (
                wrap_continuation_operators(line, {"+"}, maximum)
            ),
            "continuation-commas": split_comma_sequence,
            "inline-methods": split_inline_method_body,
            "semicolons": split_semicolon_statements,
            "strings": wrap_long_string_literal,
            "subtraction": lambda line, maximum: wrap_operators(
                line, {"-"}, maximum
            ),
        }
        targets = collect_targets(root, reports)
        if args.mode == "comments":
            targets = comment_targets(targets)
        result = apply_batch(
            targets,
            transformers[args.mode],
            maximum=args.maximum,
            apply=args.apply,
        )
        result.update(
            {"applied": args.apply, "maximum": args.maximum, "mode": args.mode}
        )
        payload = json.dumps(result, indent=2, sort_keys=True) + "\n"
        if args.output:
            args.output.write_text(payload, encoding="utf-8")
        print(payload, end="")
        return 0
    except (OSError, ValueError, ET.ParseError, subprocess.CalledProcessError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1


if __name__ == "__main__":
    sys.exit(main())
