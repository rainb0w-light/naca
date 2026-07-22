package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.CJavaComment;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Validates the comment REFERENCE template ({@code javaComment}) against the
 * direct generator {@code CJavaComment.ExportReference}. The historical quirks
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

    private String render(CJavaComment comment)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(comment, JavaTemplateRole.REFERENCE).strip();
    }

    @Test
    void plainCommentMatchesDirectExportReference()
    {
        CJavaComment comment =
            new CJavaComment(1, catalog(), new MockJavaExporter(), "HELLO WORLD");
        assertEquals("// HELLO WORLD", render(comment));
        assertEquals(comment.ExportReference(1), render(comment));
    }

    @Test
    void trailingWhitespaceIsRightTrimmed()
    {
        CJavaComment comment =
            new CJavaComment(1, catalog(), new MockJavaExporter(), "HELLO   ");
        assertEquals("// HELLO", render(comment));
        assertEquals(comment.ExportReference(1), render(comment));
    }

    @Test
    void carriageReturnIsEscapedWithTheHistoricalCapitalOQuirk()
    {
        // \r at index 1 (> 0) triggers escaping; \r maps to "Ox000D" (capital O).
        CJavaComment comment =
            new CJavaComment(1, catalog(), new MockJavaExporter(), "A\rB");
        assertTrue(render(comment).contains("AOx000DB"), render(comment));
        assertEquals(comment.ExportReference(1), render(comment));
    }

    @Test
    void newlineAtIndexZeroIsNotEscapedPreservingTheIndexOfGate()
    {
        // \n at index 0 does NOT satisfy indexOf > 0, so it is left unescaped.
        CJavaComment comment =
            new CJavaComment(1, catalog(), new MockJavaExporter(), "\nLEADING");
        String rendered = render(comment);
        assertFalse(rendered.contains("0x000A"), rendered);
        // The direct generator applies the same gate (parity).
        assertFalse(comment.ExportReference(1).contains("0x000A"));
    }
}
