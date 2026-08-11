# Java quality policy

## Scope

Hand-written Java under `src/main/java` and `src/test/java` is covered. COBOL,
BMS, copybooks, generated output, and
`naca-rt-tests/src/main/translated-java` are compatibility inputs or generated
fixtures and are intentionally outside Java source-style enforcement.

The historical `C` class prefix is part of the public compatibility vocabulary;
the naming cleanup targets field/local prefixes such as `m_`, `ms_`, `cs`, `n`,
and `b` only when a semantic rename can preserve behavior.

## Commands

| Command | Purpose | Current enforcement |
| --- | --- | --- |
| `./gradlew spotlessCheck` | Trailing whitespace, final newline, unused imports | Blocking for files changed since `origin/master` |
| `./gradlew spotlessApply` | Applies the same low-risk cleanup | Explicit developer action |
| `./gradlew qualityCheck` | Checkstyle, PMD, CPD, SpotBugs, and Spotless | Blocking no-growth ratchet |
| `./gradlew sourceDebtCheck` | Generated-comment and active `m_` identifier regression scan | Blocking |
| `./gradlew aggregateCoverageCheck` | Aggregate JaCoCo line coverage | Blocking at 6.5% or higher |
| `./gradlew :rewrite-recipes:test` | Verifies semantic modernization recipes | Blocking |

Reports are written below each module's `build/reports/{checkstyle,pmd,spotbugs}`
directory. The aggregate CPD report is `build/reports/pmd/cpd.xml`.

## Ratchet policy

1. Existing findings are recorded per module and source set in
   `docs/project-quality-baseline.json`. Checkstyle, PMD, SpotBugs, and CPD may
   decrease but may not exceed those values.
2. New and edited code must pass Spotless immediately.
3. Any decrease reported by `qualityRatchet` must be captured by lowering the
   baseline in the same reviewed change; temporary broad suppressions are not an
   acceptable substitute.
4. Mechanical formatting and semantic renaming are separate commits. Public API,
   serialization names, reflection strings, template bindings, and generated
   output require compatibility tests before rename.
5. Suppressions must be narrow, documented, and attached to a concrete false
   positive or compatibility requirement.
6. The aggregate line-coverage floor is a nonzero ratchet. Raising it is expected
   as acceptance fixtures are added; lowering it requires explicit review and
   evidence.
