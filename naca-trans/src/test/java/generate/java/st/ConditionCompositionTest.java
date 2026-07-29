package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondAnd;
import semantic.expression.CEntityCondOr;

class ConditionCompositionTest
{
    @Test
    void andRetainsBothSemanticOperands()
    {
        MockCondition left = new MockCondition(1, "left");
        MockCondition right = new MockCondition(1, "right");
        CEntityCondAnd condition = new CEntityCondAnd();

        condition.SetCondition(left, right);

        assertSame(left, condition.getLeft());
        assertSame(right, condition.getRight());
    }

    @Test
    void orRetainsBothSemanticOperands()
    {
        MockCondition left = new MockCondition(1, "left");
        MockCondition right = new MockCondition(1, "right");
        CEntityCondOr condition = new CEntityCondOr();

        condition.SetCondition(left, right);

        assertSame(left, condition.getLeft());
        assertSame(right, condition.getRight());
    }
}
