# CardDemo acceptance inventory

`ACCEPTANCE_INVENTORY.json` is the machine-readable progress report for the
pinned AWS CardDemo commit recorded in `PROVENANCE.json`.

The complete upstream `app/` tree is vendored for offline capability analysis.
`PROVENANCE.json` must contain the SHA-256 of every regular file in that tree
plus the upstream license. Vendoring a source is not evidence that Naca can
translate or execute it; executable status remains governed exclusively by the
acceptance inventory.

The denominator is every regular file below `app/` at that commit whose name
ends in `.cbl`, case-insensitively. There are no exclusions. The current report
therefore contains 44 programs: 3 strict completions, 0 stage-feasible items,
1 blocked item, and 40 not-started items. Strict completion is 3/44, or 6.82%.
The executable CardDemo suite contains 4 scenarios: the 3 strict completions
and 1 expected-blocker regression for `CBACT01C`.

Strict completion means that vendored source and hashes pass offline through
recursive ST4 generation, copybook generation, `javac`, an isolated NacaRT JVM,
and exact business-output comparison. `CBACT02C` remains a 52-line scenario;
`CBCUS01C` and `CBACT03C` are 102-line scenarios. The pinned `cardxref.txt`
omits the copybook's 14-byte trailing filler, so the `CBACT03C` test creates a
right-space-padded fixed-record input in its temporary workspace. The original
vendored data and provenance hash remain unchanged.

`CBACT01C` is also fully vendored and automated through recursive ST4,
two-copybook generation, and `javac`. Its expected-blocker regression then
requires the isolated runtime to reach `ERROR OPENING OUTFILE` and
`ABENDING PROGRAM`. This preserves the first real blocker as executable
evidence: successful `OPEN OUTPUT` does not set the program's COBOL
`FILE STATUS` to `00`.

Regenerate the derived counts and immediately validate the report:

```bash
python3 tools/carddemo_inventory.py --write --check
```

Validate the report, every vendored provenance hash, and every strict asset
without network access:

```bash
python3 tools/carddemo_inventory.py --check
```

Generate and validate the static program/CICS/SQL/COPY/CALL/CSD capability
inventory independently from acceptance status:

```bash
python3 tools/carddemo_capability_inventory.py --write --check
```

If the read-only pinned checkout is available, also prove the 44-program
denominator against its Git commit and filesystem inventory:

```bash
python3 tools/carddemo_inventory.py --check \
  --upstream /tmp/carddemo-cbcus.qee18v
```

Run the 3 strict CardDemo scenarios and the expected-blocker regression with:

```bash
./gradlew :naca-rt-tests:sampleAcceptance \
  --tests '*CardDemoEndToEndAcceptanceTest'
```
