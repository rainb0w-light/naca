package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityAssignWithAccessor;

class CEntityAssignWithAccessorRenderTest
{
    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityAssignWithAccessor.class,
            new CJavaEntityFactory(null, null).NewEntityAssignWithAccessor(1));
        assertInstanceOf(CEntityAssignWithAccessor.class,
            new CJavaEntityFactoryST(null, null).NewEntityAssignWithAccessor(1));
    }

    @Test
    void recursiveAssemblerRendersSemanticAccessorInvocation()
    {
        MockDataEntity destination = new MockDataEntity(1, "DESTINATION")
        {
            @Override
            public String ExportWriteAccessorTo(String value)
            {
                return "setField(" + value + ")";
            }
        };
        CEntityAssignWithAccessor assign = new CEntityAssignWithAccessor(1, null);
        assign.SetRefTo(destination);
        assign.SetValue(new MockDataEntity(1, "SOURCE"));

        assertEquals("setField(SOURCE)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());

        assign.SetFillAll(true);
        assertEquals("setFieldAll(SOURCE)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());
    }
}
