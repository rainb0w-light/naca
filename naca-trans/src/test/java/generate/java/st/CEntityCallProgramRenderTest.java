package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityCallProgram;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;

class CEntityCallProgramRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityCallProgram call)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(call, JavaTemplateRole.REFERENCE).trim();
    }

    private CEntityString programLiteral(String value)
    {
        return new CEntityString(catalog, value.toCharArray());
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityCallProgram.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityCallProgram(1, programLiteral("SUBPROG")));
        assertInstanceOf(CEntityCallProgram.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityCallProgram(1, programLiteral("SUBPROG")));
    }

    @Test
    void uncheckedLiteralCallRetainsDynamicProgramName()
    {
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, programLiteral("SUBPROG"));
        call.SetParameterByRef(new MockDataEntity(1, "ARG"));

        assertEquals("call(\"Subprog\").using(ARG).executeCall();", render(call));
    }

    @Test
    void checkedLiteralCallUsesProgramClass()
    {
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, programLiteral("CALLMSG"));
        call.setChecked(true);

        assertEquals("call(Callmsg.class).executeCall();", render(call));
    }

    @Test
    void dynamicCallRendersItsSemanticReference()
    {
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, new MockDataEntity(1, "PROGRAM_NAME"));

        assertEquals("call(PROGRAM_NAME).executeCall();", render(call));
    }
}
