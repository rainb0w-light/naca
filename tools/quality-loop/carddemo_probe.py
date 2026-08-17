#!/usr/bin/env python3
"""Run a bounded, diagnostic-only CardDemo feasibility probe."""
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
REPORT = ROOT / "docs/quality-governance/carddemo-feasibility.json"
COMMIT = "59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e"
CANDIDATE = "app/cbl/CBACT02C.cbl"
COPYBOOK = "app/cpy/CVACT02Y.cpy"


def classify(text):
    lower = text.lower()
    if "not found" in lower or "no such file" in lower:
        return "tool-or-input-missing"
    if "config" in lower or "xml" in lower:
        return "configuration"
    if "unsupported" in lower or "unmanaged" in lower or "parse" in lower or "unexpecting token" in lower or "end-if" in lower or "zero" in lower:
        return "unsupported-or-parse"
    return "transpiler-error"


def naca_diagnostic(text):
    for line in text.splitlines():
        clean = " ".join(line.split())
        if "[Error]" in clean or "ERROR LogFile" in clean or "COBOL parsing failed" in clean or "Missing include file" in clean:
            clean = re.sub(r"^\d\d:\d\d:\d\d\.\d+ \[main\] ", "", clean)
            return clean[:240]
    return None


def normalize_diagnostic(text, temp_root=None):
    clean = " ".join((text or "").split())
    if temp_root is not None:
        clean = clean.replace(str(temp_root), "<temp>")
    return clean[:240]


def stage(name, status, command, code=None, kind=None, detail=None, temp_root=None):
    if status == "PASS" and code is None:
        code = 0
    result = {"name": name, "status": status, "command": command}
    if code is not None:
        result["exitCode"] = code
    if kind:
        result["errorType"] = kind
    if detail:
        result["diagnostic"] = normalize_diagnostic(detail, temp_root)
    return result


def run_probe(checkout, runner=subprocess.run):
    stages = []
    phase = "preflight"
    try:
        preflight = runner([sys.executable, str(PREFLIGHT), "verify-source", str(checkout)], cwd=ROOT, text=True, capture_output=True)
        if preflight.returncode:
            stages.append(stage("source-verification", "FAIL", "carddemo_preflight verify-source", preflight.returncode, "source-verification", preflight.stdout or preflight.stderr))
            stages.extend([
                stage("parse-transpile", "NOT_RUN", "probe transpile"),
                stage("generated-java", "NOT_RUN", "inspect temporary output"),
                stage("javac", "NOT_RUN", "javac <generated-java>"),
            ])
            return report(stages, "source-verification")
        stages.append(stage("source-verification", "PASS", "carddemo_preflight verify-source", 0))
        phase = "transpile"
        with tempfile.TemporaryDirectory(prefix="carddemo-probe-") as temp:
            work = Path(temp)
            input_dir = work / "cbl"; copy_dir = work / "cpy"; output_dir = work / "output"; inter_dir = work / "inter"
            input_dir.mkdir(); copy_dir.mkdir()
            shutil.copy2(checkout / CANDIDATE, input_dir / "CBACT02C.cbl")
            shutil.copy2(checkout / COPYBOOK, copy_dir / "CVACT02Y")
            config = work / "probe.xml"
            config.write_text(f"""<NacaTrans Log4jConf=\"\"><Engines><Transcoder Name=\"CobolTranscoder\" Class=\"utils.CobolTranscoder.CobolTranscoderEngine\" ReferenceGroupName=\"\" ResourceGroupName=\"\" IncludeGroupName=\"IncludeGroup\"/><Transcoder Name=\"IncludeTranscoder\" Class=\"utils.CobolTranscoder.CobolIncludeTranscoderEngine\" ReferenceGroupName=\"\" ResourceGroupName=\"\" IncludeGroupName=\"\"/></Engines><Groups><Group Name=\"OnlineGroup\" InputPath=\"{input_dir}/\" OutputPath=\"{output_dir}/\" InterPath=\"{inter_dir}/\" Type=\"Batch\" Engine=\"CobolTranscoder\"/><Group Name=\"IncludeGroup\" InputPath=\"{copy_dir}/\" OutputPath=\"{output_dir}/include/\" InterPath=\"{inter_dir}/\" Type=\"Included\" Engine=\"IncludeTranscoder\"/></Groups><Group Name=\"OnlineGroup\"><Application Name=\"CBACT02C\"><File Name=\"CBACT02C.cbl\"/></Application></Group><GlobalPaths RuleFilePath=\"\"/></NacaTrans>""")
            command = "./gradlew :naca-jlib:classes :naca-rt:classes :naca-trans:transpile -PconfigFile=<temp-config>"
            result = runner(["./gradlew", ":naca-jlib:classes", ":naca-rt:classes", ":naca-trans:transpile", f"-PconfigFile={config}"], cwd=ROOT, text=True, capture_output=True)
            logs = result.stdout + result.stderr
            diagnostic = naca_diagnostic(logs)
            evidence = bool(diagnostic) or bool(list(output_dir.rglob("*.java"))) or ("CBACT02C" in logs and "processed" in logs.lower())
            if result.returncode or diagnostic or not evidence:
                error_type = classify(logs) if result.returncode or diagnostic else "no-transpile-evidence"
                stages.append(stage("parse-transpile", "FAIL", command, result.returncode, error_type, diagnostic or logs, temp))
                stages[-1]["errorType"] = error_type
                stages.append(stage("generated-java", "NOT_RUN", command))
                stages.append(stage("javac", "NOT_RUN", "javac <generated-java>"))
                return report(stages, "parse-transpile")
            stages.append(stage("parse-transpile", "PASS", command, 0))
            generated = list(output_dir.rglob("*.java"))
            if not generated:
                stages.append(stage("generated-java", "FAIL", "inspect temporary output", 0, "no-generated-java"))
                stages.append(stage("javac", "NOT_RUN", "javac <generated-java>"))
                return report(stages, "generated-java")
            stages.append(stage("generated-java", "PASS", "inspect temporary output", 0))
            phase = "javac"
            javac = shutil.which("javac")
            if not javac:
                stages.append(stage("javac", "FAIL", "javac <generated-java>", 127, "tool-missing"))
                return report(stages, "javac")
            runtime_paths = [
                ROOT / "naca-rt/build/classes/java/main",
                ROOT / "naca-rt/build/resources/main",
                ROOT / "naca-jlib/build/classes/java/main",
                ROOT / "naca-jlib/build/resources/main",
            ]
            classpath = os.pathsep.join(str(path) for path in runtime_paths if path.exists())
            javac_command = [
                javac, "-J-Duser.language=en", "-J-Duser.country=US",
                "-cp", classpath, "-d", str(work / "classes"), *map(str, generated)
            ]
            result = runner(javac_command, cwd=ROOT, text=True, capture_output=True)
            stages.append(stage("javac", "PASS" if result.returncode == 0 else "FAIL", "javac <generated-java>", result.returncode, None if result.returncode == 0 else "javac-error", result.stderr, temp))
            return report(stages, None if result.returncode == 0 else "javac")
    except (OSError, subprocess.SubprocessError) as error:
        detail = str(error)
        if phase == "preflight":
            stages.append(stage("source-verification", "FAIL", "carddemo_preflight verify-source", 1, "tool-error", detail))
            blocker = "source-verification"
            stages.extend([
                stage("parse-transpile", "NOT_RUN", "probe transpile"),
                stage("generated-java", "NOT_RUN", "inspect temporary output"),
                stage("javac", "NOT_RUN", "javac <generated-java>"),
            ])
        elif phase == "transpile":
            stages.append(stage("parse-transpile", "FAIL", "./gradlew ...", 1, "tool-or-input-missing", detail))
            blocker = "parse-transpile"
            stages.extend([
                stage("generated-java", "NOT_RUN", "inspect temporary output"),
                stage("javac", "NOT_RUN", "javac <generated-java>"),
            ])
        else:
            stages.append(stage("javac", "FAIL", "javac <generated-java>", 1, "tool-error", detail))
            blocker = "javac"
        return report(stages, blocker)


