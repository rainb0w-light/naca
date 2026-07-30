# ST4 Phase 2 — BMS + FPac Migration

Phase 1 (COBOL core + embedded SQL + embedded CICS onto the unified
declarative-manifest + recursive-ST4 assembler) is **complete and frozen**:
every queue item is terminal, `meta.ratchet.finalArchitectureCheck.directBackends`
is 0, and the checked-in `architecture.DirectBackendInventoryTest` baseline is 0.
It stays replayable via `--phase phase-1-cobol` but is never re-opened.

Phase 2 migrates the two remaining **independent DSL pipelines**:

| Pipeline | Scope | Generator root | Semantic tree | Backends | Ratchet |
|---|---|---|---|---|---|
| BMS map-resource DSL (`.bms` CICS screen maps) | `BMS_ARTIFACT` | `generate/java/forms` | `semantic/forms` | 34 | `BmsFormsDirectBackendInventoryTest` / `bmsDirectBackends` |
| FPac | `FPAC` | `generate/fpacjava` | shared `semantic/` model | 43 | `FPacDirectBackendInventoryTest` / `fpacDirectBackends` |

**Neither is a COBOL dialect.** Their parsing and semantics stay decoupled from
COBOL machinery; common rendering infrastructure (ST4 group files, the runtime
contract, `DiagnosticSink` fail-closed behavior) is reused only where the shared
semantic model genuinely supports it. FPac's emitters subclass the same shared
semantic entities as COBOL but render differently (its own runtime
`nacaLib.fpacPrgEnv.FPacProgram`), so FPac binding manifests/assembler roles must
stay FPac-specific. BMS emits `nacaLib.mapSupport.*`.

## Loop configuration

`tools/st4-loop/run-loop.sh --phase phase-2-bms-fpac` (the active default). The
phase declares an **ordered** scope queue: the scheduler drains every READY
`BMS_ARTIFACT` item before any `FPAC` item; within a scope the usual
priority/status/id ordering applies; exactly one item goes to one fresh worker
per iteration. All phase-1 protections are retained: single-slice, fail-closed,
no silent drops, exact declared files, independent review, controller-owned
commits, git deny rules, no `bypassPermissions`.

BMS retirement order follows containment (leaves → root): inline
attribute/condition/action emitters first (`priority 9000+`), field-level blocks
(`9030+`), form-level (`9040+`), the mapset container last (`9050`), dead wiring
at the end (`9060+`). FPac items sit at `9500+`.

## Per-scope debt mechanics

- Each scope's debt is measured independently in the worktree and must match the
  item's `expectedDebtDelta` counter exactly: `bmsDirectBackends` (BMS),
  `fpacDirectBackends` (FPAC), `directBackends` (COBOL scopes, frozen at 0).
- Counters ratchet down in `meta.ratchet.phase2.{bms,fpac}ArchitectureCheck`;
  growth of any `*DirectBackends` counter is refused.
- Each retirement slice tightens the matching checked-in Java inventory baseline
  in the same commit; the controller fails a slice whose checked-in baseline is
  stale.
- `LedgerConsistencyTest` walks all three generator roots and requires every
  live backend to have exactly one owning ledger item with the scope-correct
  expected delta, and each ratchet to equal its live inventory ± one in-flight
  retirement.

## Known traps (ledger `meta.discoveredDebt`)

- `FPAC-LEGACY-SILENT-DROP` — the legacy FPac traversal silently skipped
  structural nodes without `DoExport` (empty if/loop bodies). FPac slices must
  not preserve that hole; fail closed via `DiagnosticSink.recordUnsupported`.
- `FPAC-GENERATOR-COUPLINGS` — `CFPacJavaCondAnd/Or/Not` call the static
  COBOL-side `CJavaExporter.ExportChildCondition`; `CFPacScript` imports the FPac
  factory (parser→generator). Resolve during retirement; stay decoupled.
- `BMS-XML-OUTPUT-IN-SEMANTIC` — `semantic/forms` classes still contain XML/.res
  output backends; separate or template them under the same principle.

## Completion condition

Every production-reachable BMS/FPac ledger item terminal; `bmsDirectBackends`
and `fpacDirectBackends` at 0 with the Java baselines tightened to 0; generated
outputs compile where applicable; representative end-to-end behavior preserved
(BMS artifact contract `BmsArtifactContractTest` green against
`NacaSamples/cobol/ONLINM1.bms`; FPac focused fixtures green — no shipped corpus
exists); all prior COBOL/SQL/CICS gates stay green; final architecture checks
pass for all enabled scopes.
