# Naca Analyzer

`naca-analyzer` is the bounded integration layer between Naca and cobol-rekt.
It accepts COBOL source text, writes it to an owned temporary workspace, and runs
the real LSP4COBOL/SMOJOL pipeline.

## Capabilities

- parser-backed, serializable AST with source spans;
- deterministic CFG nodes, containment/control-flow edges, and GraphViz DOT;
- DATA DIVISION layout summary;
- SMOJOL interpretation with evaluated conditions and captured `DISPLAY` output;
- source-size, AST-node, CFG-node, execution-step, and wall-clock limits.

The public result records are owned by Naca. No parser or SMOJOL implementation
object escapes the module boundary. `CobolAnalyzer` is `AutoCloseable`; long-lived
services should keep one instance and close it during shutdown.

```java
try (CobolAnalyzer analyzer = new CobolAnalyzer()) {
    CobolAnalyzer.AnalysisResult analysis = analyzer.analyze(source);
    String dot = analysis.cfg().dot();

    CobolAnalyzer.ExecutionResult execution = analyzer.execute(source, null);
}
```

Interactive `ACCEPT` input is not exposed yet. Passing non-empty input fails
closed instead of pretending that input was consumed. Interpreter support is
therefore intended for the SMOJOL statement subset covered by the module tests.

## Dependency lock

The source revision and Maven coordinates are pinned in
`gradle/cobol-rekt.properties`. CI checks out that exact revision with its pinned
submodules and installs only the modules needed by Analyzer. The Gradle
`verifyCobolRektLock` task verifies that the resolved coordinates match the lock.

Run the module gate with:

```bash
./gradlew :naca-analyzer:check :naca-analyzer:verifyCobolRektLock
```
