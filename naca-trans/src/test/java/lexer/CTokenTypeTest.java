package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CTokenTypeTest {
    @Test
    void exposesEveryCoreTokenType() {
        assertNotNull(CTokenType.IDENTIFIER);
        assertNotNull(CTokenType.KEYWORD);
        assertNotNull(CTokenType.NUMBER);
        assertNotNull(CTokenType.STRING);
        assertNotNull(CTokenType.WHITESPACE);
        assertNotNull(CTokenType.NEWLINE);
        assertNotNull(CTokenType.END_OF_BLOCK);
    }

    @Test
    void retainsTheSourceSpellingForPunctuationTokens() {
        assertTrue(CTokenType.COMMA.HasSourceValue());
        assertEquals(",", CTokenType.COMMA.GetSourceValue());
    }
}
