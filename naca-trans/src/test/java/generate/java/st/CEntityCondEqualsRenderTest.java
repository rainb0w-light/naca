package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import semantic.expression.CEntityExprTerminal;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondEquals;

class CEntityCondEqualsRenderTest
{
    private static CEntityExprTerminal expression(String value)
    {
        return new CEntityExprTerminal(new MockDataEntity(1, value));
    }

    private static String render(CEntityCondEquals condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondEquals.class,
            new CJavaEntityFactory(null, null).NewEntityCondEquals().getClass());
        assertEquals(CEntityCondEquals.class,
            new CJavaEntityFactory(null, null).NewEntityCondEquals().getClass());
    }

    @Test
    void rendersEqualDifferentAndOpposite()
    {
        CEntityCondEquals condition = new CEntityCondEquals();
        condition.SetEqualCondition(expression("LEFT"), expression("RIGHT"));
        assertEquals("isEqual(LEFT, RIGHT)", render(condition));

        CEntityCondEquals opposite = assertInstanceOf(
            CEntityCondEquals.class, condition.GetOppositeCondition());
        assertEquals("isDifferent(LEFT, RIGHT)", render(opposite));

        condition.SetDifferentCondition(expression("LEFT"), expression("RIGHT"));
        assertEquals("isDifferent(LEFT, RIGHT)", render(condition));
    }
}
