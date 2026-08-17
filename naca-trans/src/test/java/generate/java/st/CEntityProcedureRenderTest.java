package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertDoesNotThrow(this::assertFactoriesReturnSemanticEntities,
            "both procedure factories should return semantic entities");
    }

    private void assertFactoriesReturnSemanticEntities()
    {
        assertEquals(CEntityProcedure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityProcedure(1, "MAIN-PARA", null).getClass(),
            "factory should create a procedure entity");
        assertEquals(CEntityProcedure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityProcedure(1, "MAIN-PARA", null).getClass(),
            "factory should consistently create a procedure entity");
    }

    @Test
    void recursivelyRendersParagraphBodyAndReference()
    {
        assertDoesNotThrow(this::assertParagraphRendering,
            "paragraph body and reference should render recursively");
    }

    private void assertParagraphRendering()
    {
        CEntityProcedure procedure = new CEntityProcedure(
            1, "MAIN-PARA", catalog, null);
        procedure.AddChild(new CEntityContinue(1, catalog));

        assertEquals("MAIN_PARA", procedure.getFormattedName(),
            "paragraph name should use the Java-safe formatting");
        assertEquals(
            "Paragraph MAIN_PARA = new Paragraph(this);\n"
                + "public void MAIN_PARA() {\n"
                + "// CONTINUE \n"
                + "}",
            render(procedure), "paragraph reference should include its rendered body");
    }

    @Test
    void formatsNumericProcedureDeclarationAsLegalJava()
    {
        assertDoesNotThrow(this::assertNumericProcedureRendering,
            "numeric procedure names should render as legal Java identifiers");
    }

    private void assertNumericProcedureRendering()
    {
        CEntityProcedure procedure = new CEntityProcedure(
            1, "0000-CARDFILE-OPEN", catalog, null);

        assertEquals("$0000_CARDFILE_OPEN", procedure.getFormattedName(),
            "numeric procedure declaration should use the shared Java name");
        assertTrue(Character.isJavaIdentifierStart(procedure.getFormattedName().charAt(0)),
            "formatted procedure name should start with a legal Java identifier character");
        assertEquals(
            "Paragraph $0000_CARDFILE_OPEN = new Paragraph(this);\n"
                + "public void $0000_CARDFILE_OPEN() {\n"
                + "}",
            render(procedure), "numeric procedure should render declaration and method");
    }
}
