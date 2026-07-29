package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityGoto;
import utils.CObjectCatalog;

class CEntityGotoRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityGoto goTo)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(goTo, JavaTemplateRole.REFERENCE).trim();
    }

    private void paragraph(String name)
    {
        new CEntityProcedure(1, name, catalog, null);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        paragraph("TARGET");
        assertInstanceOf(CEntityGoto.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityGoto(1, "TARGET", null));
        assertInstanceOf(CEntityGoto.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityGoto(1, "TARGET", null));
    }

    @Test
    void rendersResolvedProcedureTarget()
    {
        paragraph("TARGET-PARA");
        CEntityGoto goTo = new CEntityGoto(1, catalog, "TARGET-PARA", null);

        assertEquals("goTo(TARGET_PARA) ;", render(goTo));
    }
}
