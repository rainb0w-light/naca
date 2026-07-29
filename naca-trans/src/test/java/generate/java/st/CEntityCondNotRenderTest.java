package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.java.expressions.CJavaExprTerminal;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondEquals;
import semantic.expression.CEntityCondNot;

class CEntityCondNotRenderTest
{
    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondNot.class,
            new CJavaEntityFactory(null, null).NewEntityCondNot().getClass());
        assertEquals(CEntityCondNot.class,
            new CJavaEntityFactoryST(null, null).NewEntityCondNot().getClass());
    }

    @Test
    void recursivelyRendersAndUnwrapsItsOperand()
    {
        CEntityCondEquals operand = new CEntityCondEquals();
        operand.SetEqualCondition(
            new CJavaExprTerminal(new MockDataEntity(1, "LEFT")),
            new CJavaExprTerminal(new MockDataEntity(1, "RIGHT")));

        CEntityCondNot condition = new CEntityCondNot();
        condition.SetCondition(operand);

        assertEquals("!(isEqual(LEFT, RIGHT))",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
        assertSame(operand, condition.GetOppositeCondition());
    }
}
