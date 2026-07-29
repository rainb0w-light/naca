package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityCallProgram;
import utils.CObjectCatalog;

class CEntityCallProgramRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityCallProgram call)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(call, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityCallProgram.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityCallProgram(1, new MockDataEntity(1, "\"SUBPROG\"")));
        assertInstanceOf(CEntityCallProgram.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityCallProgram(1, new MockDataEntity(1, "\"SUBPROG\"")));
    }

    @Test
    void uncheckedLiteralCallRetainsDynamicProgramName()
    {
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, new MockDataEntity(1, "\"SUBPROG\""));
        call.SetParameterByRef(new MockDataEntity(1, "ARG"));

        assertEquals("call(\"Subprog\").using(ARG).executeCall();", render(call));
    }

    @Test
    void checkedLiteralCallUsesProgramClass()
    {
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, new MockDataEntity(1, "\"CALLMSG\""));
        call.setChecked(true);

        assertEquals("call(Callmsg.class).executeCall();", render(call));
    }
}
