#!/usr/bin/env python3
"""Diagnostic-only runtime probe for the pinned CardDemo CBACT02C."""
import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PREFLIGHT = ROOT / "tools/quality-loop/carddemo_preflight.py"
REPORT = ROOT / "docs/quality-governance/carddemo-runtime.json"
COMMIT = "59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e"
CANDIDATE = "app/cbl/CBACT02C.cbl"
COPYBOOK = "app/cpy/CVACT02Y.cpy"
CARD_DATA = "app/data/ASCII/carddata.txt"
START = "START OF EXECUTION OF PROGRAM CBACT02C"
END = "END OF EXECUTION OF PROGRAM CBACT02C"


def clean(text, temp_root=None):
    value = " ".join((text or "").split())
    if temp_root:
        value = value.replace(str(temp_root), "<temp>")
    value = re.sub(r"/(?:Users|Volumes|private|tmp|var)/[^\s;]+", "<external>", value)
    return value[:240]


def stage(name, status, command, code=None, kind=None, detail=None, temp_root=None):
    result = {"name": name, "status": status, "command": command}
    if code is not None:
        result["exitCode"] = code
    if kind:
        result["errorType"] = kind
    if detail:
        result["diagnostic"] = clean(detail, temp_root)
    return result


def runtime_report(stages, blocker):
    runtime_file_status = any(
        item.get("name") == "runtime" and item.get("errorType") == "runtime-file-status"
        for item in stages)
    return {
        "schemaVersion": 1,
        "source": {"commit": COMMIT, "candidate": CANDIDATE, "copybook": COPYBOOK},
        "runtimeInput": {
            "path": CARD_DATA,
            "logicalName": "CARDFILE",
            "descriptor": "ascii,fb,150",
            "recordLength": 150,
            "recordCount": 50,
        },
        "stages": stages,
        "firstBlocker": blocker,
        "acceptance": {
            "currentSuccess": blocker is None,
            "structuredRejection": blocker is not None,
            "nextTask": "Proceed to controlled runtime acceptance." if blocker is None
                else ("Restore omitted CARDFILE-STATUS child declarations, then rerun runtime acceptance."
                      if runtime_file_status else "Resolve the first runtime blocker and rerun this probe."),
        },
    }


def classpath(*paths):
    return os.pathsep.join(str(path) for path in paths if path.exists())


def expected_records(path):
    data = path.read_bytes()
    if len(data) != 50 * 151:
        raise ValueError("carddata.txt must contain 50 newline-terminated 150-byte records")
    records = []
    for offset in range(0, len(data), 151):
        record = data[offset:offset + 150]
        if data[offset + 150:offset + 151] != b"\n":
            raise ValueError("carddata.txt record separator is not LF")
        records.append(record.decode("ascii"))
    return records


def configure(work, input_dir, copy_dir, output_dir, inter_dir):
    config = work / "probe.xml"
    config.write_text(
        f"""<NacaTrans Log4jConf=""><Engines>
<Transcoder Name="CobolTranscoder" Class="utils.CobolTranscoder.CobolTranscoderEngine"
 ReferenceGroupName="" ResourceGroupName="" IncludeGroupName="IncludeGroup"/>
<Transcoder Name="IncludeTranscoder" Class="utils.CobolTranscoder.CobolIncludeTranscoderEngine"
 ReferenceGroupName="" ResourceGroupName="" IncludeGroupName=""/>
</Engines><Groups>
<Group Name="OnlineGroup" InputPath="{input_dir}/" OutputPath="{output_dir}/"
 InterPath="{inter_dir}/" Type="Batch" Engine="CobolTranscoder"/>
<Group Name="IncludeGroup" InputPath="{copy_dir}/" OutputPath="{output_dir}/include/"
 InterPath="{inter_dir}/" Type="Included" Engine="IncludeTranscoder"/>
</Groups>
<Group Name="OnlineGroup"><Application Name="CBACT02C"><File Name="CBACT02C.cbl"/></Application></Group>
<Group Name="IncludeGroup"><Application Name="CVACT02Y"><File Name="CVACT02Y"/></Application></Group>
<GlobalPaths RuleFilePath=""/></NacaTrans>"""
    )
    return config


