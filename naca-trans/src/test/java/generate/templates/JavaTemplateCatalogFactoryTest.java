package generate.templates;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class JavaTemplateCatalogFactoryTest
{
    @Test
    void missingModuleResourceFailsClosed()
    {
        JavaTemplateProfile profile = new JavaTemplateProfile(
            "missing",
            List.of(new JavaTemplateModule("missing", "/templates/java/missing.stg")));

        IllegalStateException error = assertThrows(
            IllegalStateException.class,
            () -> JavaTemplateCatalogFactory.create(profile));
        assertTrue(error.getMessage().contains("Missing ST4 module resource"));
    }

    @Test
    void unknownDependencyFailsClosed()
    {
        JavaTemplateProfile profile = new JavaTemplateProfile(
            "unknown-dependency",
            List.of(new JavaTemplateModule(
                "common", "/templates/java/common/common.stg", List.of("unknown"))));

        IllegalStateException error = assertThrows(
            IllegalStateException.class,
            () -> JavaTemplateCatalogFactory.create(profile));
        assertTrue(error.getMessage().contains("Unknown dependency unknown"));
    }

    @Test
    void cyclicDependenciesFailClosed()
    {
        JavaTemplateProfile profile = new JavaTemplateProfile(
            "cycle",
            List.of(
                new JavaTemplateModule(
                    "common", "/templates/java/common/common.stg", List.of("legacy")),
                new JavaTemplateModule(
                    "legacy", "/templates/java/common/legacy.stg", List.of("common"))));

        IllegalStateException error = assertThrows(
            IllegalStateException.class,
            () -> JavaTemplateCatalogFactory.create(profile));
        assertTrue(error.getMessage().contains("Cyclic template module dependency"));
    }

    @Test
    void duplicateTemplateNamesAcrossModulesFailClosed()
    {
        JavaTemplateProfile profile = new JavaTemplateProfile(
            "duplicate-template",
            List.of(
                new JavaTemplateModule("first", "/templates/java/common/common.stg"),
                new JavaTemplateModule("second", "/templates/java/common/common.stg")));

        IllegalStateException error = assertThrows(
            IllegalStateException.class,
            () -> JavaTemplateCatalogFactory.create(profile));
        assertTrue(error.getMessage().contains("Duplicate ST template"));
    }
}
