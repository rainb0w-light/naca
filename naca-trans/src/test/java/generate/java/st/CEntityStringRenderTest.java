package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;

class CEntityStringRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityString.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityString("VALUE".toCharArray()).getClass());
        assertEquals(CEntityString.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityString("VALUE".toCharArray()).getClass());
    }

    @Test
    void assemblerEscapesJavaStringLiterals()
    {
        assertEquals("\"a\\\"b\\\\c\\n\\t\\u0080\"",
            TemplateLoader.getRecursiveAssembler().renderRoot(
                new CEntityString(catalog, "a\"b\\c\n\t\u0080".toCharArray()),
                JavaTemplateRole.REFERENCE));
    }
}
