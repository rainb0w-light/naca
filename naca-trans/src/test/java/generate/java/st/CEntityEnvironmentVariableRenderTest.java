package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityEnvironmentVariable;
import semantic.Verbs.CEntityAssignWithAccessor;
import utils.CObjectCatalog;

class CEntityEnvironmentVariableRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityEnvironmentVariable.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityEnvironmentVariable("RETURN-CODE", "getReturnCode()",
                    "setReturnCode(", true).getClass());
        assertEquals(CEntityEnvironmentVariable.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityEnvironmentVariable("RETURN-CODE", "getReturnCode()",
                    "setReturnCode(", true).getClass());
    }

    @Test
    void assemblerRendersReadsAndWritesWithoutSemanticStringGeneration()
    {
        CEntityEnvironmentVariable variable = new CEntityEnvironmentVariable(
            1, "RETURN-CODE", catalog, "getReturnCode()", "setReturnCode(", true);
        assertEquals("getReturnCode()", TemplateLoader.getRecursiveAssembler()
            .renderRoot(variable, JavaTemplateRole.REFERENCE));

        CEntityAssignWithAccessor assign = new CEntityAssignWithAccessor(1, catalog);
        assign.SetRefTo(variable);
        assign.SetValue(new MockDataEntity(1, "SOURCE"));
        assertEquals("setReturnCode(SOURCE);", TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());

        assign.SetFillAll(true);
        assertEquals("setReturnCodeAll(SOURCE);", TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());
    }
}
