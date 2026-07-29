package parser.condition;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.expression.CDefaultConditionManager;
import parser.expression.CExpression;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;

class CCondIsAllTest
{
    @Test
    void constructorKeepsBothOperands() throws Exception
    {
        CExpression left = new TestExpression(1);
        CExpression right = new TestExpression(1);

        CCondIsAll condition = new CCondIsAll(1, left, right);

        assertSame(left, condition.GetFirstConditionOperand());
        Field secondOperand = CCondIsAll.class.getDeclaredField("term2");
        secondOperand.setAccessible(true);
        assertSame(right, secondOperand.get(condition));
    }

    private static class TestExpression extends CExpression
    {
        TestExpression(int line)
        {
            super(line);
        }

        @Override
        public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
        {
            return null;
        }

        @Override
        public CBaseEntityCondition AnalyseCondition(
            CBaseEntityFactory factory, CDefaultConditionManager masterCond)
        {
            return null;
        }

        @Override
        public Element DoExport(Document root)
        {
            return null;
        }

        @Override
        protected boolean CheckMembersBeforeExport()
        {
            return true;
        }

        @Override
        public CExpression GetFirstConditionOperand()
        {
            return this;
        }

        @Override
        public CExpression GetSimilarExpression(CExpression operand)
        {
            return this;
        }

        @Override
        public boolean IsBinaryCondition()
        {
            return false;
        }

        @Override
        public CExpression GetFirstCalculOperand()
        {
            return this;
        }
    }
}
