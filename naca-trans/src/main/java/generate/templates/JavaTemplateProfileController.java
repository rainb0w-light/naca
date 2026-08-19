package generate.templates;

import generate.templates.recursive.JavaTemplateAssembler;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Selects, validates, and caches template profiles at pipeline boundaries. */
public final class JavaTemplateProfileController
{
    private static final JavaTemplateProfileController INSTANCE =
        new JavaTemplateProfileController();

    private final Map<JavaTemplatePipeline, JavaTemplateProfile> profiles;
    private final ConcurrentMap<JavaTemplatePipeline, JavaTemplateCatalog> catalogs =
        new ConcurrentHashMap<>();
    private final ConcurrentMap<JavaTemplatePipeline, JavaTemplateAssembler> assemblers =
        new ConcurrentHashMap<>();

    private JavaTemplateProfileController()
    {
        EnumMap<JavaTemplatePipeline, JavaTemplateProfile> configured =
            new EnumMap<>(JavaTemplatePipeline.class);
        configured.put(JavaTemplatePipeline.FULL, JavaTemplateProfile.full());
        configured.put(JavaTemplatePipeline.COBOL, JavaTemplateProfile.cobol());
        configured.put(JavaTemplatePipeline.BMS, JavaTemplateProfile.bms());
        configured.put(JavaTemplatePipeline.FPAC, JavaTemplateProfile.fpac());
        profiles = Map.copyOf(configured);
    }

    /** Executes the instance operation. */
    public static JavaTemplateProfileController instance()
    {
        return INSTANCE;
    }

    /** Executes the profile operation. */
    public JavaTemplateProfile profile(JavaTemplatePipeline pipeline)
    {
        return profiles.get(Objects.requireNonNull(pipeline, "pipeline"));
    }

    /** Executes the catalog operation. */
    public JavaTemplateCatalog catalog(JavaTemplatePipeline pipeline)
    {
        return catalogs.computeIfAbsent(
            Objects.requireNonNull(pipeline, "pipeline"),
            selected -> JavaTemplateCatalogFactory.create(profile(selected)));
    }

    /** Creates the assembler. */
    public JavaTemplateAssembler newAssembler(JavaTemplatePipeline pipeline)
    {
        JavaTemplateCatalog catalog =
            JavaTemplateCatalogFactory.create(profile(pipeline));
        return new JavaTemplateAssembler(catalog.group());
    }

    /** Executes the shared assembler operation. */
    public JavaTemplateAssembler sharedAssembler(JavaTemplatePipeline pipeline)
    {
        return assemblers.computeIfAbsent(
            Objects.requireNonNull(pipeline, "pipeline"), this::newAssembler);
    }
}
