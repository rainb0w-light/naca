package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityCallFunction;
import utils.CObjectCatalog;

class CEntityCallFunctionRenderTest
{
    private static final String TARGET = "TARGET";
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityCallFunction perform)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(perform, JavaTemplateRole.REFERENCE).trim();
    }

    private void paragraph(String name)
    {
        new CEntityProcedure(1, name, catalog, null);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertDoesNotThrow(this::assertFactoriesReturnSemanticEntities,
            "both call-function factories should return semantic entities");
    }

    private void assertFactoriesReturnSemanticEntities()
    {
        paragraph(TARGET);
        assertInstanceOf(CEntityCallFunction.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityCallFunction(1, TARGET, "", null),
            "factory should create a call-function entity");
        assertInstanceOf(CEntityCallFunction.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityCallFunction(1, TARGET, "", null),
            "factory should consistently create a call-function entity");
    }

    @Test
    void rendersSimpleAndThroughForms()
    {
        assertDoesNotThrow(this::assertSimpleAndThroughForms,
            "simple and through performs should render");
    }

    private void assertSimpleAndThroughForms()
    {
        paragraph("START-PARA");
        paragraph("END-PARA");

        CEntityCallFunction simple = new CEntityCallFunction(
            1, catalog, "START-PARA", "", null);
        assertEquals("perform(START_PARA) ;", render(simple),
            "simple perform should use the formatted paragraph name");

        CEntityCallFunction through = new CEntityCallFunction(
            1, catalog, "START-PARA", "END-PARA", null);
        assertEquals("performThrough(START_PARA, END_PARA) ;", render(through),
            "through perform should render both formatted paragraph names");
    }

    @Test
    void formatsNumericSimpleAndThroughReferencesLikeDeclarations()
    {
        assertDoesNotThrow(this::assertNumericReferences,
            "numeric paragraph references should match declaration formatting");
    }

    private void assertNumericReferences()
    {
        paragraph("0000-CARDFILE-OPEN");
        paragraph("0001-CARDFILE-CLOSE");
        CEntityCallFunction simple = new CEntityCallFunction(
            1, catalog, "0000-CARDFILE-OPEN", "", null);
        CEntityCallFunction through = new CEntityCallFunction(
            1, catalog, "0000-CARDFILE-OPEN", "0001-CARDFILE-CLOSE", null);

        assertEquals("perform($0000_CARDFILE_OPEN) ;", render(simple),
            "numeric simple perform should be a legal Java reference");
        assertEquals(
            "performThrough($0000_CARDFILE_OPEN, $0001_CARDFILE_CLOSE) ;",
            render(through), "numeric through perform should format both references");
    }

    @Test
    void rendersTimesLoopAroundPerform()
    {
        assertDoesNotThrow(this::assertTimesLoopRendering,
            "repeated perform should render its loop and body");
    }

    private void assertTimesLoopRendering()
    {
        paragraph(TARGET);
        CEntityCallFunction perform = new CEntityCallFunction(
            1, catalog, TARGET, "", null);
        perform.SetRepetitions(new MockDataEntity(1, "TIMES"));

        assertEquals(
            "for (int loop_index=0; isLess(loop_index, TIMES); loop_index++) {\n"
                + "perform(TARGET) ;\n"
                + "}",
            render(perform), "repeated perform should wrap the formatted call");
    }
}
