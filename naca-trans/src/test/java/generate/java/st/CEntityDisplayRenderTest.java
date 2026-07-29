package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
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
    void stFactoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityDisplay.class,
            new CJavaEntityFactoryST(null, null)
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
        several.AddItemToDisplay(new MockDataEntity(1, "age"));
        assertEquals("display(name + age);", render(several));
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
