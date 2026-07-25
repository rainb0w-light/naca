package lexer.BMS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lexer.CBaseToken;
import lexer.CTokenList;
import lexer.CTokenType;
import org.junit.jupiter.api.Test;
import utils.COriginalLisiting;

/**
 * Regression tests for the column-72 single-'*' lookahead bug in
 * {@link lexer.CBaseLexer#DoLine}.
 *
 * <p>BMS (and COBOL) sources place a '*' continuation marker in column 72. The
 * BMS lexer truncates each physical line to {@code nbCharsUtils == 72} columns,
 * so that marker becomes the very last stored character (index 71 of a length-72
 * array). The '*' case used to emit a STAR token, advance the cursor to index 72
 * and then read {@code arrCurrentLine[72]} to decide whether it was really a
 * '**' (exponentiation) — with no bounds check. That threw
 * {@code ArrayIndexOutOfBoundsException: Index 72 out of bounds for length 72},
 * aborting the line. {@code StartLexer(InputStream)} swallowed the exception and
 * still returned {@code true}, handing a truncated token stream to the parser
 * (which then surfaced as a null root / NPE far from the real cause).
 *
 * <p>These tests pin the fix: a trailing single '*' lexes cleanly to a STAR token
 * without aborting the line or the stream, and '**' still lexes as STAR_STAR.
 */
class CBMSLexerStarLookaheadTest
{
    private static List<CBaseToken> tokenize(CTokenList lst)
    {
        List<CBaseToken> out = new ArrayList<>();
        lst.StartIter();
        CBaseToken t = lst.GetCurrentToken();
        while (t != null)
        {
            out.add(t);
            t = lst.GetNext();
        }
        return out;
    }

    private static boolean hasType(List<CBaseToken> toks, CTokenType type)
    {
        for (CBaseToken t : toks)
        {
            if (t.GetType() == type)
            {
                return true;
            }
        }
        return false;
    }

    /** Pad {@code s} with spaces so the returned string has exactly {@code width} chars. */
    private static String padTo(String s, int width)
    {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width)
        {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * A physical line whose column 72 is a single '*' continuation marker, followed
     * by a sequence field in columns 73-80 (which the BMS lexer truncates away).
     * The '*' is the last stored char of the length-72 line buffer.
     */
    @Test
    void column72SingleStarDoesNotAbortLexing()
    {
        // cols 1-71 content, '*' at col 72 (index 71), 'DVD00010' in cols 73-80.
        String line1 = padTo("ONLINM1 DFHMSD TYPE=MAP,", 71) + "*DVD00010\n";
        // a second physical line so we can prove lexing continued past the marker.
        String line2 = padTo("               MODE=INOUT,LANG=COBOL", 71) + " DVD00020\n";
        String src = line1 + line2;

        CBMSLexer lexer = new CBMSLexer();
        boolean ok = lexer.StartLexer(
            new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
            new COriginalLisiting());
        assertTrue(ok, "lexing a line ending in a column-72 '*' must succeed, not abort");

        List<CBaseToken> toks = tokenize(lexer.GetTokenList());
        assertTrue(hasType(toks, CTokenType.STAR),
            "the column-72 continuation marker must survive as a STAR token");
        // Proof the stream was not truncated at line 1: a token from line 2 is present.
        boolean sawLine2 = false;
        for (CBaseToken t : toks)
        {
            if ("MODE".equalsIgnoreCase(t.GetValue()))
            {
                sawLine2 = true;
            }
        }
        assertTrue(sawLine2, "lexing must continue past the column-72 '*' onto the next line");
    }

    /** A single '*' that is the final character of the whole input must not throw. */
    @Test
    void trailingSingleStarAtEndOfInputIsSafe()
    {
        CBMSLexer lexer = new CBMSLexer();
        boolean ok = lexer.StartLexer(
            new ByteArrayInputStream("X = Y *".getBytes(StandardCharsets.UTF_8)),
            new COriginalLisiting());
        assertTrue(ok, "a trailing single '*' at end of input must lex cleanly");
        List<CBaseToken> toks = tokenize(lexer.GetTokenList());
        assertTrue(hasType(toks, CTokenType.STAR));
    }

    /** '**' must still be recognized as the STAR_STAR (exponentiation) token. */
    @Test
    void doubleStarStillLexesAsStarStar()
    {
        CBMSLexer lexer = new CBMSLexer();
        boolean ok = lexer.StartLexer(
            new ByteArrayInputStream("A ** B\n".getBytes(StandardCharsets.UTF_8)),
            new COriginalLisiting());
        assertTrue(ok);
        List<CBaseToken> toks = tokenize(lexer.GetTokenList());
        assertTrue(hasType(toks, CTokenType.STAR_STAR),
            "'**' must still lex as STAR_STAR after the bounds-check fix");
    }

    /** '**' at the very end of input must lex as STAR_STAR without overrun. */
    @Test
    void doubleStarAtEndOfInputIsSafe()
    {
        CBMSLexer lexer = new CBMSLexer();
        boolean ok = lexer.StartLexer(
            new ByteArrayInputStream("A **".getBytes(StandardCharsets.UTF_8)),
            new COriginalLisiting());
        assertTrue(ok);
        List<CBaseToken> toks = tokenize(lexer.GetTokenList());
        assertTrue(hasType(toks, CTokenType.STAR_STAR));
    }

    /** Sanity: a clean line with no star still lexes to the expected identifiers. */
    @Test
    void plainLineStillLexes()
    {
        CBMSLexer lexer = new CBMSLexer();
        boolean ok = lexer.StartLexer(
            new ByteArrayInputStream("NMMASQ DFHMDF POS=001\n".getBytes(StandardCharsets.UTF_8)),
            new COriginalLisiting());
        assertTrue(ok);
        List<CBaseToken> toks = tokenize(lexer.GetTokenList());
        assertNotNull(toks);
        boolean sawName = false;
        for (CBaseToken t : toks)
        {
            if ("NMMASQ".equalsIgnoreCase(t.GetValue()))
            {
                sawName = true;
            }
        }
        assertTrue(sawName);
        assertEquals(false, hasType(toks, CTokenType.STAR));
    }
}
