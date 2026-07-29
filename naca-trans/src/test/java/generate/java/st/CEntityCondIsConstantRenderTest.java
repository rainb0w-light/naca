package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondIsConstant;

class CEntityCondIsConstantRenderTest
{
    private static String render(CEntityCondIsConstant condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondIsConstant.class,
            new CJavaEntityFactory(null, null).NewEntityCondIsConstant().getClass());
        assertEquals(CEntityCondIsConstant.class,
            new CJavaEntityFactoryST(null, null).NewEntityCondIsConstant().getClass());
    }

    @Test
    void oppositeRetainsTheFigurativeConstant()
    {
        CEntityCondIsConstant condition = new CEntityCondIsConstant();
        condition.SetIsZero(new MockDataEntity(1, "VALUE"));
        assertEquals("isZero(VALUE)", render(condition));

        CEntityCondIsConstant opposite = assertInstanceOf(
            CEntityCondIsConstant.class, condition.GetOppositeCondition());
        assertEquals("isNotZero(VALUE)", render(opposite));
    }
}
