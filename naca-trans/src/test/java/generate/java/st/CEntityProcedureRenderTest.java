package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
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
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityProcedure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityProcedure(1, "MAIN-PARA", null).getClass());
        assertEquals(CEntityProcedure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityProcedure(1, "MAIN-PARA", null).getClass());
    }

    @Test
    void recursivelyRendersParagraphBodyAndReference()
    {
        CEntityProcedure procedure = new CEntityProcedure(
            1, "MAIN-PARA", catalog, null);
        procedure.AddChild(new CEntityContinue(1, catalog));

        assertEquals("MAIN_PARA", procedure.getFormattedName());
        assertEquals(
            "Paragraph MAIN_PARA = new Paragraph(this);\n"
                + "public void MAIN_PARA() {\n"
                + "// CONTINUE \n"
                + "}",
            render(procedure));
    }

    @Test
    void formatsNumericProcedureDeclarationAsLegalJava()
    {
        CEntityProcedure procedure = new CEntityProcedure(
            1, "0000-CARDFILE-OPEN", catalog, null);

        assertEquals("$0000_CARDFILE_OPEN", procedure.getFormattedName());
        assertTrue(Character.isJavaIdentifierStart(procedure.getFormattedName().charAt(0)));
        assertEquals(
            "Paragraph $0000_CARDFILE_OPEN = new Paragraph(this);\n"
                + "public void $0000_CARDFILE_OPEN() {\n"
                + "}",
            render(procedure));
    }
}
