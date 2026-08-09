package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CFPacUnknownReference;
import semantic.CEntityUnknownReference;
import semantic.Verbs.CEntityDisplay;
import semantic.Verbs.CEntityDisplay.Upon;
import utils.CObjectCatalog;

/** Retirement proof for the FPac unresolved-reference backend. */
class CFPacJavaUnknownReferenceRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

    @Test
    void factoryBuildsPureFpacSemanticMarkerWithDeclarativeReferenceBinding()
    {
        CEntityUnknownReference reference =
            factory.NewEntityUnknownReference(1, "MISSING");

        assertEquals(CFPacUnknownReference.class, reference.getClass());
        assertFalse(reference.HasAccessors());
        assertFalse(reference.isValNeeded());
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(reference, 1));
        assertEquals("[UNDEFINED]", TemplateLoader.getRecursiveAssembler()
            .renderRoot(reference, JavaTemplateRole.FPAC_REFERENCE));
    }

    @Test
    void migratedFpacVerbKeepsUndefinedOperandMarker()
    {
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(factory.NewEntityUnknownReference(1, "MISSING"));

        assertEquals("wto.display([UNDEFINED]) ;", TemplateLoader.getRecursiveAssembler()
            .renderRoot(display, JavaTemplateRole.FPAC_REFERENCE).trim());
    }
}
