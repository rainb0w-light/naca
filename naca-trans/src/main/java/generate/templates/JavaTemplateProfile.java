package generate.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Immutable set of template modules selected once for a transpilation pipeline. */
public record JavaTemplateProfile(String id, List<JavaTemplateModule> modules)
{
    private static final JavaTemplateModule LEGACY = module(
        "java-legacy", "/templates/java/common/legacy.stg");
    private static final JavaTemplateModule COMMON = module(
        "java-common", "/templates/java/common/common.stg");
    private static final JavaTemplateModule DATA_OPERATIONS = module(
        "java-data-operations", "/templates/java/cobol/data-operations.stg");
    private static final JavaTemplateModule SEMANTIC_EXPRESSIONS = module(
        "java-semantic-expressions",
        "/templates/java/common/semantic-expressions.stg",
        "java-data-operations");
    private static final JavaTemplateModule CONTROL_FLOW = module(
        "java-control-flow",
        "/templates/java/cobol/control-flow.stg",
        "java-data-operations");
    private static final JavaTemplateModule DECLARATIONS = module(
        "java-declarations", "/templates/java/cobol/declarations.stg");
    private static final JavaTemplateModule FILE_OPERATIONS = module(
        "java-file-operations", "/templates/java/cobol/file-operations.stg");
    private static final JavaTemplateModule PROCEDURES = module(
        "java-procedures", "/templates/java/cobol/procedures.stg");
    private static final JavaTemplateModule VERBS = module(
        "java-verbs", "/templates/java/cobol/verbs.stg");
    private static final JavaTemplateModule ROOTS = module(
        "java-roots", "/templates/java/cobol/roots.stg");
    private static final JavaTemplateModule BMS_REFERENCES = module(
        "java-bms-references", "/templates/java/bms/references.stg");
    private static final JavaTemplateModule BMS_DECLARATIONS = module(
        "java-bms-declarations", "/templates/java/bms/declarations.stg");
    private static final JavaTemplateModule BMS_ACTIONS = module(
        "java-bms-actions", "/templates/java/bms/actions.stg");
    private static final JavaTemplateModule BMS_ROOTS = module(
        "java-bms-roots", "/templates/java/bms/roots.stg");
    private static final JavaTemplateModule CICS = module(
        "java-cics", "/templates/java/cics/cics.stg");
    private static final JavaTemplateModule FPAC = module(
        "java-fpac", "/templates/java/fpac/fpac.stg");
    private static final JavaTemplateModule SQL = module(
        "java-sql", "/templates/java/sql/sql.stg");

    private static final List<JavaTemplateModule> SHARED = List.of(
        LEGACY, COMMON, DATA_OPERATIONS, SEMANTIC_EXPRESSIONS, CONTROL_FLOW,
        DECLARATIONS, FILE_OPERATIONS, PROCEDURES, VERBS);
    private static final List<JavaTemplateModule> BMS_MODULES = List.of(
        BMS_REFERENCES, BMS_DECLARATIONS, BMS_ACTIONS, BMS_ROOTS);

    private static final JavaTemplateProfile FULL = profile(
        "java-full", SHARED, List.of(ROOTS), BMS_MODULES, List.of(CICS, FPAC, SQL));
    private static final JavaTemplateProfile COBOL = profile(
        "java-cobol", SHARED, List.of(ROOTS), BMS_MODULES, List.of(CICS, SQL));
    private static final JavaTemplateProfile BMS = profile(
        "java-bms", SHARED, BMS_MODULES);
    private static final JavaTemplateProfile FPAC_PROFILE = profile(
        "java-fpac", SHARED, List.of(FPAC));

    /** Executes the java template profile operation. */
    public JavaTemplateProfile
    {
        Objects.requireNonNull(id, "id");
        if (id.isBlank())
        {
            throw new IllegalArgumentException("Profile id must not be blank");
        }
        modules = List.copyOf(Objects.requireNonNull(modules, "modules"));
        if (modules.isEmpty())
        {
            throw new IllegalArgumentException("Template profile must contain a module");
        }
    }

    /** Executes the full operation. */
    public static JavaTemplateProfile full()
    {
        return FULL;
    }

    /** Executes the cobol operation. */
    public static JavaTemplateProfile cobol()
    {
        return COBOL;
    }

    /** Executes the bms operation. */
    public static JavaTemplateProfile bms()
    {
        return BMS;
    }

    /** Executes the fpac operation. */
    public static JavaTemplateProfile fpac()
    {
        return FPAC_PROFILE;
    }

    @SafeVarargs
    private static JavaTemplateProfile profile(
        String id, List<JavaTemplateModule>... moduleGroups)
    {
        ArrayList<JavaTemplateModule> modules = new ArrayList<>();
        for (List<JavaTemplateModule> moduleGroup : moduleGroups)
        {
            modules.addAll(moduleGroup);
        }
        return new JavaTemplateProfile(id, modules);
    }

    private static JavaTemplateModule module(String id, String resourcePath)
    {
        return new JavaTemplateModule(id, resourcePath);
    }

    private static JavaTemplateModule module(
        String id, String resourcePath, String... dependencies)
    {
        return new JavaTemplateModule(id, resourcePath, List.of(dependencies));
    }
}
