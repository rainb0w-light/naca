# Decision Log

Records decisions taken during the ST4 platform migration. Per the working protocol:
a decision that blocks one branch is logged here and work continues on the
highest-priority unblocked task; only when every path is blocked by the same
decision (or new external permission / a destructive operation is needed) does
work stop for user input.

Default decisions (no confirmation needed):
- recognized-but-unlowered syntax → structured `UnsupportedFeatureDiagnostic` + fail-closed;
- semantic node exists but backend binding missing → `MissingTemplateRendererException`;
- missing runtime capability → `RuntimeCapabilityDiagnostic`;
- parser cannot fully recognize declared syntax → parse error;
- one task blocked but independent work remains → log and continue;
- delete a legacy backend proven to have no production reachability → delete after gates pass.

---

## D-001 — CICS/SQL recognized-but-unlowered syntax fails closed (resolved 2026-07-23)

**Context:** `EXEC CICS SEND TEXT` (non-MAP SEND) hit `return null` in
`CExecCICSSend.DoCustomSemanticAnalysis` (line ~119): an error was logged but the
statement was dropped and the transpile still returned success. This is silent
loss of recognized syntax.

**Decision (user-confirmed):** silent drop is a defect, not compatibility to
preserve. Recognized-but-unlowered syntax must produce a structured, named,
fail-closed error diagnostic and fail compilation. Two distinct failure classes:

- parser/lowering not implemented → `UnsupportedFeatureDiagnostic`
  (feature ID, dialect, source span, phase/reason) → compile fails, never enters
  the assembler;
- semantic node exists but the Java backend has no renderer →
  `MissingTemplateRendererException` (backend fail-closed).

`EXEC CICS SEND TEXT` therefore reports:

```
Unsupported feature: cics.send.text
dialect: CICS
source: <file>:<line>:<column>
reason: syntax recognized but semantic lowering is not implemented
```

This is an error, not a warning; no success, no empty code, no direct fallback,
and no faked incomplete CICS runtime in the fixing commit.

---

## D-002 — T0 ONLINE1 fixtures: canonical sources (open, next slice)

The T0 fail-closed baseline (`onlineCorpusBaseline`, PR #4) is RED because ONLINE1
references four copybooks that are not yet resolvable. Their canonical sources
must be real content — **no empty copybooks**:

- **ONLINM1 / ONLINM1S** — generated from `NacaSamples/cobol/ONLINM1.bms` by the
  BMS transpiler (`BMSTranscoderEngine` produces the map `ONLINM1` and the
  symbolic copy `ONLINM1S`). These are BMS artifacts; route them through the BMS
  parser/artifact inventory, not hand-written.
- **SQLCA** — the IBM-standard DB2 SQL Communication Area copybook (SQLCAID,
  SQLCABC, SQLCODE, SQLERRMC, SQLERRP, SQLERRD(6), SQLWARN, SQLSTATE). naca-rt
  already models SQLCODE (`nacaLib.sqlSupport.SQLCode`); the copybook must match.
- **DFHAID** — the IBM-standard CICS AID copybook (DFHCLEAR, DFHPA1, DFHPA2, ...).
  The project already knows these constants via `NacaTransRules.xml`.

Once these resolve, the `onlineCorpusBaseline` turns green statement-by-statement
as each EXEC SQL / EXEC CICS feature is migrated in T3-T5 (each with a structured
diagnostic or a preserved semantic node).
