package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSAskTime;

/**
 * Embedded CICS ASKTIME rendering through the recursive assembler (the production
 * path). CICS ASKTIME is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSAskTimeEntity} binding; this test drives it directly and
 * asserts byte-for-byte parity with the output shape the retired
 * {@code CJavaCICSAskTime.DoExport} direct backend produced:
 * <ul>
 *   <li>bare ASKTIME -&gt; {@code CESM.askTime() ;}</li>
 * </ul>
 * ASKTIME carries no operands, so there is a single rendering shape. Replaces the
 * legacy {@code generate.java.CICS.CJavaCICSAskTime} direct backend.
 */
class CICSAskTimeRenderTest
{
    private static String render()
    {
        CEntityCICSAskTime askTime = new CEntityCICSAskTime(1, null);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(askTime, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare ASKTIME renders CESM.askTime() ;")
    void bareAskTime()
    {
        String output = render();
        assertTrue(output.contains("CESM.askTime() ;"), output);
    }

    @Test
    @DisplayName("ASKTIME output is exactly the retired backend's statement")
    void exactParityWithRetiredBackend()
    {
        String output = render();
        assertEquals("CESM.askTime() ;", output.trim());
    }
}