def run_probe(checkout, runner=subprocess.run):
    stages = []
    phase = "source-verification"
    try:
        preflight = runner(
            [sys.executable, str(PREFLIGHT), "verify-source", str(checkout)],
            cwd=ROOT, text=True, capture_output=True)
        if preflight.returncode:
            stages.append(stage("source-verification", "FAIL",
                "carddemo_preflight verify-source", preflight.returncode,
                "source-verification", preflight.stdout or preflight.stderr))
            stages.extend([
                stage("transpile", "NOT_RUN", "./gradlew ..."),
                stage("generated-java", "NOT_RUN", "inspect generated Java"),
                stage("javac", "NOT_RUN", "javac <generated-java>"),
                stage("runtime", "NOT_RUN", "java AcceptanceProgramRunner"),
            ])
            return runtime_report(stages, "source-verification")
        stages.append(stage("source-verification", "PASS",
            "carddemo_preflight verify-source", 0))

        with tempfile.TemporaryDirectory(prefix="carddemo-runtime-") as temp:
            work = Path(temp)
            input_dir, copy_dir = work / "cbl", work / "cpy"
            output_dir, inter_dir = work / "output", work / "inter"
            input_dir.mkdir()
            copy_dir.mkdir()
            shutil.copy2(checkout / CANDIDATE, input_dir / "CBACT02C.cbl")
            shutil.copy2(checkout / COPYBOOK, copy_dir / "CVACT02Y")
            config = configure(work, input_dir, copy_dir, output_dir, inter_dir)

            phase = "transpile"
            gradle = runner(
                ["./gradlew", ":naca-jlib:classes", ":naca-rt:classes",
                 ":naca-trans:transpile", f"-PconfigFile={config}"],
                cwd=ROOT, text=True, capture_output=True)
            if gradle.returncode:
                stages.append(stage("transpile", "FAIL", "./gradlew ...",
                    gradle.returncode, "tool-or-transpiler-error",
                    gradle.stderr or gradle.stdout, work))
                stages.extend([
                    stage("generated-java", "NOT_RUN", "inspect generated Java"),
                    stage("javac", "NOT_RUN", "javac <generated-java>"),
                    stage("runtime", "NOT_RUN", "java AcceptanceProgramRunner"),
                ])
                return runtime_report(stages, "transpile")
            stages.append(stage("transpile", "PASS", "./gradlew ...", 0))

            generated = list(output_dir.rglob("*.java"))
            expected = {"cbact02c.java", "cvact02y.java"}
            missing = sorted(expected - {path.name.lower() for path in generated})
            if missing:
                stages.append(stage("generated-java", "FAIL", "inspect generated Java",
                    0, "missing-generated-artifact",
                    "missing generated artifacts: " + ", ".join(missing)))
                stages.extend([
                    stage("javac", "NOT_RUN", "javac <generated-java>"),
                    stage("runtime", "NOT_RUN", "java AcceptanceProgramRunner"),
                ])
                return runtime_report(stages, "generated-java")
            stages.append(stage("generated-java", "PASS", "inspect generated Java", 0))

            phase = "javac"
            javac = shutil.which("javac")
            if not javac:
                stages.append(stage("javac", "FAIL", "javac <generated-java>",
                    127, "tool-missing"))
                stages.append(stage("runtime", "NOT_RUN", "java AcceptanceProgramRunner"))
                return runtime_report(stages, "javac")
            runtime_cp = [
                ROOT / "naca-rt/build/classes/java/main",
                ROOT / "naca-rt/build/resources/main",
                ROOT / "naca-jlib/build/classes/java/main",
                ROOT / "naca-jlib/build/resources/main",
            ]
            classes = work / "classes"
            javac_result = runner(
                [javac, "-J-Duser.language=en", "-J-Duser.country=US",
                 "-cp", classpath(*runtime_cp), "-d", str(classes),
                 *map(str, generated)], cwd=ROOT, text=True, capture_output=True)
            if javac_result.returncode:
                stages.append(stage("javac", "FAIL", "javac <generated-java>",
                    javac_result.returncode, "javac-error",
                    javac_result.stderr, work))
                stages.append(stage("runtime", "NOT_RUN", "java AcceptanceProgramRunner"))
                return runtime_report(stages, "javac")
            stages.append(stage("javac", "PASS", "javac <generated-java>", 0))

            phase = "runtime"
            data = checkout / CARD_DATA
            expected = expected_records(data)
            runtime_result = runner(
                ["./gradlew", "--quiet", ":naca-rt-tests:cardDemoRuntime",
                 f"-PruntimeClasses={classes}", "-PruntimeClass=Cbact02c",
                 "-PruntimeLogicalName=CARDFILE", f"-PruntimeInput={data}",
                 "-PruntimeDescriptor=ascii,fb,150"],
                cwd=ROOT, text=True, capture_output=True)
            lines = runtime_result.stdout.splitlines()
            business_lines = [line for line in lines
                              if line == START or line == END or len(line) == 150]
            expected_output = [START, *expected, END]
            ok = runtime_result.returncode == 0 and business_lines == expected_output
            if not ok:
                detail = "runtime output mismatch: business-lines=%d expected=%d" % (
                    len(business_lines), len(expected_output))
                if len(business_lines) == len(expected_output):
                    mismatch = next((index for index, pair in enumerate(
                        zip(business_lines, expected_output)) if pair[0] != pair[1]), None)
                    if mismatch is not None:
                        detail += "; first-mismatch=" + str(mismatch)
                diagnostics = [line.strip() for line in
                               (runtime_result.stdout + runtime_result.stderr).splitlines()
                               if any(marker in line for marker in
                                      ("ERROR OPENING CARDFILE", "FILE STATUS IS:",
                                       "ABENDING PROGRAM"))]
                if diagnostics:
                    detail += "; " + " | ".join(diagnostics)
                if runtime_result.returncode:
                    output = runtime_result.stdout + runtime_result.stderr
                    relevant = [line for line in output.splitlines()
                                if any(token in line for token in
                                       ("Exception", "Error", "ERROR", "Caused by"))]
                    if not diagnostics:
                        detail += "; " + " ".join((relevant or output.splitlines()[-8:]))
                error_type = "runtime-file-status" if diagnostics else "runtime-output-mismatch"
                stages.append(stage("runtime", "FAIL",
                    "java AcceptanceProgramRunner", runtime_result.returncode,
                    error_type,
                    detail, work))
                return runtime_report(stages, "runtime")
            stages.append(stage("runtime", "PASS",
                "java AcceptanceProgramRunner", 0))
            return runtime_report(stages, None)
    except (OSError, subprocess.SubprocessError, ValueError) as error:
        name = phase
        stages.append(stage(name, "FAIL", name, 1, "tool-error", str(error)))
        order = ["source-verification", "transpile", "generated-java", "javac", "runtime"]
        position = order.index(name)
        for downstream in order[position + 1:]:
            stages.append(stage(downstream, "NOT_RUN", downstream))
        return runtime_report(stages, name)


