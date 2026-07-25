package parser.BMS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lexer.BMS.CBMSLexer;
import lexer.CBaseToken;
import lexer.CTokenList;
import org.junit.jupiter.api.Test;
import parser.CBaseElement;
import parser.map_elements.CMapElement;
import parser.map_elements.CMapSetElement;
import utils.COriginalLisiting;

/**
 * End-to-end regression: the real {@code ONLINM1.bms} mapset must lex and parse
 * completely.
 *
 * <p>Before the column-72 '*' lookahead fix, the first physical line
 * ({@code ONLINM1 DFHMSD TYPE=MAP, ... *} with the continuation marker in column
 * 72) threw {@code ArrayIndexOutOfBoundsException} inside the lexer. The
 * exception was swallowed by {@code StartLexer(InputStream)} (which still returned
 * {@code true}), so the parser received a truncated token stream and the mapset
 * came back null/empty — the failure surfaced as {@code tokMapSet == null} far
 * from its cause. This test pins the whole-file contract:
 *
 * <ul>
 *   <li>the lexer reaches the final {@code END} on line 80 (token stream complete);</li>
 *   <li>{@code CBMSParser.StartParsing} succeeds;</li>
 *   <li>the root element is named {@code ONLINM1};</li>
 *   <li>the parsed tree has a non-empty map/field structure.</li>
 * </ul>
 */
class ONLINM1BmsFullParseTest
{
    private static final int LAST_LINE = 80;

    private static Path locate()
    {
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol/ONLINM1.bms"),
            Path.of("../NacaSamples/cobol/ONLINM1.bms") })
        {
            if (Files.exists(p))
            {
                return p;
            }
        }
        return null;
    }

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

    @Test
    void onlinm1LexesCompletelyAndParsesToANonEmptyMapset() throws Exception
    {
        Path bms = locate();
        assertNotNull(bms, "ONLINM1.bms sample must exist");

        // --- Stage 1: lex the whole file. ---
        CBMSLexer lexer = new CBMSLexer();
        boolean lexed;
        try (InputStream in = new BufferedInputStream(new FileInputStream(bms.toFile())))
        {
            lexed = lexer.StartLexer(in, new COriginalLisiting());
        }
        assertTrue(lexed, "lexing ONLINM1.bms must succeed (no swallowed column-72 '*' failure)");

        CTokenList tokens = lexer.GetTokenList();
        List<CBaseToken> toks = tokenize(tokens);
        assertTrue(toks.size() > 50, "token stream should be substantial, got " + toks.size());

        // The lexer must have reached the final physical line (the END on line 80),
        // proving the stream was not truncated at the first column-72 marker.
        int maxLine = 0;
        boolean sawEnd = false;
        for (CBaseToken t : toks)
        {
            maxLine = Math.max(maxLine, t.getLine());
            if ("END".equalsIgnoreCase(t.GetValue()))
            {
                sawEnd = true;
            }
        }
        assertEquals(LAST_LINE, maxLine, "lexer must reach the END on line " + LAST_LINE);
        assertTrue(sawEnd, "the terminating END token must be present");

        // Late-file field labels must survive — concrete proof the tail was lexed.
        boolean sawNewpass = false;
        boolean sawLierr = false;
        for (CBaseToken t : toks)
        {
            if ("NEWPASS".equalsIgnoreCase(t.GetValue()))
            {
                sawNewpass = true;
            }
            if ("LIERR".equalsIgnoreCase(t.GetValue()))
            {
                sawLierr = true;
            }
        }
        assertTrue(sawNewpass, "NEWPASS field (line 70) must be in the token stream");
        assertTrue(sawLierr, "LIERR field (line 75) must be in the token stream");

        // --- Stage 2: parse the complete token stream. ---
        CBMSParser parser = new CBMSParser();
        boolean parsed = parser.StartParsing(tokens);
        assertTrue(parsed, "CBMSParser.StartParsing must succeed on the complete stream");

        CMapSetElement root = parser.GetRootElement();
        assertNotNull(root, "parsed root must not be null (was tokMapSet == null before the fix)");
        assertEquals("ONLINM1", root.getName(), "root mapset must be named ONLINM1");

        // --- Non-empty map/field structure. ---
        assertTrue(root.getNbChildren() >= 1, "mapset must contain at least one map");
        CMapElement firstMap = null;
        for (CBaseElement child : root.getChildElements())
        {
            if (child instanceof CMapElement map)
            {
                firstMap = map;
                break;
            }
        }
        assertNotNull(firstMap, "mapset must contain a CMapElement (the ONLINEF map)");
        assertTrue(firstMap.getNbChildren() >= 1,
            "map must contain at least one field/group child");
    }
}
