package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSHandleCondition;

/**
 * Embedded CICS HANDLE CONDITION rendering through the recursive assembler (the
 * production path). CICS HANDLE CONDITION is rendered as a REFERENCE-role
 * executable child via the {@code recursiveCICSHandleConditionEntity} binding;
 * this test drives it directly with lightweight mocks and asserts byte-for-byte
 * parity with the output shapes the retired
 * {@code CJavaCICSHandleCondition.DoExport} direct backend produced:
 * <ul>
 *   <li>a handled condition renders
 *       {@code CESM.handleCondition("cond", label)} chained onto the prefix</li>
 *   <li>an unhandled condition renders
 *       {@code CESM.unhandleCondition("cond")} chained onto the prefix</li>
 *   <li>all conditions chain into a single statement terminated by {@code ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSHandleCondition}
 * direct backend.
 */
class CICSHandleConditionRenderTest
{
    private static CEntityCICSHandleCondition entity()
    {
        return new CEntityCICSHandleCondition(1, null);
    }

    @Test
    @DisplayName("handled condition renders CESM.handleCondition(<cond>, <label>) chained")
    void handledCondition()
    {
        CEntityCICSHandleCondition handle = entity();
        handle.HandleCondition("COND1", "COND1-LABEL");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        assertEquals("CESM.handleCondition(\"COND1\", COND1_LABEL) ;", output.trim());
    }

    @Test
    @DisplayName("multiple handled conditions chain onto the CESM prefix")
    void multipleHandledConditions()
    {
        CEntityCICSHandleCondition handle = entity();
        handle.HandleCondition("COND1", "COND1-LABEL");
        handle.HandleCondition("COND2", "COND2-LABEL");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        assertEquals(
            "CESM.handleCondition(\"COND1\", COND1_LABEL).handleCondition(\"COND2\", COND2_LABEL) ;",
            output.trim());
    }

    @Test
    @DisplayName("unhandled condition renders CESM.unhandleCondition(<cond>) chained")
    void unhandledCondition()
    {
        CEntityCICSHandleCondition handle = entity();
        handle.UnhandleCondition("COND1");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        assertEquals("CESM.unhandleCondition(\"COND1\") ;", output.trim());
    }

    @Test
    @DisplayName("handled conditions before unhandled conditions, all chained into one statement")
    void handledThenUnhandled()
    {
        CEntityCICSHandleCondition handle = entity();
        handle.HandleCondition("COND1", "COND1-LABEL");
        handle.UnhandleCondition("COND2");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        // Handled conditions first, then unhandled, then semicolon terminator
        assertTrue(output.startsWith("CESM.handleCondition"), "starts with handled");
        assertTrue(output.contains(".unhandleCondition"), "has unhandled chain");
        assertTrue(output.endsWith(" ;"), "ends with terminator");
        int handledPos = output.indexOf("handleCondition");
        int unhandledPos = output.indexOf("unhandleCondition");
        assertTrue(handledPos >= 0, "has handled condition");
        assertTrue(unhandledPos > handledPos, "handled before unhandled");
        // Single statement: no newlines inside
        assertEquals(1, output.trim().split("\n").length, "single-line output");
    }

    @Test
    @DisplayName("empty lists produce no output")
    void emptyIgnored()
    {
        CEntityCICSHandleCondition handle = entity();
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        assertTrue(output.isEmpty(), "empty lists produce no output: " + output);
    }
}
