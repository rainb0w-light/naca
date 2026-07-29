package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityExprTerminal;
import utils.CObjectCatalog;

class CEntityExprTerminalRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        MockDataEntity field = new MockDataEntity(1, "FIELD");
        assertEquals(CEntityExprTerminal.class,
            new CJavaEntityFactory(catalog, null).NewEntityExprTerminal(field).getClass());
        assertEquals(CEntityExprTerminal.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityExprTerminal(field).getClass());
    }

    @Test
    void assemblerRecursivelyRendersTheTerm()
    {
        assertEquals("FIELD",
            TemplateLoader.getRecursiveAssembler().renderRoot(
                new CEntityExprTerminal(new MockDataEntity(1, "FIELD")),
                JavaTemplateRole.REFERENCE));
    }
}
