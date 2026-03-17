package lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CTokenType enum
 */
public class CTokenTypeTest {

    @Test
    @DisplayName("Test all token types exist")
    void testAllTokenTypesExist() {
        assertNotNull(CTokenType.IDENTIFIER);
        assertNotNull(CTokenType.KEYWORD);
        assertNotNull(CTokenType.NUMBER);
        assertNotNull(CTokenType.STRING);
        assertNotNull(CTokenType.WHITESPACE);
        assertNotNull(CTokenType.NEWLINE);
        assertNotNull(CTokenType.END_OF_BLOCK);
    }
}