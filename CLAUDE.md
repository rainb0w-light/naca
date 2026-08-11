# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Naca** is a COBOL to Java transpiler. It parses COBOL source code (including CICS, SQL, BMS, and FPac dialects) and generates equivalent Java code. The project targets JDK 21 and uses Gradle.

## Build & Development Commands

### Build
```bash
./gradlew build              # Build all modules
./gradlew :naca-trans:build  # Build specific module
```

### Run Transpiler
```bash
./gradlew :naca-trans:run -PconfigFile=path/to/config.txt
./gradlew :naca-trans:transpile -PconfigFile=... -PinputDir=... -PoutputDir=...
```

### Tests
```bash
./gradlew test                          # Run all tests
./gradlew :naca-trans:test              # Run transpiler tests
./gradlew :naca-trans:test --tests "*TemplateValidationTest*"  # Single test class
```

### End-to-End Acceptance (requires GnuCOBOL and JDK 21)
```bash
./gradlew sampleAcceptance      # GnuCOBOL vs Naca -> javac -> NacaRT, strict 34-line diff
```

### Code Quality
```bash
./gradlew :naca-trans:spotlessApply   # Format code
./gradlew jacocoAggregateReport       # Aggregate coverage report
```

## Architecture

### Module Dependency Graph

```
naca-jlib (shared utilities, no deps)
    ├── naca-rt (COBOL runtime library)
    ├── naca-trans (transpiler) ─── depends on naca-jlib
    │       └── naca-rt-tests (integration tests)
    ├── naca-cloud-native (Spring Boot cloud services)
    └── naca-analyzer (COBOL code analysis)
```

### Transpiler Pipeline (`naca-trans`)

The transpiler follows a classic compiler pipeline:

1. **Lexer** (`lexer/`) - Tokenizes COBOL source into tokens (keywords, identifiers, strings, numbers, comments). Key classes: `CBaseLexer`, `CTokenList`.

2. **Parser** (`parser/`) - Builds an AST from tokens. Supports multiple dialects:
   - `parser/Cobol/` - Standard COBOL (MOVE, IF, PERFORM, READ, etc.)
   - `parser/Cobol/elements/CICS/` - CICS EXEC CICS statements
   - `parser/Cobol/elements/SQL/` - Embedded SQL statements
   - `parser/FPac/` - FPac dialect
   - `parser/BMS/` - BMS map parsing
   - Key class: `CCobolParser`

3. **Generator** (`generate/`) - Renders the completed semantic tree:
   - `generate/templates/recursive/` - The unique production recursive-ST4 assembler
   - `generate/templates/` - ST4 loading and binding infrastructure
   - `generate/java/forms/` - BMS artifact writers around the semantic form model
   - `generate/java/` and `generate/fpacjava/` - Compatibility namespaces only; direct semantic backends have been retired

4. **Transcoder** (`utils/`) - Bridges parser and generator: `Transcoder`, `TranscoderEngine`, `CRulesManager`

### Code Generation: One Production Mode

- **Recursive ST4 generation** (`JavaTemplateAssembler` + `TemplateLoader`): declarative manifests bind semantic entity types to an explicit, validated catalog of Java template modules.
- ST4 is unconditional in production. The historical `naca.transpiler.factory` property is accepted only by compatibility tests and cannot select a direct fallback.
- `CJavaExporter` remains an identifier/export utility and root output sink; it is not an alternative semantic code generator.

### Key Entry Points

- `NacaTrans.java` - Main CLI entry point
- `NacaTransTask.java` - Ant/Gradle task wrapper
- `CCobolParser.java` - COBOL parser
- `JavaTemplateAssembler.java` - Unique Java semantic-tree renderer
- `CJavaExporter.java` - Identifier/export utility and output sink
- `TemplateLoader.java` - ST4 template loader

### ST4 Template Files

