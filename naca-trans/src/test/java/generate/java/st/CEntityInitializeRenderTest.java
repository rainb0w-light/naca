package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityInitialize;

class CEntityInitializeRenderTest
{
    private static String render(CEntityInitialize initialize)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(initialize, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void stFactoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityInitialize.class,
            new CJavaEntityFactoryST(null, null)
                .NewEntityInitialize(1, new MockDataEntity(1, "TARGET")));
    }

    @Test
    void rendersDefaultAndReplacingModes()
    {
        CEntityInitialize plain = new CEntityInitialize(
            1, null, new MockDataEntity(1, "TARGET"));
        assertEquals("initialize(TARGET) ;", render(plain));

        CEntityInitialize alpha = new CEntityInitialize(
            1, null, new MockDataEntity(1, "TARGET"));
        alpha.ReplaceAlphaNumWith(new MockDataEntity(1, "VALUE"));
        assertEquals("initializeReplacingAlphaNum(TARGET, VALUE) ;", render(alpha));

        CEntityInitialize numeric = new CEntityInitialize(
            1, null, new MockDataEntity(1, "TARGET"));
        numeric.ReplaceNumWith(new MockDataEntity(1, "VALUE"));
        assertEquals("initializeReplacingNum(TARGET, VALUE) ;", render(numeric));

        CEntityInitialize edited = new CEntityInitialize(
            1, null, new MockDataEntity(1, "TARGET"));
        edited.ReplaceNumEditedWith(new MockDataEntity(1, "VALUE"));
        assertEquals("initializeReplacingNumEdited(TARGET, VALUE) ;", render(edited));
    }

    @Test
    void sqlCodeUsesDedicatedResetOperation()
    {
        CEntityInitialize initialize = new CEntityInitialize(
            1, null, new MockDataEntity(1, "getSQLCode()"));

        assertEquals("resetSQLCode(0);", render(initialize));
    }
}
