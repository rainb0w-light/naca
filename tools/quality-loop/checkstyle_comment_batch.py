#!/usr/bin/env python3
"""Wrap comment-only Checkstyle LineLength findings deterministically.

The batch deliberately ignores Java code and trailing comments.  It consumes the
XML reports produced by Checkstyle, so every edit is tied to an observed rule
finding instead of scanning and reformatting unrelated source lines.
"""

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
JAVADOC_CONTINUATION_SOURCE = (
    "com.puppycrawl.tools.checkstyle.checks.javadoc."
    "JavadocTagContinuationIndentationCheck"
)
JAVADOC_PARAGRAPH_SOURCE = (
    "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocParagraphCheck"
)
TASK_COMMENT_SOURCE = (
    "com.puppycrawl.tools.checkstyle.checks.regexp.RegexpSinglelineJavaCheck"
)
MAXIMUM_COMMENT_INDENT = 40


def repo_root():
    """Return the current Git worktree root."""
    output = subprocess.check_output(
        ["git", "rev-parse", "--show-toplevel"], text=True
    )
    return Path(output.strip()).resolve()


def comment_parts(line):
    """Return indentation, prefix, and body for a supported comment-only line."""
    stripped = line.lstrip()
    indentation = line[: len(line) - len(stripped)].expandtabs(4)
    if len(indentation) > MAXIMUM_COMMENT_INDENT:
        indentation = indentation[:MAXIMUM_COMMENT_INDENT]
    if stripped.startswith("//"):
        return indentation, "// ", stripped[2:].strip()
    if stripped.startswith("*") and not stripped.startswith("*/"):
        after_marker = stripped[1:]
        continuation = after_marker.startswith("     ") or after_marker.startswith("\t")
        return indentation, "*     " if continuation else "* ", after_marker.strip()
    return None


def wrap_comment_line(line, maximum=140):
    """Wrap one supported comment line, returning an empty list when unsafe."""
    parts = comment_parts(line)
    if parts is None:
        return []
    indentation, marker, body = parts
    prefix = indentation + marker
    available = maximum - len(prefix)
    if available < 20 or not body:
        return []
    wrapped = textwrap.wrap(
        " ".join(body.split()),
        width=available,
        break_long_words=False,
        break_on_hyphens=False,
    )
    result = [prefix + part for part in wrapped]
    if not result or any(len(item.expandtabs(8)) > maximum for item in result):
        return []
    return result


def trailing_comment_index(line):
    """Locate a Java // comment outside string and character literals."""
    quote = None
    escaped = False
    for index, current in enumerate(line[:-1]):
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
        elif current == "/" and line[index + 1] == "/":
            return index
    return None


def split_trailing_comment_line(line, maximum=140):
    """Move a trailing comment above its code when both results meet the limit."""
    index = trailing_comment_index(line)
    if index is None:
        return []
    code = line[:index].rstrip()
    body = line[index + 2 :].strip()
    if not code.strip() or not body:
        return []
    indentation = code[: len(code) - len(code.lstrip())]
    comments = wrap_comment_line(indentation + "// " + body, maximum)
    if not comments or len(code.expandtabs(8)) > maximum:
        return []
    return comments + [code]


def checkstyle_reports(root):
    """Find all generated Checkstyle XML reports in stable order."""
    return sorted(root.glob("*/build/reports/checkstyle/*.xml"))


def collect_targets(root, reports):
    """Collect observed comment-only LineLength findings by source path."""
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


def collect_rule_targets(root, reports, rule_source, parse_errors=None):
    """Collect source locations for one exact Checkstyle rule."""
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
                if error.attrib.get("source") != rule_source:
                    continue
                message = error.attrib.get("message", "").lower()
                is_parse_error = "解析错误" in message or "parse error" in message
                if parse_errors is None or parse_errors == is_parse_error:
                    targets[path].add(int(error.attrib["line"]) - 1)
    return targets


