# Quality governance loop

Run from any repository directory:

```sh
python3 tools/quality-loop/quality_loop.py validate
python3 tools/quality-loop/quality_loop.py next
python3 tools/quality-loop/verify_task.py QG-000 --base-ref <base-ref>
python3 tools/quality-loop/quality_loop.py transition QG-000 RUNNING --evidence start
```

The verifier locates the repository root automatically, but the script path must still be resolvable from the current directory. From a subdirectory use a relative path such as `python3 ../../tools/quality-loop/verify_task.py ...`, or use the absolute path to the script.

Generate reports first, then collect and compare:

```sh
./gradlew qualityRatchet aggregateCoverageCheck
python3 tools/quality-loop/quality_metrics.py collect --output current.json
python3 tools/quality-loop/quality_metrics.py compare current.json
```

`collect` fails closed when any baseline-described XML report is missing or malformed; `compare` reports only static-analysis/CPD debt and aggregate line-coverage ratchets.

CardDemo preflight is offline after a one-time pinned checkout: `git clone --no-checkout https://github.com/aws-samples/aws-mainframe-modernization-carddemo.git && git -C aws-mainframe-modernization-carddemo checkout 59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e`, then `python3 tools/quality-loop/carddemo_preflight.py verify-source /path/to/aws-mainframe-modernization-carddemo`. The manifest can be checked offline with `python3 tools/quality-loop/carddemo_preflight.py validate-manifest`; the tool never clones or fetches.

Workers flow `RUNNING → REVIEW`; the controller independently runs `verify_task`, then transitions `REVIEW → VERIFIED` only after PASS. Failures may become `REJECTED` or `BLOCKED`, and rejected work can return to `READY`. `next` selects only READY tasks whose dependencies are VERIFIED, ordered by priority then id.

Acceptance is bounded by each task's allowed/forbidden paths, file limit, and declared commands. The verifier independently checks git diff and protected baseline/configuration paths; worker claims are not evidence.
