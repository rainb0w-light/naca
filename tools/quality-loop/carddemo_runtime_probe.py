#!/usr/bin/env python3
"""Diagnostic-only runtime probe for pinned CardDemo candidates."""
import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PREFLIGHT = ROOT / "tools/quality-loop/carddemo_preflight.py"
REPORT = ROOT / "docs/quality-governance/carddemo-runtime.json"
CUSTOMER_REPORT = ROOT / "docs/quality-governance/carddemo-customer-runtime.json"
COMMIT = "59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e"


@dataclass(frozen=True)
class CandidateSpec:
    name: str
    candidate: str
    copybook: str
    data: str
    program_file: str
    include_file: str
    program_artifact: str
    include_artifact: str
    runtime_class: str
    logical_name: str
    descriptor: str
    record_length: int
    record_count: int
    output_repetitions: int
    start: str
    end: str
    report: Path


CANDIDATES = {
    "CBACT02C": CandidateSpec(
        "CBACT02C", "app/cbl/CBACT02C.cbl", "app/cpy/CVACT02Y.cpy",
        "app/data/ASCII/carddata.txt", "CBACT02C.cbl", "CVACT02Y",
        "Cbact02c.java", "Cvact02y.java", "Cbact02c", "CARDFILE",
        "ascii,fb,150", 150, 50, 1,
        "START OF EXECUTION OF PROGRAM CBACT02C",
        "END OF EXECUTION OF PROGRAM CBACT02C", REPORT,
    ),
    "CBCUS01C": CandidateSpec(
        "CBCUS01C", "app/cbl/CBCUS01C.cbl", "app/cpy/CVCUS01Y.cpy",
        "app/data/ASCII/custdata.txt", "CBCUS01C.cbl", "CVCUS01Y",
        "Cbcus01c.java", "Cvcus01y.java", "Cbcus01c", "CUSTFILE",
        "ascii,fb,500", 500, 50, 2,
        "START OF EXECUTION OF PROGRAM CBCUS01C",
        "END OF EXECUTION OF PROGRAM CBCUS01C", CUSTOMER_REPORT,
    ),
}
DEFAULT_CANDIDATE = "CBACT02C"
CANDIDATE = CANDIDATES[DEFAULT_CANDIDATE].candidate
COPYBOOK = CANDIDATES[DEFAULT_CANDIDATE].copybook
CARD_DATA = CANDIDATES[DEFAULT_CANDIDATE].data
START = CANDIDATES[DEFAULT_CANDIDATE].start
END = CANDIDATES[DEFAULT_CANDIDATE].end


def candidate_spec(name=DEFAULT_CANDIDATE):
    try:
        return CANDIDATES[name]
    except KeyError as error:
        raise ValueError(f"unknown candidate: {name}") from error


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


def runtime_report(stages, blocker, candidate=DEFAULT_CANDIDATE):
    spec = candidate_spec(candidate)
    runtime_file_status = any(
        item.get("name") == "runtime" and item.get("errorType") == "runtime-file-status"
        for item in stages)
    logical_file_alias = any(
        item.get("name") == "runtime"
        and item.get("errorType") == "runtime-logical-file-alias"
        for item in stages)
    if blocker is None:
        next_task = "Proceed to controlled runtime acceptance."
    elif logical_file_alias:
        next_task = (
            "Add a generic runtime mapping for the generated logical file name, "
            "then rerun this probe.")
    elif runtime_file_status and spec.name == DEFAULT_CANDIDATE:
        next_task = (
            "Restore omitted CARDFILE-STATUS child declarations, then rerun "
            "runtime acceptance.")
    else:
        next_task = "Resolve the first runtime blocker and rerun this probe."
    return {
        "schemaVersion": 1,
        "source": {
            "commit": COMMIT,
            "candidate": spec.candidate,
            "copybook": spec.copybook,
        },
        "runtimeInput": {
            "path": spec.data,
            "logicalName": spec.logical_name,
            "descriptor": spec.descriptor,
            "recordLength": spec.record_length,
            "recordCount": spec.record_count,
        },
        "stages": stages,
        "firstBlocker": blocker,
        "acceptance": {
            "currentSuccess": blocker is None,
            "structuredRejection": blocker is not None,
            "nextTask": next_task,
        },
    }


