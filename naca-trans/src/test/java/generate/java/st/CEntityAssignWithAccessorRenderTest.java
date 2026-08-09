package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityEnvironmentVariable;
import semantic.SQL.CEntitySQLCode;
import semantic.Verbs.CEntityAssignWithAccessor;
import utils.CObjectCatalog;

class CEntityAssignWithAccessorRenderTest
{
    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityAssignWithAccessor.class,
            new CJavaEntityFactory(null, null).NewEntityAssignWithAccessor(1));
        assertInstanceOf(CEntityAssignWithAccessor.class,
            new CJavaEntityFactory(null, null).NewEntityAssignWithAccessor(1));
    }

    @Test
    void recursiveAssemblerRendersEnvironmentAccessorFromSemanticState()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityEnvironmentVariable destination = new CEntityEnvironmentVariable(
            1, "DESTINATION", catalog, "readField()", "setField(", false);
        CEntityAssignWithAccessor assign = new CEntityAssignWithAccessor(1, null);
        assign.SetRefTo(destination);
        assign.SetValue(new MockDataEntity(1, "SOURCE"));

        assertEquals("setField(SOURCE);",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());

        assign.SetFillAll(true);
        assertEquals("setFieldAll(SOURCE);",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());
    }

    @Test
    void recursiveAssemblerRendersSQLCodeResetFromSemanticState()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityAssignWithAccessor assign = new CEntityAssignWithAccessor(1, null);
        assign.SetRefTo(new CEntitySQLCode("SQLCODE", catalog));
        assign.SetValue(new MockDataEntity(1, "SOURCE"));

        assertEquals("resetSQLCode(SOURCE);",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(assign, JavaTemplateRole.REFERENCE).trim());
    }
}
