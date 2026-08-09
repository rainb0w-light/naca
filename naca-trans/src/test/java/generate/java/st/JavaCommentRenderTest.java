package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityComment;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Validates the comment REFERENCE template ({@code javaComment}). The historical quirks
 * (newline escaping gated on {@code indexOf > 0}, the {@code "Ox000D"}
 * capital-O carriage-return escape, right trim) must be reproduced faithfully.
 */
class JavaCommentRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private String render(CEntityComment comment)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(comment, JavaTemplateRole.REFERENCE).strip();
    }

    @Test
    void plainCommentMatchesDirectExportReference()
    {
        CEntityComment comment = new CEntityComment(1, catalog(), "HELLO WORLD");
        assertEquals("// HELLO WORLD", render(comment));
    }

    @Test
    void trailingWhitespaceIsRightTrimmed()
    {
        CEntityComment comment = new CEntityComment(1, catalog(), "HELLO   ");
        assertEquals("// HELLO", render(comment));
    }

    @Test
    void carriageReturnIsEscapedWithTheHistoricalCapitalOQuirk()
    {
        // \r at index 1 (> 0) triggers escaping; \r maps to "Ox000D" (capital O).
        CEntityComment comment = new CEntityComment(1, catalog(), "A\rB");
        assertTrue(render(comment).contains("AOx000DB"), render(comment));
    }

    @Test
    void newlineAtIndexZeroIsNotEscapedPreservingTheIndexOfGate()
    {
        // \n at index 0 does NOT satisfy indexOf > 0, so it is left unescaped.
        CEntityComment comment = new CEntityComment(1, catalog(), "\nLEADING");
        String rendered = render(comment);
        assertFalse(rendered.contains("0x000A"), rendered);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityComment.class,
            new CJavaEntityFactory(catalog(), null).NewEntityComment(1, "COMMENT").getClass());
        assertEquals(CEntityComment.class,
            new CJavaEntityFactory(catalog(), null).NewEntityComment(1, "COMMENT").getClass());
    }
}
