package generate.templates;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JavaTemplateProfileControllerTest
{
    private final JavaTemplateProfileController controller =
        JavaTemplateProfileController.instance();

    @Test
    void cobolProfileIncludesEmbeddedDomainsButExcludesFpacOverrides()
    {
        JavaTemplateCatalog catalog = controller.catalog(JavaTemplatePipeline.COBOL);

        assertTrue(catalog.hasTemplate("javaProgramRoot"));
        assertTrue(catalog.hasTemplate("recursiveCICSReturnEntity"));
        assertTrue(catalog.hasTemplate("recursiveSQLCommitEntity"));
        assertTrue(catalog.hasTemplate("bmsMapsetRoot"));
        assertFalse(catalog.hasTemplate("recursiveFPacClassEntity"));
    }

    @Test
    void fpacProfileIncludesSharedTemplatesButExcludesCobolEmbeddedDomains()
    {
        JavaTemplateCatalog catalog = controller.catalog(JavaTemplatePipeline.FPAC);

        assertTrue(catalog.hasTemplate("recursiveMoveEntity"));
        assertTrue(catalog.hasTemplate("recursiveFPacClassEntity"));
        assertFalse(catalog.hasTemplate("javaProgramRoot"));
        assertFalse(catalog.hasTemplate("recursiveCICSReturnEntity"));
        assertFalse(catalog.hasTemplate("recursiveSQLCommitEntity"));
        assertFalse(catalog.hasTemplate("bmsMapsetRoot"));
    }

    @Test
    void bmsProfileIncludesSharedExpressionsAndOnlyBmsRoots()
    {
        JavaTemplateCatalog catalog = controller.catalog(JavaTemplatePipeline.BMS);

        assertTrue(catalog.hasTemplate("recursiveConditionChild"));
        assertTrue(catalog.hasTemplate("bmsMapsetRoot"));
        assertFalse(catalog.hasTemplate("javaProgramRoot"));
        assertFalse(catalog.hasTemplate("recursiveCICSReturnEntity"));
        assertFalse(catalog.hasTemplate("recursiveSQLCommitEntity"));
        assertFalse(catalog.hasTemplate("recursiveFPacClassEntity"));
    }

    @Test
    void assemblersAreCachedPerPipelineAndNeverSharedAcrossProfiles()
    {
        assertSame(
            controller.sharedAssembler(JavaTemplatePipeline.COBOL),
            controller.sharedAssembler(JavaTemplatePipeline.COBOL));
        assertNotSame(
            controller.sharedAssembler(JavaTemplatePipeline.COBOL),
            controller.sharedAssembler(JavaTemplatePipeline.FPAC));
    }
}
