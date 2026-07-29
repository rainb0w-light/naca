package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityExprOpposite;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;

class CEntityExprOppositeRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityExprOpposite.class,
            new CJavaEntityFactory(catalog, null).NewEntityExprOpposite().getClass());
        assertEquals(CEntityExprOpposite.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityExprOpposite().getClass());
    }

    @Test
    void assemblerRecursivelyRendersTheOperand()
    {
        CEntityExprOpposite opposite = new CEntityExprOpposite();
        opposite.SetOpposite(
            new CEntityExprTerminal(new CEntityNumber(catalog, "12")) {});
        assertEquals("opposite(12)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(opposite, JavaTemplateRole.REFERENCE));
    }
}
