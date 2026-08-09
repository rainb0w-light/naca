package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityGoto;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac procedure container backend. */
class CFPacJavaProcedureRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void factoryReturnsPureSemanticProcedureWithImplicitNext()
    {
        CEntityProcedure procedure = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityProcedure(1, "MAIN-SUBR", null);

        assertEquals(CEntityProcedure.class, procedure.getClass());
        String rendered = render(procedure);
        assertTrue(rendered.contains("protected int main$subr() {"), rendered);
        assertTrue(rendered.contains("return NEXT ;"), rendered);
    }

    @Test
    void explicitControlReturnSuppressesImplicitNext()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityProcedure procedure = factory.NewEntityProcedure(1, "MAIN", null);
        CEntityGoto goEnd = factory.NewEntityGoto(2, "END", null);
        procedure.AddChild(goEnd);

        assertEquals("protected int main() {\nreturn END ;\n}", render(procedure));
    }

    private static String render(CEntityProcedure procedure)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(procedure, JavaTemplateRole.FPAC_REFERENCE).trim();
    }
}
