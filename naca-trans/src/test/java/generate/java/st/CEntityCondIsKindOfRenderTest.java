package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityCondIsKindOf;

class CEntityCondIsKindOfRenderTest
{
    private static String render(CEntityCondIsKindOf condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityCondIsKindOf.class,
            new CJavaEntityFactory(null, null).NewEntityCondIsKindOf().getClass());
        assertEquals(CEntityCondIsKindOf.class,
            new CJavaEntityFactoryST(null, null).NewEntityCondIsKindOf().getClass());
    }

    @Test
    void rendersNumericAlphabeticAndCaseSpecificConditions()
    {
        MockDataEntity value = new MockDataEntity(1, "VALUE");

        CEntityCondIsKindOf numeric = new CEntityCondIsKindOf();
        numeric.SetIsNumeric(value);
        assertEquals("isNumeric(VALUE)", render(numeric));
        CEntityCondIsKindOf notNumeric = assertInstanceOf(
            CEntityCondIsKindOf.class, numeric.GetOppositeCondition());
        assertEquals("isNotNumeric(VALUE)", render(notNumeric));

        CEntityCondIsKindOf alphabetic = new CEntityCondIsKindOf();
        alphabetic.SetIsAlphabetic(value);
        assertEquals("isAlphabetic(VALUE)", render(alphabetic));

        CEntityCondIsKindOf lower = new CEntityCondIsKindOf();
        lower.SetIsLower(value);
        assertEquals("isAlphabeticLower(VALUE)", render(lower));

        CEntityCondIsKindOf upper = new CEntityCondIsKindOf();
        upper.SetIsUpper(value);
        upper.setOpposite();
        assertEquals("isNotAlphabeticUpper(VALUE)", render(upper));
    }
}