def collect_rule_findings(root, reports, rule_source):
    """Collect line indexes and messages for one exact Checkstyle rule."""
    findings = defaultdict(dict)
    for report in reports:
        for file_element in ET.parse(report).getroot().findall("file"):
            path = Path(file_element.attrib["name"]).resolve()
            try:
                path.relative_to(root.resolve())
            except ValueError:
                continue
            for error in file_element.findall("error"):
                if error.attrib.get("source") == rule_source:
                    findings[path][int(error.attrib["line"]) - 1] = error.attrib.get(
                        "message", ""
                    )
    return findings


def apply_comment_policy_batch(findings, policy, apply=False):
    """Apply one message-aware comment policy in reverse source order."""
    metrics = {
        "observed": sum(len(lines) for lines in findings.values()),
        "fixed": 0,
        "skipped": 0,
        "filesChanged": 0,
    }
    for path in sorted(findings):
        source = path.read_text(encoding="latin1")
        had_final_newline = source.endswith("\n")
        lines = source.splitlines()
        changed = False
        for index, message in sorted(findings[path].items(), reverse=True):
            if index >= len(lines):
                metrics["skipped"] += 1
                continue
            if policy is fix_javadoc_paragraph and "空行后" in message:
                if lines[index].strip() != "*":
                    metrics["skipped"] += 1
                    continue
                del lines[index]
                metrics["fixed"] += 1
                changed = True
                continue
            if policy is fix_javadoc_paragraph and "多余" in message and "<p>" in lines[index]:
                lines[index] = lines[index].replace("<p>", "", 1).replace("</p>", "", 1)
                lines[index] = re.sub(r"(\*)\s+", r"\1 ", lines[index]).rstrip()
                if index > 0 and lines[index - 1].strip() == "*":
                    del lines[index - 1]
                metrics["fixed"] += 1
                changed = True
                continue
            replacement = policy(lines[index], message)
            if not replacement:
                metrics["skipped"] += 1
                continue
            lines[index : index + 1] = replacement
            metrics["fixed"] += 1
            changed = True
        if changed:
            metrics["filesChanged"] += 1
            if apply:
                rendered = "\n".join(lines) + ("\n" if had_final_newline else "")
                path.write_text(rendered, encoding="latin1")
    return metrics


def fix_javadoc_paragraph(line, message):
    """Repair the three JavadocParagraph finding shapes."""
    indentation = line[: len(line) - len(line.lstrip())]
    stripped = line.strip()
    if "多余" in message and "<p>" in line:
        return [line.replace("<p>", "", 1).replace("</p>", "", 1).rstrip()]
    if "标签前应有空行" in message and "<p>" in line:
        return [indentation + "*", line]
    if "标签前应有空行" in message and "<P>" in line:
        return [line.replace("<P>", "", 1).rstrip()]
    if "解析" in message and "</p>" in line and "<p>" not in line:
        marker = line.find("*") + 1
        repaired = line[:marker] + " <p>" + line[marker:].lstrip()
        return [indentation + "*", repaired]
    return []


def qualify_task_comment(line, _message):
    """Attach the governance owner to an unqualified TODO/FIXME comment."""
    match = re.match(r"^(\s*//\s*)(TODO|FIXME)\s*(.*)$", line)
    if not match:
        return []
    prefix, marker, body = match.groups()
    separator = ": " if body else ""
    return [f"{prefix}{marker}(quality-governance){separator}{body}"]


def fix_javadoc_continuation(line):
    """Apply Checkstyle's four-space continuation offset to one Javadoc line."""
    stripped = line.lstrip()
    if not stripped.startswith("*") or stripped.startswith("*/"):
        return None
    indentation = line[: len(line) - len(stripped)]
    body = stripped[1:].strip()
    if not body:
        return None
    return indentation + "*     " + body


