package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityAssignWithAccessor;

class CEntityAssignWithAccessorRenderTest
{
    @Test
    void stFactoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityAssignWithAccessor.class,
            new CJavaEntityFactoryST(null, null).NewEntityAssignWithAccessor(1));
    }

    @Test
    void recursiveAssemblerRendersReadyAccessorInvocation()
    {
        CEntityAssignWithAccessor assign = new CEntityAssignWithAccessor(1, null)
        {
            @Override
            public String getAccessorInvocation()
            {
                return "setField(SOURCE)";
            }
        };

        assertEquals("setField(SOURCE)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());
    }
}