def report(stages, blocker):
    ordered = [item["name"] for item in stages]
    if blocker:
        seen = False
        for item in stages:
            if item["name"] == blocker:
                seen = True
            elif seen and item["status"] == "PASS":
                item["status"] = "NOT_RUN"
    return {"schemaVersion": 1, "source": {"commit": COMMIT, "candidate": CANDIDATE, "copybook": COPYBOOK}, "stages": stages, "firstBlocker": blocker, "acceptance": {"currentSuccess": blocker is None, "structuredRejectionCorpus": blocker is not None, "nextTask": "Resolve the first blocker and rerun this probe." if blocker else "Proceed to controlled runtime acceptance."}}


def validate(data):
    if data.get("schemaVersion") != 1 or data.get("source", {}).get("commit") != COMMIT or data["source"].get("candidate") != CANDIDATE or data["source"].get("copybook") != COPYBOOK:
        raise ValueError("source identity invalid")
    stages = data.get("stages")
    if not isinstance(stages, list) or [item.get("name") for item in stages] != ["source-verification", "parse-transpile", "generated-java", "javac"]:
        raise ValueError("stage order invalid")
    statuses = {"PASS", "FAIL", "NOT_RUN"}
    for item in stages:
        if item.get("status") not in statuses or not isinstance(item.get("command"), str):
            raise ValueError("stage schema invalid")
        if item["status"] == "FAIL":
            if not isinstance(item.get("errorType"), str) or not item["errorType"] or not isinstance(item.get("exitCode"), int):
                raise ValueError("failure stage evidence invalid")
        elif item["status"] == "PASS" and not isinstance(item.get("exitCode"), int):
            raise ValueError("pass stage evidence invalid")
    blocker = data.get("firstBlocker")
    failures = [item["name"] for item in stages if item["status"] == "FAIL"]
    if blocker != (failures[0] if failures else None):
        raise ValueError("first blocker does not match stages")
    blocked = False
    for item in stages:
        if blocked and item["status"] != "NOT_RUN":
            raise ValueError("downstream stage ran after failure")
        if item["status"] == "FAIL":
            blocked = True
    acceptance = data.get("acceptance", {})
    if acceptance.get("currentSuccess") != (not blocker) or acceptance.get("structuredRejectionCorpus") != bool(blocker):
        raise ValueError("acceptance flags inconsistent")


def main(argv=None):
    parser = argparse.ArgumentParser(); sub = parser.add_subparsers(dest="command", required=True)
    probe = sub.add_parser("probe-source"); probe.add_argument("checkout", type=Path); probe.add_argument("--output", type=Path)
    sub.add_parser("validate-report")
    args = parser.parse_args(argv)
    try:
        if args.command == "probe-source":
            result = run_probe(args.checkout); target = args.output or REPORT; target.write_text(json.dumps(result, indent=2) + "\n"); print(json.dumps({"status": "PASS", "report": str(target)})); return 0
        validate(json.loads(REPORT.read_text()))
        print(json.dumps({"status": "PASS", "command": "validate-report"})); return 0
    except (OSError, ValueError, KeyError) as error:
        print(json.dumps({"status": "FAIL", "error": str(error)})); return 1


if __name__ == "__main__": sys.exit(main())
