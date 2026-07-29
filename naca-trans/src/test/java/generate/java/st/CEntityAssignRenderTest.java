package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityAssign;

class CEntityAssignRenderTest
{
    private static String render(CEntityAssign assign)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void rendersMoveFromPureSemanticState()
    {
        CEntityAssign assign = new CEntityAssign(1, null);
        assign.SetValue(new MockDataEntity(2, "SOURCE"));
        assign.AddRefTo(new MockDataEntity(3, "DEST-A"));
        assign.AddRefTo(new MockDataEntity(4, "DEST-B"));

        assertEquals("move(SOURCE, DEST-A);\nmove(SOURCE, DEST-B);",
            render(assign));
    }

    @Test
    void rendersMoveAllAndCorrespondingModes()
    {
        CEntityAssign all = new CEntityAssign(1, null);
        all.SetValue(new MockDataEntity(2, "SOURCE"));
        all.AddRefTo(new MockDataEntity(3, "DEST"));
        all.SetFillAll(true);
        assertEquals("moveAll(SOURCE, DEST);", render(all));

        CEntityAssign corresponding = new CEntityAssign(1, null);
        corresponding.SetValue(new MockDataEntity(2, "SOURCE"));
        corresponding.AddRefTo(new MockDataEntity(3, "DEST"));
        corresponding.SetAssignCorresponding(true);
        assertEquals("moveCorresponding(SOURCE, DEST);", render(corresponding));
    }

    @Test
    void stFactoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityAssign.class,
            new CJavaEntityFactoryST(null, null).NewEntityAssign(1));
    }
}
