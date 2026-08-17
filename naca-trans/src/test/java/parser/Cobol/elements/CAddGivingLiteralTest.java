package parser.Cobol.elements;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CStringExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import jlib.misc.AsciiEbcdicConverter;
import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import org.junit.jupiter.api.Test;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import parser.CGlobalCommentContainer;
import semantic.CEntityClass;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

class CAddGivingLiteralTest
{
    private static final String PROGRAM = """
                 IDENTIFICATION DIVISION.
                 PROGRAM-ID. ADDTEST.
                 DATA DIVISION.
                 WORKING-STORAGE SECTION.
                 01 RESULT PIC 9(4).
                 01 A PIC 9(4).
                 01 B PIC 9(4).
                 PROCEDURE DIVISION.
                     ADD 8 TO ZERO GIVING RESULT.
                     ADD A TO B GIVING RESULT.
                     ADD 1 TO RESULT.
                     STOP RUN.
           """;

    @Test
    void parsesAndRendersLiteralDestinationAndRegressions() {
        COriginalLisiting listing = new COriginalLisiting();
        CCobolLexer lexer = new CCobolLexer();
        assertTrue(lexer.StartLexer(new ByteArrayInputStream(
            PROGRAM.getBytes(StandardCharsets.UTF_8)), listing));
        CTokenList tokens = lexer.GetTokenList();
        CCobolParser parser = new CCobolParser();
        assertTrue(parser.StartParsing(tokens));
        CProgram program = parser.GetRootElement();
        AsciiEbcdicConverter.create();
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        CObjectCatalog catalog = new CObjectCatalog(global, listing,
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
        CStringExporter exporter = new CStringExporter();
        catalog.setExporter(exporter);
        CEntityClass root = program.DoSemanticAnalysis(new CJavaEntityFactory(catalog, exporter));
        assertTrue(root != null, "semantic root should be created");
        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(root, JavaTemplateRole.ROOT);
        assertTrue(rendered.contains("add(8, 0).to(RESULT)"), rendered);
        assertTrue(rendered.contains("add(A, B).to(RESULT)"), rendered);
        assertTrue(rendered.contains("inc(RESULT)"), rendered);
    }

    @Test
    void rejectsLiteralToTargetWithoutGiving() {
        COriginalLisiting listing = new COriginalLisiting();
        CCobolLexer lexer = new CCobolLexer();
        String invalidStatement = "       ADD 1 TO ZERO.";
        assertTrue(lexer.StartLexer(new ByteArrayInputStream(
            invalidStatement.getBytes(StandardCharsets.UTF_8)), listing));
        lexer.GetTokenList().StartIter();
        CAdd add = new CAdd(1);
        assertFalse(add.Parse(lexer.GetTokenList(), new CGlobalCommentContainer()));
    }
}
