package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityNamedCondition;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityIsNamedCondition;
import utils.CObjectCatalog;

class CEntityIsNamedConditionRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityIsNamedCondition.class,
            new CJavaEntityFactory(catalog, null).NewEntityIsNamedCondition().getClass());
        assertEquals(CEntityIsNamedCondition.class,
            new CJavaEntityFactory(catalog, null).NewEntityIsNamedCondition().getClass());
    }

    @Test
    void oppositeRetainsTheNamedConditionReference()
    {
        CEntityNamedCondition named =
            new CEntityNamedCondition(1, "VALID-STATUS", catalog);
        CEntityIsNamedCondition condition = new CEntityIsNamedCondition();
        condition.SetCondition(named);
        assertEquals("is(VALID_STATUS)", render(condition));

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        assertEquals(CEntityIsNamedCondition.class, opposite.getClass());
        assertEquals("isNot(VALID_STATUS)", render(opposite));
    }

    private String render(CBaseEntityCondition condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }
}
