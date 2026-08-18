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

Probe the default pinned CBACT02C candidate with `python3 tools/quality-loop/carddemo_probe.py probe-source /path/to/checkout`; validate it with `python3 tools/quality-loop/carddemo_probe.py validate-report`. Probe CBCUS01C with `python3 tools/quality-loop/carddemo_probe.py probe-source /path/to/checkout --candidate CBCUS01C --output docs/quality-governance/carddemo-customer-feasibility.json`, then validate it offline with `python3 tools/quality-loop/carddemo_probe.py validate-report --candidate CBCUS01C --report docs/quality-governance/carddemo-customer-feasibility.json`.

Run the default diagnostic runtime probe with `python3 tools/quality-loop/carddemo_runtime_probe.py probe-runtime /path/to/checkout`, then validate it with `python3 tools/quality-loop/carddemo_runtime_probe.py validate-report`. Probe CBCUS01C with `python3 tools/quality-loop/carddemo_runtime_probe.py probe-runtime /path/to/checkout --candidate CBCUS01C --output docs/quality-governance/carddemo-customer-runtime.json`, then validate it offline with `python3 tools/quality-loop/carddemo_runtime_probe.py validate-report --candidate CBCUS01C --report docs/quality-governance/carddemo-customer-runtime.json`. The probes use pinned data descriptors and never change product sources.

Workers flow `RUNNING → REVIEW`; the controller independently runs `verify_task`, then transitions `REVIEW → VERIFIED` only after PASS. Failures may become `REJECTED` or `BLOCKED`, and rejected work can return to `READY`. `next` selects only READY tasks whose dependencies are VERIFIED, ordered by priority then id.

Acceptance is bounded by each task's allowed/forbidden paths, file limit, and declared commands. The verifier independently checks git diff and protected baseline/configuration paths; worker claims are not evidence.

Run the vendored offline CardDemo acceptance with `./gradlew :naca-rt-tests:sampleAcceptance --tests '*CardDemoEndToEndAcceptanceTest*'`; its two tests cover CBACT02C's exact 52-line output and CBCUS01C's exact 102-line repeated-record output. The fixture is under `naca-rt-tests/src/test/resources/carddemo` and is pinned by `PROVENANCE.json`.