Located in `naca-trans/src/main/resources/templates/`:
- `java/common/` - shared references, expressions, and compatibility templates
- `java/cobol/` - control flow, data/file operations, declarations, roots, and verbs
- `java/sql/` - embedded SQL templates
- `java/cics/` - embedded CICS templates
- `java/bms/` - BMS map and form templates
- `java/fpac/` - FPac pipeline overrides and roots
- `java/semantic-*-bindings.properties` - role- and pipeline-specific semantic bindings

`JavaTemplateProfile` declares every module explicitly. `JavaTemplateCatalogFactory`
loads dependencies, rejects missing resources and duplicate template names, registers
renderers, and exposes one logical ST group to `JavaTemplateAssembler`.

## Important Conventions

- **Source encoding**: ISO-8859-1 for Java compilation (COBOL source compatibility)
- **Naming**: COBOL-style names are preserved with `C` prefix in Java (e.g., `CJavaAssign`, `CCobolParser`)
- **PUSH model**: Controllers push entity objects to templates; templates handle all formatting/conditionals
- **JVM args**: Requires `--add-exports` and `--add-opens` for internal XML/Xalan classes
- **Maven repo**: Uses Aliyun mirrors for faster downloads in China; local Maven repo for cobol-rekt modules

## Sample Programs

COBOL/BMS/copybook samples are owned by `naca-rt-tests/src/test/resources/naca-samples/source/`.
Legacy translated runtime fixtures are isolated under `naca-rt-tests/src/main/translated-java/`.

## ST4 Refactoring: Architecture Principle

**Core Principle: Semantic Analysis and Code Generation must be strictly separated.**

### Two-Stage Pipeline

**Stage 1 — Semantic Analysis (Java code):**
- `entity.export()` converts AST nodes into **semantic entity objects**
- Responsibilities: resolve variable references, compute expressions, handle scoping, build complete semantic object tree
- When done, `entity.left`, `entity.condition`, `entity.children` etc. should point to ready semantic sub-entities
- `export()` **does NOT return strings, does NOT write output, does NOT trigger template rendering**

**Stage 2 — Code Generation (ST4 templates):**
- Templates only handle **formatting output**
- Access entity properties (e.g., `<entity.condition>`) to get string values
- **NO `.export()` or `.exportChildren()` method calls** — only property access
- Child rendering unfolds naturally through property access because sub-entities are already built in Stage 1
- Templates only care about: which field to use, how to concatenate, what conditions to render

### Anti-Pattern (DO NOT do this)

```st4
condition(entity) ::= <<
if (<entity.condition.export()>)        // ❌ calling export in template triggers semantic analysis
{
  <entity.thenBloc.exportChildren()>     // ❌ iterating and rendering children in template
}
>>
```

ST4 4.3.4 does not support `<obj.method()>` syntax — it fails with `'(' came as a complete surprise to me`. Even if it worked, this mixes semantic analysis with rendering.

### Correct Pattern

```st4
condition(entity) ::= <<
if (<entity.condition>)                // ✅ semantic entity ready, template only takes its string value
{
  <entity.thenBloc>                     // ✅ child entities built in Stage 1
}
>>
```

## OpenCode Driving Workflow

**Roles:**
- Claude主控: analyze errors, locate root cause, provide file:line fixes, verify via API
- opencode: execute code fixes following the ST4 refactoring principle above

**Work Cycle:**
1. Claude主控 calls transpile API → gets error logs
2. Claude主控 analyzes error → provides exact file:line, root cause, fix direction
3. Claude主控 hands off to opencode with clear requirements
4. opencode fixes code
5. Claude主控 re-tests → repeat until pass
6. Claude主控 compares against GnuCOBOL expected output

**Verification Standard:**
- API `POST http://localhost:8000/api/transpile` returns `success: true`
- Generated Java code compiles with `javac`
- Runtime output matches the 34-line GnuCOBOL baseline from
  `naca-rt-tests/src/test/resources/naca-samples/source/cobol/TEST-A-STANDALONE.cbl`
- `./gradlew :naca-trans:finalArchitectureCheck` passes with zero direct-backend debt
