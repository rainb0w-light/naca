package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityCallFunction;
import utils.CObjectCatalog;

class CEntityCallFunctionRenderTest
{
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
        paragraph("TARGET");
        assertInstanceOf(CEntityCallFunction.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityCallFunction(1, "TARGET", "", null));
        assertInstanceOf(CEntityCallFunction.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityCallFunction(1, "TARGET", "", null));
    }

    @Test
    void rendersSimpleAndThroughForms()
    {
        paragraph("START-PARA");
        paragraph("END-PARA");

        CEntityCallFunction simple = new CEntityCallFunction(
            1, catalog, "START-PARA", "", null);
        assertEquals("perform(START_PARA) ;", render(simple));

        CEntityCallFunction through = new CEntityCallFunction(
            1, catalog, "START-PARA", "END-PARA", null);
        assertEquals("performThrough(START_PARA, END_PARA) ;", render(through));
    }

    @Test
    void rendersTimesLoopAroundPerform()
    {
        paragraph("TARGET");
        CEntityCallFunction perform = new CEntityCallFunction(
            1, catalog, "TARGET", "", null);
        perform.SetRepetitions(new MockDataEntity(1, "TIMES"));

        assertEquals(
            "for (int loop_index=0; isLess(loop_index, TIMES); loop_index++) {\n"
                + "perform(TARGET) ;\n"
                + "}",
            render(perform));
    }
}
