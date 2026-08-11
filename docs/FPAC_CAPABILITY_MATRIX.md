# FPac capability matrix

This document defines the maintained FPac production boundary. FPac remains an
independent lexer/parser pipeline which shares Naca's semantic model and renders
through the `FPAC_ROOT` recursive-ST4 profile.

## Executable contract

The canonical source is
`naca-trans/src/test/resources/fpac/SMOKE.fpac`. The dedicated command

```bash
./gradlew :naca-trans:fpacAcceptance
```

proves the complete source-to-Java slice: lex, parse, semantic lowering,
recursive-ST4 rendering and compilation against NacaRT. Runtime tests in
`FPacUndefinedLengthRuntimeTest` separately cover inferred-length packed numeric
read, comparison and increment behavior.

## Supported semantic surface

The parser currently lowers the following families through
`CJavaFPacEntityFactory`: program/data/procedure roots; assignments and
conversion; arithmetic; conditions and loops; function/program calls; control
transfer; input/output file descriptors and buffers; open/read/write/close; WTO
display; strings, numbers, references and substrings.

The exact rendering contract remains the FPac semantic bindings and
`templates/java/fpac/fpac.stg`. Missing bindings are rejected by the recursive
assembler rather than falling back to direct Java emitters.

## Explicit rejections

The following syntax may be recognized by the historical grammar but does not
have defined runtime semantics and therefore fails during semantic lowering:

| Surface | Diagnostic boundary |
| --- | --- |
| `PARM` declarations | `CFPacDeclarationZone` |
| `CB` and `CD` commands | `CFPacMove` |
| PR and CD output-file modes | `CFPacOutputFile` |

Factory operations inherited from `CBaseEntityFactory` but unreachable from the
FPac grammar also fail closed. Their diagnostic names the exact `NewEntity...`
operation, which makes accidental grammar expansion visible in tests and logs.
There are no generic `Method not implemented` branches in the FPac factory.

## Change rule

A new FPac feature is production-ready only when all four conditions hold:

1. parser syntax has a semantic entity mapping;
2. the FPac binding catalog and ST4 module render that entity;
3. generated Java compiles against NacaRT;
4. a focused fixture exercises observable runtime behavior or a structured
   rejection.

Do not replace a structured rejection with a warning, null entity or default
value. Such changes recreate the silent-success behavior this boundary removes.
