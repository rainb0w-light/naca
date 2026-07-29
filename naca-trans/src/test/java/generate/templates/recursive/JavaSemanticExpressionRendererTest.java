package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.CJavaExporter;
import generate.java.expressions.CJavaExprSum;
import generate.java.expressions.CJavaExprTerminal;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import parser.expression.CProdExpression;
import parser.expression.CSumExpression;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

class JavaSemanticExpressionRendererTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaExporter output = new CJavaExporter(null, "/tmp/unused.java", null, false);
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersEveryArithmeticOperatorLikeTheDirectGenerator()
    {
        assertMatchesDirect(sum("1", "2", CSumExpression.CSumType.ADD));
        assertMatchesDirect(sum("3", "4", CSumExpression.CSumType.SUB));
        assertMatchesDirect(product("5", "6", CProdExpression.CProdType.PROD));
        assertMatchesDirect(product("7", "8", CProdExpression.CProdType.DIVIDE));
        assertMatchesDirect(product("9", "2", CProdExpression.CProdType.POW));

        LegacyOppositeFixture opposite = new LegacyOppositeFixture();
        opposite.SetOpposite(numberExpression("10"));
        assertMatchesDirect(opposite);
    }

    @Test
    void recursivelyComposesNestedArithmeticWithoutIntermediateStrings()
    {
        CJavaExprSum sum = sum("1", "2", CSumExpression.CSumType.ADD);
        LegacyOppositeFixture opposite = new LegacyOppositeFixture();
        opposite.SetOpposite(numberExpression("3"));
        LegacyProductFixture product = new LegacyProductFixture();
        product.SetProdExpression(sum, opposite, CProdExpression.CProdType.PROD);

        assertMatchesDirect(product);
    }

    private CJavaExprSum sum(String left, String right, CSumExpression.CSumType type)
    {
        CJavaExprSum expression = new CJavaExprSum();
        expression.SetSumExpression(numberExpression(left), numberExpression(right), type);
        return expression;
    }

    private LegacyProductFixture product(String left, String right, CProdExpression.CProdType type)
    {
        LegacyProductFixture expression = new LegacyProductFixture();
        expression.SetProdExpression(numberExpression(left), numberExpression(right), type);
        return expression;
    }

    private CBaseEntityExpression numberExpression(String value)
    {
        return new CJavaExprTerminal(new LegacyNumberFixture(catalog, value));
    }

    private void assertMatchesDirect(CBaseEntityExpression expression)
    {
        assertEquals(expression.Export(), assembler.renderRoot(expression, JavaTemplateRole.REFERENCE),
            expression.getClass().getName());
    }
}
