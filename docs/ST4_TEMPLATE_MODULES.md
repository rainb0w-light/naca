# ST4 Java template modules

Naca loads Java templates through `JavaTemplateProfileController` and
`JavaTemplateCatalogFactory`. Production code no longer loads one monolithic STG file.
Every transpilation pipeline selects one immutable profile before rendering starts.

## Responsibilities

| Directory | Responsibility |
|---|---|
| `java/common/` | Shared literals, references, expressions, conditions, and compatibility templates |
| `java/cobol/` | COBOL control flow, verbs, procedures, file/data operations, declarations, and roots |
| `java/sql/` | Embedded SQL statements and cursor declarations |
| `java/cics/` | Embedded CICS statements |
| `java/bms/` | BMS references, declarations, actions/conditions, and mapset roots |
| `java/fpac/` | FPac-only references, overrides, file operations, and program root |

The binding manifests remain under `templates/java/`. They map semantic runtime types
to template names and are independent from physical STG file locations.

## Profiles

- `FULL` contains every module and is used for compatibility APIs and global validation.
- `COBOL` contains shared/COBOL modules plus SQL, CICS, and BMS support.
- `BMS` contains shared modules and BMS modules only.
- `FPAC` contains shared modules and FPac overrides only.

Profile selection is allowed only at a pipeline boundary. Templates and the recursive
assembler must not switch profiles while visiting individual semantic nodes.

## Dependency rules

- A module may call templates from itself or from explicitly declared dependencies.
- Dependencies must be acyclic.
- Template names must be unique across a profile; implicit shadowing is forbidden.
- Missing resources, dependencies, bindings, or templates fail closed.
- A module must stay below 500 lines. Split by role or domain before exceeding the limit.
- Templates format semantic values only. They must not call `export()` or consume
  pre-rendered source fields.
- `JavaTemplateAssembler.renderRoot()` remains the only recursive-tree flattening point.

`JavaTemplateModuleDependencyTest` enforces cross-module calls, size limits, and binding
resolution. `templateJarCheck` verifies that every STG module is included in the built
JAR and that the retired `java.stg` and `base.stg` are absent.

## Adding or moving a template

1. Choose the module by semantic responsibility, not by the current caller.
2. Keep the template name globally unique.
3. Add or update the semantic binding manifest when the template renders an entity.
4. If the template calls another module, declare that dependency in
   `JavaTemplateProfile`.
5. Add a byte-exact render test for every changed output branch.
6. Run:

   ```bash
   ./gradlew :naca-trans:test
   ./gradlew :naca-trans:finalArchitectureCheck
   ./gradlew :naca-trans:templateJarCheck
   ./gradlew :naca-trans:templateJarSmoke
   ```

7. For root, BMS, or runtime-affecting changes, also run `./gradlew sampleAcceptance`.

Do not place Java syntax in the profile controller or role policy. Semantic analysis
builds the complete entity tree; STG modules alone own target formatting.
