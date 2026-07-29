package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntityParseString;
import utils.CObjectCatalog;

class CEntityParseStringRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static MockDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    private static String render(CEntityParseString unstring)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(unstring, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityParseString.class,
            new CJavaEntityFactory(catalog, null).NewEntityParseString(1).getClass());
        assertEquals(CEntityParseString.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityParseString(1).getClass());
    }

    @Test
    void rendersTheCompleteUnstringFluentChain()
    {
        CEntityParseString unstring = new CEntityParseString(1, null);
        unstring.ParseString(ref("SOURCE"));
        unstring.AddDelimiterSingle(ref("DELIMITER"));
        unstring.AddDelimiterMulti(ref("ALL_DELIMITER"));
        unstring.setWithPointer(ref("POINTER"));
        unstring.setTallying(ref("TALLY"));
        unstring.AddDestination(new CDataEntity[] {ref("FIRST"), null, null});
        unstring.AddDestination(
            new CDataEntity[] {ref("SECOND"), ref("DELIMITER_IN"), ref("COUNT_IN")});

        assertEquals(
            "unstring(SOURCE).delimitedBy(DELIMITER).delimitedByAll(ALL_DELIMITER)"
                + ".withPointer(POINTER).tallying(TALLY).to(FIRST)"
                + ".to(SECOND, DELIMITER_IN, COUNT_IN) ;",
            render(unstring));
    }

    @Test
    void overflowChildrenRenderBehindTheFailedResult()
    {
        CEntityParseString unstring = new CEntityParseString(1, null);
        unstring.ParseString(ref("SOURCE"));
        unstring.AddDestination(new CDataEntity[] {ref("TARGET"), null, null});
        unstring.AddChild(new CEntityContinue(1, null));

        String rendered = render(unstring);
        assertTrue(rendered.startsWith("if (unstring(SOURCE).to(TARGET).failed())"));
        assertTrue(rendered.contains("// CONTINUE"));
    }
}
