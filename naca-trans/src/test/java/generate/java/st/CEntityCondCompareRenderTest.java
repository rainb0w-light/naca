package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import semantic.expression.CEntityExprTerminal;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondCompare;

class CEntityCondCompareRenderTest
{
    private static CEntityExprTerminal expression(String value)
    {
        return new CEntityExprTerminal(new MockDataEntity(1, value));
    }

    private static String render(CEntityCondCompare condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondCompare.class,
            new CJavaEntityFactory(null, null).NewEntityCondCompare().getClass());
        assertEquals(CEntityCondCompare.class,
            new CJavaEntityFactory(null, null).NewEntityCondCompare().getClass());
    }

    @Test
    void rendersAllOrderedComparisons()
    {
        CEntityCondCompare condition = new CEntityCondCompare();
        condition.SetLessThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isLess(LEFT, RIGHT)", render(condition));

        condition.SetLessOrEqualThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isLessOrEqual(LEFT, RIGHT)", render(condition));

        condition.SetGreaterThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isGreater(LEFT, RIGHT)", render(condition));

        condition.SetGreaterOrEqualsThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isGreaterOrEqual(LEFT, RIGHT)", render(condition));
    }

    @Test
    void oppositeIsAnotherPureSemanticComparison()
    {
        CEntityCondCompare condition = new CEntityCondCompare();
        condition.SetLessThan(expression("LEFT"), expression("RIGHT"));

        CEntityCondCompare opposite = assertInstanceOf(
            CEntityCondCompare.class, condition.GetOppositeCondition());
        assertEquals("isGreaterOrEqual(LEFT, RIGHT)", render(opposite));
    }
}
