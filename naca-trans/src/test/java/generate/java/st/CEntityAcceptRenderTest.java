package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityAccept;
import semantic.Verbs.CEntityAccept.AcceptMode;

class CEntityAcceptRenderTest
{
    private static String render(CEntityAccept accept)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(accept, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityAccept.class,
            new CJavaEntityFactory(null, null).NewEntityAccept(1));
        assertInstanceOf(CEntityAccept.class,
            new CJavaEntityFactory(null, null).NewEntityAccept(1));
    }

    @Test
    void inputUsesExistingRuntimeAcceptOperation()
    {
        CEntityAccept accept = new CEntityAccept(1, null);
        accept.AcceptFrom(AcceptMode.FROM_INPUT, new MockDataEntity(1, "TARGET"));

        assertEquals("accept(TARGET);", render(accept));
    }

    @Test
    void rendersEnvironmentDateAndVariableSources()
    {
        CEntityAccept environment = new CEntityAccept(1, null);
        environment.AcceptFrom(
            AcceptMode.FROM_ENVIRONMENT_VALUE,
            new MockDataEntity(1, "TARGET"));
        assertEquals("acceptEnv(TARGET);", render(environment));

        CEntityAccept date = new CEntityAccept(1, null);
        date.AcceptFrom(AcceptMode.FROM_DATE, new MockDataEntity(1, "TARGET"));
        assertEquals("move(getDateBatch(), TARGET);", render(date));

        CEntityAccept variable = new CEntityAccept(1, null);
        variable.AcceptFromVariable(
            new MockDataEntity(1, "TARGET"),
            new MockDataEntity(1, "SOURCE"));
        assertEquals("move(SOURCE, TARGET);", render(variable));
    }
}
