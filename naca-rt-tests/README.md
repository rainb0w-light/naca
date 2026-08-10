# Naca runtime tests and sample corpus

This module owns both the runtime fixtures and the source corpus used to validate
COBOL-to-Java translation. The directories deliberately distinguish provenance:

- `src/test/resources/naca-samples/source/cobol/` — COBOL program sources.
- `src/test/resources/naca-samples/source/bms/` — BMS map sources.
- `src/test/resources/naca-samples/source/copybooks/` — COPY/INCLUDE sources.
- `src/main/translated-java/` — legacy translated or hand-adapted Java runtime fixtures.
- `build/` or JUnit temporary directories — freshly generated Java/classes; never committed.

## Correspondence

The imported sample corpus and the legacy Java fixtures are not one historical
one-to-one set. The table records the strongest truthful relationship.

| Source fixture | Java counterpart | Relationship |
| --- | --- | --- |
| `TEST-A-STANDALONE.cbl` | Generated during `SampleEndToEndAcceptanceTest` | Exact: all 34 GnuCOBOL/NacaRT output lines must match |
| `TEST-A.cbl` | `TEST-A-STANDALONE.cbl` is the self-contained acceptance form | Source variant; no committed generated Java |
| `TESTHELLO.cbl` | `nacaTests/CobolLikeSupport/TestHelloWorld.java` | Feature-level hello/display coverage, not the same generated source |
| `T01.cbl` | `TestVarTypes.java`, `TestRedefines.java` | Feature-level data declaration/storage coverage |
| `INSPECT1.cbl`, `INSPECT2.cbl` | `TestInspect.java` | Feature-level INSPECT coverage |
| `UNSTRING1.cbl` | `TestUnstring.java` | Feature-level UNSTRING coverage |
| `VERBS.cbl` | `TestMath.java`, `TestStrings.java`, `nacaTests/ExtraTests/Goto.java` | Feature-level verb coverage |
| `BATCH1.cbl`, `CALLMSG.cbl`, `MSGZONE` | Generated and compiled by cloud-native COPY tests | No committed generated Java |
| `ONLINE1.cbl`, `ONLINM1.bms`, online copybooks | Online corpus and BMS contract tests | Semantic/artifact correspondence; no committed generated Java |
| `REPLACE1.cbl`, `SEARCH1.cbl` | None | Source-only coverage candidates |

Paths in the table under `nacaTests/` are rooted at `src/main/translated-java/`.

## Canonical end-to-end pipeline

From the repository root:

```bash
./gradlew sampleAcceptance
```

The task is implemented in `naca-rt-tests` and requires GnuCOBOL plus JDK 21. It
compiles and runs `TEST-A-STANDALONE.cbl` with GnuCOBOL, transpiles the same source
with Naca, compiles the generated Java with `javac`, runs it on NacaRT, and compares
all 34 canonical output lines exactly.
