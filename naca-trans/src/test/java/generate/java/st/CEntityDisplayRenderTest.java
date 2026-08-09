package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityDisplay;

class CEntityDisplayRenderTest
{
    private static String render(CEntityDisplay display)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(display, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityDisplay.class,
            new CJavaEntityFactory(null, null)
                .NewEntityDisplay(1, CEntityDisplay.Upon.DEFAULT));
        assertInstanceOf(CEntityDisplay.class,
            new CJavaEntityFactory(null, null)
                .NewEntityDisplay(1, CEntityDisplay.Upon.DEFAULT));
    }

    @Test
    void rendersOneOrSeveralSemanticItems()
    {
        CEntityDisplay one = new CEntityDisplay(1, null, CEntityDisplay.Upon.DEFAULT);
        one.AddItemToDisplay(new MockDataEntity(1, "\"Hello World\""));
        assertEquals("display(\"Hello World\");", render(one));

        CEntityDisplay several = new CEntityDisplay(1, null, CEntityDisplay.Upon.DEFAULT);
        several.AddItemToDisplay(new MockDataEntity(1, "name"));
        several.AddItemToDisplay(new MockDataEntity(1, "age")
        {
            @Override
            public boolean isValNeeded()
            {
                return true;
            }
        });
        assertEquals("display(name + val(age));", render(several));
    }

    @Test
    void retainsUponTarget()
    {
        CEntityDisplay console = new CEntityDisplay(1, null, CEntityDisplay.Upon.CONSOLE);
        console.AddItemToDisplay(new MockDataEntity(1, "message"));
        assertEquals("console().display(message);", render(console));

        CEntityDisplay environment = new CEntityDisplay(
            1, null, CEntityDisplay.Upon.ENVINONMENT);
        environment.AddItemToDisplay(new MockDataEntity(1, "message"));
        assertEquals("displayEnv(message);", render(environment));
    }
}
