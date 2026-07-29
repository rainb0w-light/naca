package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import parser.expression.CSumExpression;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprSum;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;

class CEntityExprSumRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityExprSum.class,
            new CJavaEntityFactory(catalog, null).NewEntityExprSum().getClass());
        assertEquals(CEntityExprSum.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityExprSum().getClass());
    }

    @Test
    void assemblerRendersAddAndSubtract()
    {
        assertEquals("add(2, \n3)", render(CSumExpression.CSumType.ADD));
        assertEquals("subtract(2, \n3)", render(CSumExpression.CSumType.SUB));
    }

    private String render(CSumExpression.CSumType type)
    {
        CEntityExprSum sum = new CEntityExprSum();
        sum.SetSumExpression(number("2"), number("3"), type);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(sum, JavaTemplateRole.REFERENCE);
    }

    private CBaseEntityExpression number(String value)
    {
        return new CEntityExprTerminal(new CEntityNumber(catalog, value)) {};
    }
}
