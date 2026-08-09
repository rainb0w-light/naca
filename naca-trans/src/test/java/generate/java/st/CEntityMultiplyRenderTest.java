package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityMultiply;

class CEntityMultiplyRenderTest
{
    private static String render(CEntityMultiply multiply)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(multiply, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityMultiply.class,
            new CJavaEntityFactory(null, null).NewEntityMultiply(1));
        assertInstanceOf(CEntityMultiply.class,
            new CJavaEntityFactory(null, null).NewEntityMultiply(1));
    }

    @Test
    void rendersNormalAndRoundedTargets()
    {
        CEntityMultiply normal = new CEntityMultiply(1, null);
        normal.SetMultiply(new MockDataEntity(1, "VALUE"),
            new MockDataEntity(1, "BY"), new MockDataEntity(1, "RESULT"), false);
        assertEquals("multiply(VALUE, BY).to(RESULT) ;", render(normal));

        CEntityMultiply rounded = new CEntityMultiply(1, null);
        rounded.SetMultiply(new MockDataEntity(1, "VALUE"),
            new MockDataEntity(1, "BY"), new MockDataEntity(1, "RESULT"), true);
        assertEquals("multiply(VALUE, BY).toRounded(RESULT) ;", render(rounded));
    }
}
