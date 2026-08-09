package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCurrentDate;
import utils.CObjectCatalog;

class CEntityCurrentDateRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCurrentDate.class,
            new CJavaEntityFactory(catalog, null).NewEntityCurrentDate().getClass());
        assertEquals(CEntityCurrentDate.class,
            new CJavaEntityFactory(catalog, null).NewEntityCurrentDate().getClass());
    }

    @Test
    void assemblerRendersTheRuntimeDateReference()
    {
        CEntityCurrentDate currentDate = new CEntityCurrentDate(catalog);
        assertEquals("currentDate()",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(currentDate, JavaTemplateRole.REFERENCE));
        assertEquals(false, currentDate.isValNeeded());
    }
}
