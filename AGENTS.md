# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

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
./gradlew :naca-trans:transpileWithST4 -PconfigFile=...  # Enable ST4 factory
```

### Tests
```bash
./gradlew test                          # Run all tests
./gradlew :naca-trans:test              # Run transpiler tests
./gradlew :naca-trans:test --tests "*TemplateValidationTest*"  # Single test class
```

### COBOL Workflow (requires GnuCOBOL)
```bash
./gradlew checkCobolCompiler    # Check GnuCOBOL availability
./gradlew compileCobol          # Compile COBOL programs
./gradlew runCobol -Pprogram=BATCH1              # Run original COBOL
./gradlew runTranspiled -Pprogram=BATCH1         # Run transpiled Java
./gradlew compareResults -Pprogram=BATCH1        # Compare outputs
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

3. **Generator** (`generate/`) - Walks the AST and produces Java code:
   - `generate/java/` - Direct Java code generation (verbs, expressions, conditions, forms, CICS, SQL)
   - `generate/java/st/` - ST4-based code generation (uses StringTemplate 4)
   - `generate/fpacjava/` - FPac-specific Java generation
   - `generate/templates/` - ST4 template files and `TemplateLoader`

4. **Transcoder** (`utils/`) - Bridges parser and generator: `Transcoder`, `TranscoderEngine`, `CRulesManager`

### Code Generation: Two Modes

- **Direct generation** (`CJavaExporter` and subclasses): Each AST node has a corresponding `CJava*` class that emits Java strings directly.
- **ST4 templates** (`TemplateLoader` + `generate/java/st/`): Uses StringTemplate 4 group files (`base.stg`, `java/java.stg`) for template-based generation. The ST4 mode is enabled via `-Dnaca.transpiler.factory=st4`.

### Key Entry Points

- `NacaTrans.java` - Main CLI entry point
- `NacaTransTask.java` - Ant/Gradle task wrapper
- `CCobolParser.java` - COBOL parser
- `CJavaExporter.java` - Java code generator
- `TemplateLoader.java` - ST4 template loader

### ST4 Template Files

Located in `naca-trans/src/main/resources/templates/`:
- `base.stg` - Base templates (utility functions, data references, placeholders)
- `java/java.stg` - Java-specific templates (control flow, data ops, file ops, expressions)

## Important Conventions

- **Source encoding**: ISO-8859-1 for Java compilation (COBOL source compatibility)
- **Naming**: COBOL-style names are preserved with `C` prefix in Java (e.g., `CJavaAssign`, `CCobolParser`)
- **PUSH model**: Controllers push entity objects to templates; templates handle all formatting/conditionals
- **JVM args**: Requires `--add-exports` and `--add-opens` for internal XML/Xalan classes
- **Maven repo**: Uses Aliyun mirrors for faster downloads in China; local Maven repo for cobol-rekt modules

## Sample Programs

COBOL sample programs are in `NacaSamples/` directory for testing the transpiler end-to-end.

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
- Codex主控: analyze errors, locate root cause, provide file:line fixes, verify via API
- opencode: execute code fixes following the ST4 refactoring principle above

**Work Cycle:**
1. Codex主控 calls transpile API → gets error logs
2. Codex主控 analyzes error → provides exact file:line, root cause, fix direction
3. Codex主控 hands off to opencode with clear requirements
4. opencode fixes code
5. Codex主控 re-tests → repeat until pass
6. Codex主控 compares against GnuCOBOL expected output

**Verification Standard:**
- API `POST http://localhost:8000/api/transpile` returns `success: true`
- Generated Java code compiles with `javac`
- Runtime output matches GnuCOBOL baseline (`NacaSamples/cobol/TEST-A-STANDALONE.cbl`, 32 lines, 16 scenarios)
