package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityReturn;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac RETURN control backend. */
class CFPacJavaReturnRetirementTest
{
    @Test
    void pureReturnRendersNextAndSuppressesProcedureFallback()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityReturn action = factory.NewEntityReturn(2);
        CEntityProcedure procedure = factory.NewEntityProcedure(1, "NORMAL", null);
        procedure.AddChild(action);

        assertEquals(CEntityReturn.class, action.getClass());
        assertEquals("return NEXT ;", render(action));
        assertEquals("protected int normal() {\nreturn NEXT ;\n}", render(procedure));
    }

    private static String render(Object entity)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.FPAC_REFERENCE).trim();
    }
}