def apply_javadoc_batch(targets, apply=False):
    """Plan or apply observed Javadoc continuation-indentation findings."""
    metrics = {
        "observed": sum(len(lines) for lines in targets.values()),
        "fixed": 0,
        "skipped": 0,
        "filesChanged": 0,
    }
    for path in sorted(targets):
        source = path.read_text(encoding="latin1")
        had_final_newline = source.endswith("\n")
        lines = source.splitlines()
        changed = False
        for index in sorted(targets[path]):
            if index >= len(lines):
                metrics["skipped"] += 1
                continue
            replacement = fix_javadoc_continuation(lines[index])
            if replacement is None:
                metrics["skipped"] += 1
                continue
            metrics["fixed"] += 1
            if replacement != lines[index]:
                lines[index] = replacement
                changed = True
        if changed:
            metrics["filesChanged"] += 1
            if apply:
                rendered = "\n".join(lines) + ("\n" if had_final_newline else "")
                path.write_text(rendered, encoding="latin1")
    return metrics


def repair_javadoc_document(source):
    """Repair repeated malformed Javadoc fragments without changing line count."""
    replacements = {
        "developers found worthwhile to log.</i>":
            "developers found worthwhile to log.</li>",
        "specified <b>logFlow</i>": "specified <b>logFlow</b>",
        "is the method checking this second condition.</i>":
            "is the method checking this second condition.</li>",
        "events of any flow.</i>.</li>": "events of any flow.</li>",
        "{@link #grantWriteAccessToUsersOrGroups and ":
            "{@link #grantWriteAccessToUsersOrGroups} and ",
        "{@link #denyWriteAccessToUsersOrGroups and ":
            "{@link #denyWriteAccessToUsersOrGroups} and ",
    }
    for before, after in replacements.items():
        source = source.replace(before, after)
    return source


def repair_javadoc_parse_line(line):
    """Repair one report-located malformed Javadoc line when the pattern is known."""
    stripped = line.lstrip()
    if not stripped.startswith("*") or stripped.startswith("*/"):
        return None
    indentation = line[: len(line) - len(stripped)]
    body = stripped[1:].strip()
    body = re.sub(r"^@\s+see\b", "@see", body)
    raw_url = re.match(r"^@see\s+(https?://\S+)(.*)$", body)
    if raw_url:
        url, description = raw_url.groups()
        body = f'@see <a href="{url}">reference</a>{description}'
    if body.startswith("@author"):
        body = re.sub(r"<([^<>@]+@[^<>]+)>", r"&lt;\1&gt;", body)
    body = re.sub(
        r"^@exception\s+\{@link\s+([^}]+)}",
        r"@throws \1",
        body,
    )
    body = re.sub(r"^@see\s+\{@link\s+([^}]+)}", r"@see \1", body)
    if "Used by ST4 templates: <entity.children:statement()>" in body:
        body = body.replace(
            "Used by ST4 templates: <entity.children:statement()>",
            "Used by ST4 templates: {@code <entity.children:statement()>}",
        )
    if "within </," in body:
        body = body.replace("within </,", "within {@code </},")
    if body == "</p>":
        return indentation + "*"
    if body.startswith("@") or body.startswith("<"):
        return indentation + "* " + body
    return indentation + "*     " + body


def apply_javadoc_parse_batch(targets, apply=False):
    """Repair observed Javadoc parser findings plus their repeated root patterns."""
    metrics = {
        "observed": sum(len(lines) for lines in targets.values()),
        "fixed": 0,
        "skipped": 0,
        "filesChanged": 0,
    }
    for path in sorted(targets):
        original = path.read_text(encoding="latin1")
        source = repair_javadoc_document(original)
        had_final_newline = source.endswith("\n")
        lines = source.splitlines()
        for index in sorted(targets[path]):
            if index >= len(lines):
                metrics["skipped"] += 1
                continue
            replacement = repair_javadoc_parse_line(lines[index])
            if replacement is None:
                metrics["skipped"] += 1
                continue
            lines[index] = replacement
            metrics["fixed"] += 1
        rendered = "\n".join(lines) + ("\n" if had_final_newline else "")
        if rendered != original:
            metrics["filesChanged"] += 1
            if apply:
                path.write_text(rendered, encoding="latin1")
    return metrics


