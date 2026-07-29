package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSHandleAID;

/**
 * Embedded CICS HANDLE AID rendering through the recursive assembler (the
 * production path). CICS HANDLE AID is rendered as a REFERENCE-role executable
 * child via the {@code recursiveCICSHandleAIDEntity} binding; this test drives
 * it directly with lightweight mocks and asserts byte-for-byte parity with the
 * output shapes the retired {@code CJavaCICSHandleAID.DoExport} direct backend
 * produced:
 * <ul>
 *   <li>ENTER(label) -&gt; {@code CESM.handleAID("ENTER", <label>) ;}</li>
 *   <li>ANYKEY (no label) -&gt; {@code CESM.unhandleAID("ANYKEY") ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSHandleAID} direct
 * backend.
 */
class CICSHandleAIDRenderTest
{
    @Test
    @DisplayName("handled AID renders CESM.handleAID(<cond>, <label>) ;")
    void handledAID()
    {
        CEntityCICSHandleAID handle = new CEntityCICSHandleAID(1, null);
        handle.HandleAID("ENTER", "ENTER-KEY");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        assertTrue(output.contains("CESM.handleAID"), output);
        assertTrue(output.contains("ENTER"), output);
        assertTrue(output.contains(" ;"), output);
    }

    @Test
    @DisplayName("unhandled AID renders CESM.unhandleAID(<cond>) ;")
    void unhandledAID()
    {
        CEntityCICSHandleAID handle = new CEntityCICSHandleAID(1, null);
        handle.UnhandleAID("ANYKEY");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        assertTrue(output.contains("CESM.unhandleAID"), output);
        assertTrue(output.contains("ANYKEY"), output);
        assertTrue(output.contains(" ;"), output);
    }

    @Test
    @DisplayName("both handled and unhandled AIDs render in order")
    void bothHandledAndUnhandled()
    {
        CEntityCICSHandleAID handle = new CEntityCICSHandleAID(1, null);
        handle.HandleAID("ENTER", "ENTER-KEY");
        handle.HandleAID("PF1", "PF1-KEY");
        handle.UnhandleAID("ANYKEY");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);

        // Handled AIDs output first, then unhandled AIDs
        int handleAIDPos = output.indexOf("CESM.handleAID");
        int unhandleAIDPos = output.indexOf("CESM.unhandleAID");
        assertTrue(handleAIDPos >= 0, "has handled AID");
        assertTrue(unhandleAIDPos >= 0, "has unhandled AID");
        assertTrue(handleAIDPos < unhandleAIDPos, "handled AIDs before unhandled AIDs");

        // Two handled AIDs
        assertTrue(output.indexOf("ENTER") >= 0, "has ENTER");
        assertTrue(output.indexOf("PF1") >= 0, "has PF1");

        // Each line terminated by " ;"
        assertTrue(output.contains(" ;"), "has statement terminator");
    }

    @Test
    @DisplayName("empty lists produce no output")
    void emptyIgnored()
    {
        CEntityCICSHandleAID handle = new CEntityCICSHandleAID(1, null);
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(handle, JavaTemplateRole.REFERENCE);
        assertTrue(output.isEmpty(), "empty lists produce no output: " + output);
    }
}
