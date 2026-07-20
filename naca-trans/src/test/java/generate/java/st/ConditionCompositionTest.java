package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertSame;

import generate.java.expressions.CJavaCondAnd;
import generate.java.expressions.CJavaCondOr;
import org.junit.jupiter.api.Test;

class ConditionCompositionTest
{
    @Test
    void andRetainsBothSemanticOperands()
    {
        MockCondition left = new MockCondition(1, "left");
        MockCondition right = new MockCondition(1, "right");
        CJavaCondAnd condition = new CJavaCondAnd();

        condition.SetCondition(left, right);

        assertSame(left, condition.getLeft());
        assertSame(right, condition.getRight());
    }

    @Test
    void orRetainsBothSemanticOperands()
    {
        MockCondition left = new MockCondition(1, "left");
        MockCondition right = new MockCondition(1, "right");
        CJavaCondOr condition = new CJavaCondOr();

        condition.SetCondition(left, right);

        assertSame(left, condition.getLeft());
        assertSame(right, condition.getRight());
    }
}
