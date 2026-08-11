# cobol-rekt integration contract

## Ownership boundary

`naca-analyzer` is the only Naca module that directly integrates the
LSP4COBOL/SMOJOL parser and interpreter APIs. `naca-cloud-native` depends on the
stable Naca facade and adapts its records to HTTP responses; it does not build a
second parser model.

The integration provides:

| Capability | Implementation | Contract test |
|---|---|---|
| AST | LSP4COBOL `ParsePipeline` | `CobolAnalyzerTest.buildsRealAstCfgAndDataLayout` |
| CFG/DOT | SMOJOL `FlowNode` model | `CobolAnalyzerTest.graphIdentifiersAreDeterministic` |
| Data layout | SMOJOL `CobolDataStructure` | `CobolAnalyzerTest.buildsRealAstCfgAndDataLayout` |
| Execution | `CobolInterpreterFactory` | `CobolAnalyzerTest.interpretsDisplayUsingRuntimeData` |
| API adapter | `SmojolService` | `SmojolServiceTest` |
| SVG/PNG | graphviz-java + GraalJS Community | `SmojolController.visualize` |

## Resource limits

The default facade limits source length, AST/CFG size, executed instructions,
and elapsed time. Execution-step exhaustion and timeouts are structured failures.
Temporary source workspaces are deleted after every operation. Interactive
`ACCEPT` input is not currently bound and non-empty input fails closed.

The visualization endpoint uses the embedded GraalJS Community engine on JDK
21, so production and CI do not require a host `dot` executable. The pinned
LSP4COBOL engine currently embeds its Logback provider; dependency exclusions
prevent a second external provider, while the facade itself logs only through
SLF4J.

## Reproducible dependency bootstrap

The upstream repository, source commit, submodule commits, and resolved Maven
versions are recorded in `gradle/cobol-rekt.properties`. The verification CI job:

1. reads the repository and commit from that lock;
2. checks out the exact commit with recursive submodules;
3. installs `smojol-core` and `smojol-toolkit` plus required reactor modules;
4. runs `:naca-analyzer:verifyCobolRektLock` through the normal `check` lifecycle.

For local development, build the same locked checkout into Maven Local before
running Gradle. Do not replace a locked SNAPSHOT with an arbitrary working-tree
build without updating the source lock and rerunning Analyzer contract tests.

```bash
mvn --file /path/to/cobol-rekt/pom.xml \
  --projects smojol-core,smojol-toolkit --also-make install \
  -DskipTests -Dmaven.test.skip=true -Dcheckstyle.skip

./gradlew :naca-analyzer:check :naca-cloud-native:test
```
