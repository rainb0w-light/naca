package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;

class CEntityNumberRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityNumber.class,
            new CJavaEntityFactory(catalog, null).NewEntityNumber("42").getClass());
        assertEquals(CEntityNumber.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityNumber("42").getClass());
    }

    @Test
    void assemblerPreservesLegacyLiteralNormalization()
    {
        assertEquals("42", render("0042"));
        assertEquals("2147483648L", render("2147483648"));
        assertEquals("\"12.50\"", render("12.50"));
        assertEquals("true", render("true"));
    }

    private String render(String value)
    {
        return TemplateLoader.getRecursiveAssembler().renderRoot(
            new CEntityNumber(catalog, value), JavaTemplateRole.REFERENCE);
    }
}