def validate(data):
    if data.get("schemaVersion") != 1:
        raise ValueError("schemaVersion invalid")
    source = data.get("source", {})
    if source.get("commit") != COMMIT or source.get("candidate") != CANDIDATE:
        raise ValueError("source identity invalid")
    runtime_input = data.get("runtimeInput", {})
    if (runtime_input.get("logicalName") != "CARDFILE"
            or runtime_input.get("descriptor") != "ascii,fb,150"
            or runtime_input.get("recordLength") != 150
            or runtime_input.get("recordCount") != 50):
        raise ValueError("runtime input invalid")
    stages = data.get("stages")
    names = ["source-verification", "transpile", "generated-java", "javac", "runtime"]
    if not isinstance(stages, list) or [item.get("name") for item in stages] != names:
        raise ValueError("stage order invalid")
    for item in stages:
        if item.get("status") not in {"PASS", "FAIL", "NOT_RUN"}:
            raise ValueError("stage status invalid")
        if not isinstance(item.get("command"), str):
            raise ValueError("stage command invalid")
        if item["status"] in {"PASS", "FAIL"} and not isinstance(item.get("exitCode"), int):
            raise ValueError("stage exitCode invalid")
        if item["status"] == "FAIL" and not item.get("errorType"):
            raise ValueError("failure errorType missing")
    failures = [item["name"] for item in stages if item["status"] == "FAIL"]
    blocker = data.get("firstBlocker")
    if blocker != (failures[0] if failures else None):
        raise ValueError("first blocker invalid")
    blocked = False
    for item in stages:
        if blocked and item["status"] != "NOT_RUN":
            raise ValueError("failure did not short-circuit")
        if item["status"] == "FAIL":
            blocked = True
    acceptance = data.get("acceptance", {})
    if acceptance.get("currentSuccess") != (blocker is None):
        raise ValueError("acceptance flag invalid")


def main(argv=None):
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)
    probe = sub.add_parser("probe-runtime")
    probe.add_argument("checkout", type=Path)
    probe.add_argument("--output", type=Path)
    sub.add_parser("validate-report")
    args = parser.parse_args(argv)
    try:
        if args.command == "probe-runtime":
            result = run_probe(args.checkout)
            target = args.output or REPORT
            target.write_text(json.dumps(result, indent=2) + "\n")
            print(json.dumps({"status": "PASS", "report": str(target)}))
            return 0
        validate(json.loads(REPORT.read_text()))
        print(json.dumps({"status": "PASS", "command": "validate-report"}))
        return 0
    except (OSError, ValueError, KeyError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1


if __name__ == "__main__":
    sys.exit(main())
