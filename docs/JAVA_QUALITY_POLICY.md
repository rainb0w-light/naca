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
| `./gradlew qualityCheck` | Checkstyle, PMD, CPD, SpotBugs, and Spotless | Static-analysis findings are report-only |
| `./gradlew :rewrite-recipes:test` | Verifies semantic modernization recipes | Blocking |

Reports are written below each module's `build/reports/{checkstyle,pmd,spotbugs}`
directory. The aggregate CPD report is `build/reports/pmd/cpd.xml`.

## Ratchet policy

1. Existing findings are recorded as debt; they do not hide compiler or test
   failures.
2. New and edited code must pass Spotless immediately.
3. A Checkstyle, PMD, or SpotBugs rule becomes blocking after its current findings
   are fixed or explicitly baselined.
4. Mechanical formatting and semantic renaming are separate commits. Public API,
   serialization names, reflection strings, template bindings, and generated
   output require compatibility tests before rename.
5. Suppressions must be narrow, documented, and attached to a concrete false
   positive or compatibility requirement.
