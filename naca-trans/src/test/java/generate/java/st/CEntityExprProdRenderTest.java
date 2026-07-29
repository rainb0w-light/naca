package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import parser.expression.CProdExpression;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprProd;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;

class CEntityExprProdRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityExprProd.class,
            new CJavaEntityFactory(catalog, null).NewEntityExprProd().getClass());
        assertEquals(CEntityExprProd.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityExprProd().getClass());
    }

    @Test
    void assemblerRendersAllProductOperators()
    {
        assertEquals("multiply(2, \n3)", render(CProdExpression.CProdType.PROD));
        assertEquals("divide(2, \n3)", render(CProdExpression.CProdType.DIVIDE));
        assertEquals("pow(2, \n3)", render(CProdExpression.CProdType.POW));
    }

    private String render(CProdExpression.CProdType type)
    {
        CEntityExprProd product = new CEntityExprProd();
        product.SetProdExpression(number("2"), number("3"), type);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(product, JavaTemplateRole.REFERENCE);
    }

    private CBaseEntityExpression number(String value)
    {
        return new CEntityExprTerminal(new CEntityNumber(catalog, value)) {};
    }
}
