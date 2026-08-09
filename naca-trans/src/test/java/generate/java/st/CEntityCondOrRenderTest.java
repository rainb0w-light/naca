package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import semantic.expression.CEntityExprTerminal;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondAnd;
import semantic.expression.CEntityCondEquals;
import semantic.expression.CEntityCondOr;

class CEntityCondOrRenderTest
{
    private static CEntityCondEquals equals(String left, String right)
    {
        CEntityCondEquals condition = new CEntityCondEquals();
        condition.SetEqualCondition(
            new CEntityExprTerminal(new MockDataEntity(1, left)),
            new CEntityExprTerminal(new MockDataEntity(1, right)));
        return condition;
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondOr.class,
            new CJavaEntityFactory(null, null).NewEntityCondOr().getClass());
        assertEquals(CEntityCondOr.class,
            new CJavaEntityFactory(null, null).NewEntityCondOr().getClass());
    }

    @Test
    void recursivelyRendersOperandsAndBuildsSemanticOpposite()
    {
        CEntityCondOr condition = new CEntityCondOr();
        condition.SetCondition(equals("A", "B"), equals("C", "D"));

        assertEquals("isEqual(A, B) \n|| isEqual(C, D)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
        assertInstanceOf(CEntityCondAnd.class, condition.GetOppositeCondition());
    }
}
