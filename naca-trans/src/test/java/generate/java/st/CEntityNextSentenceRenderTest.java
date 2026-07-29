package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityNextSentence;

class CEntityNextSentenceRenderTest
{
    private static String render(CEntityNextSentence nextSentence)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(nextSentence, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityNextSentence.class,
            new CJavaEntityFactory(null, null).NewEntityNextSentence(1).getClass());
        assertEquals(CEntityNextSentence.class,
            new CJavaEntityFactoryST(null, null).NewEntityNextSentence(1).getClass());
    }

    @Test
    void rendersParserHandledStatementAsComment()
    {
        assertInstanceOf(CEntityNextSentence.class, new CEntityNextSentence(1, null));
        assertEquals("// NEXT SENTENCE", render(new CEntityNextSentence(1, null)));
    }
}
