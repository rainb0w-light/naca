package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CLevel88DeclarationRenderTest
{
    private static final String PROGRAM = """
                 IDENTIFICATION DIVISION.
                 PROGRAM-ID. L88TEST.
                 DATA DIVISION.
                 WORKING-STORAGE SECTION.
                 01 APPL-RESULT PIC S9(9).
                    88 APPL-AOK VALUE 0.
                    88 APPL-EOF VALUE 16.
                 PROCEDURE DIVISION.
                     IF APPL-AOK
                         MOVE 1 TO APPL-RESULT
                     END-IF.
                     STOP RUN.
           """;

    private static final String PLAIN_PROGRAM = """
                 IDENTIFICATION DIVISION.
                 PROGRAM-ID. PLAIN.
                 DATA DIVISION.
                 WORKING-STORAGE SECTION.
                 01 APPL-RESULT PIC S9(9).
                 PROCEDURE DIVISION.
                     STOP RUN.
           """;

    @Test
    void rendersLevel88ChildrenBeforeProcedureReferencesAndCompiles()
    {
        assertDoesNotThrow(this::assertLevel88Rendering, "level-88 rendering should compile");
    }

    private void assertLevel88Rendering() throws Exception
    {
        String rendered = CobolRenderTestSupport.render(PROGRAM);
        String aokDeclaration = "Cond APPL_AOK = declare.condition().value(0).var() ;";
        String eofDeclaration = "Cond APPL_EOF = declare.condition().value(16).var() ;";
        assertEquals(1, CobolRenderTestSupport.count(rendered, aokDeclaration),
            "APPL-AOK declaration count");
        assertEquals(1, CobolRenderTestSupport.count(rendered, eofDeclaration),
            "APPL-EOF declaration count");
        assertTrue(PROGRAM.contains("IF APPL-AOK"), "fixture should reference APPL-AOK");
        int ifIndex = rendered.indexOf("if (is(APPL_AOK))");
        assertTrue(ifIndex >= 0, "APPL-AOK procedure reference should render");
        assertTrue(rendered.indexOf(aokDeclaration) < ifIndex, "AOK declaration precedes reference");
        assertTrue(rendered.indexOf(eofDeclaration) < ifIndex, "EOF declaration precedes reference");

        CobolRenderTestSupport.compile(rendered);
    }

    @Test
    void plainPicDeclarationHasNoNamedConditionOutput()
    {
        assertDoesNotThrow(this::assertPlainPicRendering, "plain PIC rendering should be stable");
    }

    private void assertPlainPicRendering() throws Exception
    {
        String rendered = CobolRenderTestSupport.render(PLAIN_PROGRAM);
        assertEquals(0, CobolRenderTestSupport.count(rendered, "Cond "),
            "plain PIC should have no conditions");
        assertEquals(rendered,
            CobolRenderTestSupport.render(PLAIN_PROGRAM), "plain PIC output should be stable");
    }
}
