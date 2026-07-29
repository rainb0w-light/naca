package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityReturn;
import utils.CObjectCatalog;

class CEntityReturnRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityReturn entity)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityReturn.class,
            new CJavaEntityFactory(catalog, null).NewEntityReturn(1).getClass());
        assertEquals(CEntityReturn.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityReturn(1).getClass());
    }

    @Test
    void rendersGoBackParagraphReturnAndStopRun()
    {
        assertEquals("exitProgram();", render(new CEntityReturn(1, null)));

        CEntityReturn paragraphReturn = new CEntityReturn(1, null);
        paragraphReturn.SetOnlyReturnFromProcedure();
        assertEquals("return;", render(paragraphReturn));

        CEntityReturn stopRun = new CEntityReturn(1, null);
        stopRun.SetStopProgram(12);
        assertEquals("stopRun(12);", render(stopRun));
    }
}