def apply_batch(targets, maximum=140, apply=False):
    """Plan or apply wrapping and return deterministic batch metrics."""
    metrics = {
        "observed": sum(len(lines) for lines in targets.values()),
        "commentOnly": 0,
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
            if comment_parts(lines[index]) is None:
                continue
            metrics["commentOnly"] += 1
            replacement = wrap_comment_line(lines[index], maximum)
            if not replacement:
                metrics["skipped"] += 1
                continue
            metrics["wrapped"] += 1
            if replacement != [lines[index]]:
                lines[index : index + 1] = replacement
                changed = True
        if changed:
            metrics["filesChanged"] += 1
            if apply:
                rendered = "\n".join(lines) + ("\n" if had_final_newline else "")
                path.write_text(rendered, encoding="latin1")
    return metrics


def apply_trailing_comment_batch(targets, maximum=140, apply=False):
    """Plan or apply report-located trailing-comment splits."""
    metrics = {
        "observed": sum(len(lines) for lines in targets.values()),
        "eligible": 0,
        "split": 0,
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
            if trailing_comment_index(lines[index]) is None:
                continue
            metrics["eligible"] += 1
            replacement = split_trailing_comment_line(lines[index], maximum)
            if not replacement:
                metrics["skipped"] += 1
                continue
            lines[index : index + 1] = replacement
            metrics["split"] += 1
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
    parser.add_argument("--fix-javadoc-continuations", action="store_true")
    parser.add_argument("--repair-javadoc-parse", action="store_true")
    parser.add_argument("--split-trailing-comments", action="store_true")
    parser.add_argument("--fix-javadoc-paragraphs", action="store_true")
    parser.add_argument("--qualify-task-comments", action="store_true")
    parser.add_argument("--maximum", type=int, default=140)
    parser.add_argument("--output", type=Path)
    args = parser.parse_args(argv)
    try:
        root = repo_root()
        reports = checkstyle_reports(root)
        if not reports:
            raise ValueError("no Checkstyle XML reports found; run Checkstyle first")
        if args.qualify_task_comments:
            result = apply_comment_policy_batch(
                collect_rule_findings(root, reports, TASK_COMMENT_SOURCE),
                qualify_task_comment,
                apply=args.apply,
            )
            result["rule"] = "RegexpSinglelineJavaTaskComment"
        elif args.fix_javadoc_paragraphs:
            result = apply_comment_policy_batch(
                collect_rule_findings(root, reports, JAVADOC_PARAGRAPH_SOURCE),
                fix_javadoc_paragraph,
                apply=args.apply,
            )
            result["rule"] = "JavadocParagraph"
        elif args.split_trailing_comments:
            result = apply_trailing_comment_batch(
                collect_targets(root, reports), maximum=args.maximum, apply=args.apply
            )
            result["rule"] = "LineLengthTrailingComment"
        elif args.repair_javadoc_parse:
            result = apply_javadoc_parse_batch(
                collect_rule_targets(
                    root,
                    reports,
                    JAVADOC_CONTINUATION_SOURCE,
                    parse_errors=True,
                ),
                apply=args.apply,
            )
            result["rule"] = "JavadocParse"
        elif args.fix_javadoc_continuations:
            result = apply_javadoc_batch(
                collect_rule_targets(
                    root,
                    reports,
                    JAVADOC_CONTINUATION_SOURCE,
                    parse_errors=False,
                ),
                apply=args.apply,
            )
            result["rule"] = "JavadocTagContinuationIndentation"
        else:
            result = apply_batch(
                collect_targets(root, reports), maximum=args.maximum, apply=args.apply
            )
            result["rule"] = "LineLengthCommentOnly"
        result.update({"applied": args.apply, "maximum": args.maximum})
        payload = json.dumps(result, indent=2, sort_keys=True) + "\n"
        if args.output:
            args.output.write_text(payload, encoding="utf-8")
        print(payload, end="")
        return 0 if result["skipped"] == 0 else 2
    except (OSError, ValueError, ET.ParseError, subprocess.CalledProcessError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1


if __name__ == "__main__":
    sys.exit(main())
