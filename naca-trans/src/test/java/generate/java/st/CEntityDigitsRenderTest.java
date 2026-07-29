package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityDigits;
import utils.CObjectCatalog;

class CEntityDigitsRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        MockDataEntity field = new MockDataEntity(1, "FIELD");
        assertEquals(CEntityDigits.class,
            new CJavaEntityFactory(catalog, null).NewEntityDigits(field).getClass());
        assertEquals(CEntityDigits.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityDigits(field).getClass());
    }

    @Test
    void assemblerRecursivelyRendersTheOperand()
    {
        CEntityDigits digits = new CEntityDigits(
            catalog, new MockDataEntity(1, "FIELD"));
        assertEquals("digits(FIELD)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(digits, JavaTemplateRole.REFERENCE));
        assertEquals(false, digits.isValNeeded());
    }
}
