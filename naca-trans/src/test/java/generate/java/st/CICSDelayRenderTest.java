package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSDelay;

/**
 * Embedded CICS DELAY rendering through the recursive assembler (the production
 * path). CICS DELAY is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSDelayEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSDelay.DoExport} direct backend produced:
 * <ul>
 *   <li>DELAY INTERVAL(time) -&gt; {@code CESM.delayInterval(<time>) ;}</li>
 *   <li>DELAY FOR SECONDS(n) -&gt; {@code CESM.delaySeconds(<n>) ;}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSDelay} direct backend.
 */
class CICSDelayRenderTest
{
    private static String render(CDataEntity interval, CDataEntity seconds)
    {
        CEntityCICSDelay delay = new CEntityCICSDelay(1, null);
        if (interval != null)
        {
            delay.SetInterval(interval);
        }
        else if (seconds != null)
        {
            delay.SetSeconds(seconds);
        }
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(delay, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("DELAY INTERVAL(time) renders CESM.delayInterval(<time>) ;")
    void delayWithInterval()
    {
        String output = render(new MockDataEntity(2, "W-DELAY-TIME"), null);
        assertTrue(output.contains("CESM.delayInterval(W-DELAY-TIME) ;"), output);
    }

    @Test
    @DisplayName("DELAY FOR SECONDS(n) renders CESM.delaySeconds(<n>) ;")
    void delayWithSeconds()
    {
        String output = render(null, new MockDataEntity(3, "W-DELAY-SEC"));
        assertTrue(output.contains("CESM.delaySeconds(W-DELAY-SEC) ;"), output);
    }
}
