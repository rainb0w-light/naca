package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntitySetConstant;

class CEntitySetConstantRenderTest
{
    private static MockDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    private static String render(CEntitySetConstant set)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(set, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntitySetConstant.class,
            new CJavaEntityFactory(null, null).NewEntitySetConstant(1).getClass());
        assertEquals(CEntitySetConstant.class,
            new CJavaEntityFactoryST(null, null).NewEntitySetConstant(1).getClass());
    }

    @Test
    void rendersEveryWholeValueMode()
    {
        CEntitySetConstant set = new CEntitySetConstant(1, null);
        set.SetToSpace(ref("TARGET"));
        assertEquals("moveSpace(TARGET) ;", render(set));
        set.SetToZero(ref("TARGET"));
        assertEquals("moveZero(TARGET) ;", render(set));
        set.SetToLowValue(ref("TARGET"));
        assertEquals("moveLowValue(TARGET) ;", render(set));
        set.SetToHighValue(ref("TARGET"));
        assertEquals("moveHighValue(TARGET) ;", render(set));
        set.SetCondition(ref("CONDITION"), true);
        assertEquals("moveTrue(CONDITION) ;", render(set));
        set.SetCondition(ref("CONDITION"), false);
        assertEquals("moveFalse(CONDITION) ;", render(set));
        set.SetCsteValue(ref("TARGET"), ref("VALUE"));
        assertEquals("moveAll(VALUE, TARGET) ;", render(set));
    }

    @Test
    void rendersSubstringStartAndLength()
    {
        CEntitySetConstant set = new CEntitySetConstant(1, null);
        set.SetToZero(ref("TARGET"));
        CJavaEntityFactory factory = new CJavaEntityFactory(null, null);
        set.SetSubStringRef(
            factory.NewEntityExprTerminal(factory.NewEntityNumber("2")),
            factory.NewEntityExprTerminal(factory.NewEntityNumber("4")));

        assertEquals("moveSubStringZero(TARGET, 2, 4) ;", render(set));
    }
}
