package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityContinue;
import utils.CObjectCatalog;

class CEntityProcedureRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityProcedure procedure)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(procedure, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void stFactoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityProcedure.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityProcedure(1, "MAIN-PARA", null));
    }

    @Test
    void recursivelyRendersParagraphBodyAndReference()
    {
        CEntityProcedure procedure = new CEntityProcedure(
            1, "MAIN-PARA", catalog, null);
        procedure.AddChild(new CEntityContinue(1, catalog));

        assertEquals("MAIN_PARA", procedure.ExportReference(1));
        assertEquals(
            "Paragraph MAIN_PARA = new Paragraph(this);\n"
                + "public void MAIN_PARA() {\n"
                + "// CONTINUE \n"
                + "}",
            render(procedure));
    }
}
