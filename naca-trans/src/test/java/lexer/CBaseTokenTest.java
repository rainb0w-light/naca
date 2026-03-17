package lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class CBaseTokenTest {

    @Test
    @DisplayName("Test CTokenIdentifier creation and GetValue")
    void testTokenIdentifierCreation() {
        CTokenIdentifier token = new CTokenIdentifier("testId", 10, false);
        assertEquals("testId", token.GetValue());
        assertEquals(CTokenType.IDENTIFIER, token.GetType());
        assertEquals(10, token.getLine());
        assertFalse(token.m_bIsNewLine);
    }

    @Test
    @DisplayName("Test CTokenIdentifier newline flag")
    void testTokenIdentifierNewline() {
        CTokenIdentifier token = new CTokenIdentifier("myVar", 5, true);
        assertTrue(token.m_bIsNewLine);
    }

    @Test
    @DisplayName("Test CTokenString creation")
    void testTokenStringCreation() {
        CTokenString token = new CTokenString("hello".toCharArray(), 20, false);
        assertEquals("hello", token.GetValue());
        assertEquals(CTokenType.STRING, token.GetType());
    }

    @Test
    @DisplayName("Test CTokenNumber creation and GetIntValue")
    void testTokenNumberCreation() {
        CTokenNumber token = new CTokenNumber("42", 15, false);
        assertEquals("42", token.GetValue());
        assertEquals(42, token.GetIntValue());
        assertEquals(CTokenType.NUMBER, token.GetType());
    }

    @Test
    @DisplayName("Test CTokenKeyword creation")
    void testTokenKeywordCreation() {
        CKeywordList keywordList = new CKeywordList();
        CReservedKeyword keyword = new CReservedKeyword(keywordList, "TEST");
        CTokenKeyword token = new CTokenKeyword(keyword, 1, false);
        assertEquals(CTokenType.KEYWORD, token.GetType());
        assertNotNull(token.GetKeyword());
        assertTrue(token.IsKeyword());
    }

    @Test
    @DisplayName("Test token IsWhiteSpace check")
    void testTokenWhitespaceCheck() {
        CTokenIdentifier token = new CTokenIdentifier("test", 1, false);
        assertFalse(token.IsWhiteSpace());
    }

    @Test
    @DisplayName("Test setLine method")
    void testSetLine() {
        CTokenIdentifier token = new CTokenIdentifier("test", 1, false);
        token.setLine(100);
        assertEquals(100, token.getLine());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        CTokenIdentifier token = new CTokenIdentifier("myVar", 1, false);
        String str = token.toString();
        assertTrue(str.contains("myVar"));
    }
}