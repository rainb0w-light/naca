package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.java.expressions.CJavaExprTerminal;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondIsAll;

class CEntityCondIsAllRenderTest
{
    private static CJavaExprTerminal expression(String value)
    {
        return new CJavaExprTerminal(new MockDataEntity(1, value));
    }

    private static String render(CEntityCondIsAll condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondIsAll.class,
            new CJavaEntityFactory(null, null).NewEntityCondIsAll().getClass());
        assertEquals(CEntityCondIsAll.class,
            new CJavaEntityFactoryST(null, null).NewEntityCondIsAll().getClass());
    }

    @Test
    void rendersAllAndItsSemanticOpposite()
    {
        CEntityCondIsAll condition = new CEntityCondIsAll();
        condition.SetCondition(expression("VALUE"), expression("\"X\""));
        assertEquals("isAll(VALUE, \"X\")", render(condition));

        CEntityCondIsAll opposite = assertInstanceOf(
            CEntityCondIsAll.class, condition.GetOppositeCondition());
        assertEquals("isNotAll(VALUE, \"X\")", render(opposite));

        condition.setOpposite();
        assertEquals("isNotAll(VALUE, \"X\")", render(condition));
    }
}
