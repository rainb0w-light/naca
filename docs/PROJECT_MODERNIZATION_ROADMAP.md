# Naca project modernization roadmap

This is the authoritative roadmap for work after completion of the recursive-ST4
migration. Historical ST4 plans remain useful audit records but do not represent
the current queue.

## Baseline (2026-08-11)

- `./gradlew build :naca-trans:finalArchitectureCheck` passes.
- Recursive ST4 is the only production Java generator; all three direct-backend
  inventories are frozen at zero.
- The migration ledger contains 274 terminal entries and no blocked entry.
- `:naca-rt-tests:legacyRuntimeTest` executes 46 tests; 21 currently fail.
- The canonical `sampleAcceptance` covers `TEST-A-STANDALONE` and matches the
  34-line GnuCOBOL baseline. BATCH1 is transpiled and compiled but does not yet
  have an equally strict multi-program runtime acceptance test.
- `naca-analyzer` has no tests and its three public operations are placeholders.
- The CICS runtime has 21 explicit TODO/fake markers in `BaseCESMManager`.
- `CJavaFPacEntityFactory` has 131 explicit `Method not implemented` branches;
  many are inherited capabilities that require classification rather than blind
  implementation. No canonical FPac corpus or shipped configuration exists.
- A lexical audit finds active `m_*` identifiers on 41 source lines in 10 Java
  files (nine production files and one test file).
- The feature registry declares 173 production features; 168 entries do not yet
  name a conformance fixture.
- JaCoCo verification is configured with a zero minimum and therefore provides
  reporting but no coverage gate.

Machine-readable counters live in `project-quality-baseline.json`. A counter may
only decrease unless a reviewed change updates both its rationale and its target.

## Execution order

1. **Quality infrastructure** — activate Spotless and add Checkstyle, PMD,
   SpotBugs, OpenRewrite recipe tests, aggregate reports, and no-growth ratchets.
2. **Reproducible CI** — JDK 21, clean dependency resolution, fast PR checks,
   complete merge checks, and nightly compatibility audits.
3. **Low-risk cleanup** — encoding-safe whitespace/import cleanup, stale generated
   comments, stale architecture documentation, and verified self-assignment bugs.
4. **Hungarian notation** — semantic OpenRewrite recipes, collision and public-API
   guards, then small module-by-module batches. The established `C*` class naming
   convention is not part of this cleanup.
5. **Comment/Javadoc contract** — meaningful public API and invariant comments;
   no generated filler, naked TODO, or comments naming retired implementation.
6. **Acceptance matrix** — strict BATCH1/CALLMSG/file-I/O execution and traceable
   fixtures for every production feature.
7. **Runtime compatibility** — reduce the 21-failure NacaRT ratchet to zero,
   beginning with storage/OCCURS/REDEFINES before string and program-call behavior.
8. **CICS runtime** — implement each production-reachable fake method or reject
   the feature during lowering with a structured diagnostic.
9. **Analyzer/SMOJOL** — real parser/AST/CFG/execution integration, tests,
   timeouts, resource limits, and reproducible cobol-rekt dependencies.
10. **FPac productionization** — canonical corpus and configuration, explicit
    capability classification, and parse-to-runtime acceptance.
11. **Final enforcement** — all quality tools in `check`, nonzero ratcheted
    coverage gates, no temporary suppressions, and the complete CI matrix green.

## Change discipline

- Mechanical formatting, semantic renaming, documentation, and behavior changes
  are separate commits.
- Existing COBOL/BMS/copybook sources, generated output, and
  `naca-rt-tests/src/main/translated-java` are excluded from automatic cleanup.
- Java source encoding remains ISO-8859-1 until an independently planned encoding
  migration is accepted.
- Every slice keeps `build`, `finalArchitectureCheck`, and relevant acceptance
  tests green. Legacy-runtime failures are governed by a decreasing ratchet until
  they reach zero.
