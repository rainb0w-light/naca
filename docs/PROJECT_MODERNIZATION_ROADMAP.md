# Naca project modernization roadmap

This is the authoritative roadmap for work after completion of the recursive-ST4
migration. Historical ST4 plans remain useful audit records but do not represent
the current queue.

## Baseline (2026-08-11)

- `./gradlew build :naca-trans:finalArchitectureCheck` passes.
- Recursive ST4 is the only production Java generator; all three direct-backend
  inventories are frozen at zero.
- The migration ledger contains 274 terminal entries and no blocked entry.
- `:naca-rt-tests:legacyRuntimeTest` executes 46 tests with zero failures and is
  part of the module `check` lifecycle.
- The canonical `sampleAcceptance` contains two strict pipelines: the 34-line
  `TEST-A-STANDALONE` GnuCOBOL comparison, and a BATCH1/CALLMSG/MSGZONE run that
  verifies dynamic CALL linkage plus exact FILEIN/FILEOUT behavior.
- `naca-analyzer` has parser-backed AST/CFG/data-layout analysis and bounded
  SMOJOL execution, with a dedicated integration test source.
- The CICS runtime has no explicit TODO/fake marker in `BaseCESMManager`;
  supported local operations execute real behavior and unsupported indexed-file
  operations fail closed during lowering.
- `CJavaFPacEntityFactory` has no generic `Method not implemented` branch. Its
  grammar-excluded inherited operations now fail closed with the exact factory
  operation name; the shipped smoke corpus is exercised by `fpacAcceptance`.
- The active `m_*` identifier ratchet is zero. Historical `C*` type names remain
  an intentional compatibility convention and are outside this cleanup.
- The feature registry declares 173 production features; 168 entries do not yet
  name a conformance fixture.
- JaCoCo verification is configured with a zero minimum and therefore provides
  reporting but no coverage gate.

## Progress

- M0-M3 are complete: roadmap/baselines, quality tooling, CI/security workflows,
  and transpiler self-assignment cleanup are committed.
- M4-M5 have active no-growth gates and completed first cleanup slices; broader
  semantic naming remains guarded at zero `m_*` identifiers. All Eclipse/IDE
  template boilerplate and generated TODO placeholders have been removed from
  maintained Java sources; remaining meaningful Javadoc debt is governed by the
  static-analysis ratchet.
- M6's canonical multi-program batch slice is complete. Feature-registry fixture
  traceability remains part of the acceptance-matrix work.
- The BATCH1 slice also fixed the runtime `CallParamByRef` constructor defect that
  previously discarded every by-reference `Var` argument.
- M7 is complete: all 65 unqualified self-assignments in maintained NacaRT code
  and all 142 equivalents in JLib are fixed and guarded by module-level tests.
  The 46-program compatibility audit is green after repairing assertion
  collection, artifact classification, call/link resolution, COBOL substring
  and INSPECT expectations, and DISPLAY/COMP-3/sign handling.
- M8 is complete: ENQ/DEQ, ASSIGN, HANDLE AID, and local TS/TD queue behavior
  have concrete runtime semantics. GETMAIN, indexed-file READ/WRITE/REWRITE and
  STARTBR are classified as structured rejections until a storage backend is
  configured; legacy generated entry points throw instead of silently succeeding.
- M9 is complete: Analyzer now runs the real cobol-rekt parse, AST, CFG, data
  layout, and interpreter pipeline. Cloud endpoints delegate to that facade,
  Graphviz renders actual SVG/PNG output, resource limits are tested, and CI
  bootstraps cobol-rekt from a source/submodule lock.
- M10 is complete: a shipped FPac program is parsed, lowered through recursive
  ST4 and compiled; undefined-length numeric runtime operations are executable;
  unsupported factory, PARM, CB/CD command and PR/CD file paths fail closed.
  The maintained capability boundary is documented in `FPAC_CAPABILITY_MATRIX.md`.

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
