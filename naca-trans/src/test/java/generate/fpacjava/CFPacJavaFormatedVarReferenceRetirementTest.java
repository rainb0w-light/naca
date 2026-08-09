package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityFormatedVarReference;
import semantic.CEntityStructure;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the former FPac formatted-variable direct backend. */
class CFPacJavaFormatedVarReferenceRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CEntityStructure variable = new CEntityStructure(1, "CUSTOMER-NAME", catalog, "01");
        CEntityFormatedVarReference formatted = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityFormatedVarReference(variable, "ZZZZZZ");

        assertEquals(CEntityFormatedVarReference.class, formatted.getClass());
        assertEquals("ZZZZZZ", formatted.getFormat());
        assertEquals(variable, formatted.getReference());
        assertTrue(formatted.HasAccessors());
    }

    @Test
    void productionCompatibilityBridgeRendersWrappedReferenceThroughAssembler()
    {
        CEntityStructure variable = new CEntityStructure(1, "CUSTOMER-NAME", catalog, "01");
        CEntityFormatedVarReference formatted = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityFormatedVarReference(variable, "ZZZZZZ");

        assertEquals(LegacyDataRenderer.renderReference(variable, 1),
            LegacyDataRenderer.renderReference(formatted, 1));
        assertEquals(LegacyDataRenderer.renderReference(variable, 1),
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(formatted, JavaTemplateRole.FPAC_REFERENCE).trim());
    }

    @Test
    void deadValueParameterizedWriteProtocolIsAbsentFromSemanticEntity()
    {
        CEntityStructure variable = new CEntityStructure(1, "CUSTOMER-NAME", catalog, "01");
        CEntityFormatedVarReference formatted = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityFormatedVarReference(variable, "ZZZZZZ");

        assertNull(LegacyDataRenderer.renderWriteAccessor(formatted, "SOURCE"));
    }
}
