package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntityCondIsSQLCode;
import semantic.expression.CEntityCondAnd;
import semantic.expression.CEntityCondOr;

class CEntityCondAndRenderTest
{
    private static CEntityCondIsSQLCode sqlCode(int value)
    {
        CEntityCondIsSQLCode condition = new CEntityCondIsSQLCode();
        condition.setIsEqual(value);
        return condition;
    }

    private static String render(CEntityCondAnd condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondAnd.class,
            new CJavaEntityFactory(null, null).NewEntityCondAnd().getClass());
        assertEquals(CEntityCondAnd.class,
            new CJavaEntityFactoryST(null, null).NewEntityCondAnd().getClass());
    }

    @Test
    void recursivelyRendersBothOperandsAndBuildsSemanticOpposite()
    {
        CEntityCondAnd condition = new CEntityCondAnd();
        condition.SetCondition(
            sqlCode(0),
            sqlCode(42));

        assertEquals(
            "isSQLCode(SQLCode.SQL_OK) \n&& isSQLCode(42)",
            render(condition));
        assertInstanceOf(CEntityCondOr.class, condition.GetOppositeCondition());
    }

    @Test
    void ignoredOperandCollapsesWithoutDuplicatingTheOtherOperand()
    {
        CEntityCondIsSQLCode ignored = new CEntityCondIsSQLCode()
        {
            @Override
            public boolean ignore()
            {
                return true;
            }
        };
        ignored.setIsEqual(-1);
        CEntityCondAnd condition = new CEntityCondAnd();
        condition.SetCondition(ignored, sqlCode(42));

        assertEquals("isSQLCode(42)", render(condition));
    }
}
