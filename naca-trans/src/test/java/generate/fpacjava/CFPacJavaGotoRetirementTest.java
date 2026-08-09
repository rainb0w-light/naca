package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntityGoto;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the former FPac GOEND/GOLAST direct backend. */
class CFPacJavaGotoRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityGoto.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityGoto(1, "LAST", null).getClass());
    }

    @Test
    void fpacRolePreservesGoLastAndGoEndControlReturns()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        assertEquals("return LAST ;", render(factory.NewEntityGoto(1, "LAST", null)));
        assertEquals("return END ;", render(factory.NewEntityGoto(2, "END", null)));
    }

    @Test
    void productionAssemblerUsesFpacOverride()
    {
        CEntityGoto goTo = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityGoto(1, "LAST", null);

        assertEquals("return LAST ;", render(goTo));
    }

    private static String render(CEntityGoto goTo)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(goTo, JavaTemplateRole.FPAC_REFERENCE).trim();
    }
}