def classpath(*paths):
    return os.pathsep.join(str(path) for path in paths if path.exists())


def expected_records(path, spec=None):
    spec = spec or candidate_spec()
    data = path.read_bytes()
    stride = spec.record_length + 1
    if len(data) != spec.record_count * stride:
        raise ValueError(
            f"{Path(spec.data).name} must contain {spec.record_count} "
            f"newline-terminated {spec.record_length}-byte records")
    records = []
    for offset in range(0, len(data), stride):
        record = data[offset:offset + spec.record_length]
        if data[offset + spec.record_length:offset + stride] != b"\n":
            raise ValueError(f"{Path(spec.data).name} record separator is not LF")
        records.append(record.decode("ascii"))
    return records


def configure(work, input_dir, copy_dir, output_dir, inter_dir, spec=None):
    spec = spec or candidate_spec()
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
<Group Name="OnlineGroup"><Application Name="{spec.name}"><File Name="{spec.program_file}"/></Application></Group>
<Group Name="IncludeGroup"><Application Name="{spec.include_file}"><File Name="{spec.include_file}"/></Application></Group>
<GlobalPaths RuleFilePath=""/></NacaTrans>"""
    )
    return config


def run_probe(checkout, runner=subprocess.run, candidate=DEFAULT_CANDIDATE):
    spec = candidate_spec(candidate)
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
            return runtime_report(stages, "source-verification", spec.name)
        stages.append(stage("source-verification", "PASS",
            "carddemo_preflight verify-source", 0))

        with tempfile.TemporaryDirectory(prefix="carddemo-runtime-") as temp:
            work = Path(temp)
            input_dir, copy_dir = work / "cbl", work / "cpy"
            output_dir, inter_dir = work / "output", work / "inter"
            input_dir.mkdir()
            copy_dir.mkdir()
            shutil.copy2(checkout / spec.candidate, input_dir / spec.program_file)
            shutil.copy2(checkout / spec.copybook, copy_dir / spec.include_file)
            config = configure(work, input_dir, copy_dir, output_dir, inter_dir, spec)

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
                return runtime_report(stages, "transpile", spec.name)
            stages.append(stage("transpile", "PASS", "./gradlew ...", 0))

            generated = list(output_dir.rglob("*.java"))
            expected = {
                spec.program_artifact.lower(), spec.include_artifact.lower(),
            }
            missing = sorted(expected - {path.name.lower() for path in generated})
            if missing:
                stages.append(stage("generated-java", "FAIL", "inspect generated Java",
                    0, "missing-generated-artifact",
                    "missing generated artifacts: " + ", ".join(missing)))
                stages.extend([
                    stage("javac", "NOT_RUN", "javac <generated-java>"),
                    stage("runtime", "NOT_RUN", "java AcceptanceProgramRunner"),
                ])
                return runtime_report(stages, "generated-java", spec.name)
            stages.append(stage("generated-java", "PASS", "inspect generated Java", 0))
            declared_logical_names = sorted({
                name
                for path in generated
                for name in re.findall(
                    r'declare\.file\("([^"]+)"\)', path.read_text())
            })

            phase = "javac"
            javac = shutil.which("javac")
            if not javac:
                stages.append(stage("javac", "FAIL", "javac <generated-java>",
                    127, "tool-missing"))
                stages.append(stage("runtime", "NOT_RUN", "java AcceptanceProgramRunner"))
                return runtime_report(stages, "javac", spec.name)
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
                return runtime_report(stages, "javac", spec.name)
            stages.append(stage("javac", "PASS", "javac <generated-java>", 0))

            phase = "runtime"
            data = checkout / spec.data
            expected = expected_records(data, spec)
            runtime_result = runner(
                ["./gradlew", "--quiet", ":naca-rt-tests:cardDemoRuntime",
                 f"-PruntimeClasses={classes}",
                 f"-PruntimeClass={spec.runtime_class}",
                 f"-PruntimeLogicalName={spec.logical_name}",
                 f"-PruntimeInput={data}",
                 f"-PruntimeDescriptor={spec.descriptor}"],
                cwd=ROOT, text=True, capture_output=True)
            lines = runtime_result.stdout.splitlines()
            business_lines = [line for line in lines
                              if line == spec.start or line == spec.end
                              or len(line) == spec.record_length]
            repeated_records = [record for record in expected
                                for _ in range(spec.output_repetitions)]
            expected_output = [spec.start, *repeated_records, spec.end]
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
                                      ("ERROR OPENING CARDFILE",
                                       "ERROR OPENING CUSTFILE",
                                       "ERROR READING CUSTOMER FILE",
                                       "ERROR CLOSING CUSTOMER FILE",
                                       "FILE STATUS IS:", "ABENDING PROGRAM"))]
                if diagnostics:
                    detail += "; " + " | ".join(diagnostics)
                runtime_output = runtime_result.stdout + runtime_result.stderr
                input_not_found = "InputFileNotFoundException" in runtime_output
                if input_not_found:
                    unmapped = [name for name in declared_logical_names
                                if name != spec.logical_name]
                    if unmapped:
                        detail += ("; InputFileNotFoundException: generated logical name "
                                   + unmapped[0] + " is not mapped by runner argument "
                                   + spec.logical_name)
                    else:
                        detail += "; InputFileNotFoundException"
                elif runtime_result.returncode:
                    output = runtime_result.stdout + runtime_result.stderr
                    relevant = [line for line in output.splitlines()
                                if any(token in line for token in
                                       ("Exception", "Error", "ERROR", "Caused by"))]
                    if not diagnostics:
                        detail += "; " + " | ".join((relevant or output.splitlines()[-8:])[:3])
                if input_not_found and any(
                        name != spec.logical_name for name in declared_logical_names):
                    error_type = "runtime-logical-file-alias"
                elif input_not_found:
                    error_type = "runtime-input-file-not-found"
                else:
                    error_type = ("runtime-file-status" if diagnostics
                                  else "runtime-output-mismatch")
                stages.append(stage("runtime", "FAIL",
                    "java AcceptanceProgramRunner", runtime_result.returncode,
                    error_type,
                    detail, work))
                return runtime_report(stages, "runtime", spec.name)
            stages.append(stage("runtime", "PASS",
                "java AcceptanceProgramRunner", 0))
            return runtime_report(stages, None, spec.name)
    except (OSError, subprocess.SubprocessError, ValueError) as error:
        name = phase
        stages.append(stage(name, "FAIL", name, 1, "tool-error", str(error)))
        order = ["source-verification", "transpile", "generated-java", "javac", "runtime"]
        position = order.index(name)
        for downstream in order[position + 1:]:
            stages.append(stage(downstream, "NOT_RUN", downstream))
        return runtime_report(stages, name, spec.name)


def validate(data, candidate=DEFAULT_CANDIDATE):
    spec = candidate_spec(candidate)
    if data.get("schemaVersion") != 1:
        raise ValueError("schemaVersion invalid")
    source = data.get("source", {})
    if (source.get("commit") != COMMIT
            or source.get("candidate") != spec.candidate
            or source.get("copybook") != spec.copybook):
        raise ValueError("source identity invalid")
    runtime_input = data.get("runtimeInput", {})
    if (runtime_input.get("path") != spec.data
            or runtime_input.get("logicalName") != spec.logical_name
            or runtime_input.get("descriptor") != spec.descriptor
            or runtime_input.get("recordLength") != spec.record_length
            or runtime_input.get("recordCount") != spec.record_count):
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
    probe.add_argument("--candidate", default=DEFAULT_CANDIDATE)
    probe.add_argument("--output", type=Path)
    validation = sub.add_parser("validate-report")
    validation.add_argument("--candidate", default=DEFAULT_CANDIDATE)
    validation.add_argument("--report", type=Path)
    args = parser.parse_args(argv)
    try:
        if args.command == "probe-runtime":
            spec = candidate_spec(args.candidate)
            result = run_probe(args.checkout, candidate=spec.name)
            target = args.output or spec.report
            target.write_text(json.dumps(result, indent=2) + "\n")
            print(json.dumps({"status": "PASS", "report": str(target)}))
            return 0
        spec = candidate_spec(args.candidate)
        target = args.report or spec.report
        validate(json.loads(target.read_text()), spec.name)
        print(json.dumps({"status": "PASS", "command": "validate-report"}))
        return 0
    except (OSError, ValueError, KeyError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)}))
        return 1


if __name__ == "__main__":
    sys.exit(main())
