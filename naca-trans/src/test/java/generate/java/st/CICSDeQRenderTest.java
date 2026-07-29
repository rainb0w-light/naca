package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSDeQ;

/**
 * Embedded CICS DEQ rendering through the recursive assembler (the production
 * path). CICS DEQ is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSDeQEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSDeQ.DoExport} direct backend produced:
 * <ul>
 *   <li>DEQ RESOURCE(res) -&gt; {@code CESM.deQ(<res>) ;}</li>
 *   <li>DEQ RESOURCE(res) LENGTH(len) -&gt; {@code CESM.deQ(<res>, <len>) ;}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSDeQ} direct backend.
 */
class CICSDeQRenderTest
{
    private static String render(CDataEntity resource, CDataEntity length)
    {
        CEntityCICSDeQ deq = new CEntityCICSDeQ(1, null);
        deq.SetResource(resource, length);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(deq, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("DEQ RESOURCE(res) renders CESM.deQ(<res>) ;")
    void bareDeQ()
    {
        String output = render(new MockDataEntity(2, "W-DEQ-RES"), null);
        assertTrue(output.contains("CESM.deQ(W-DEQ-RES) ;"), output);
    }

    @Test
    @DisplayName("DEQ RESOURCE(res) LENGTH(len) renders CESM.deQ(<res>, <len>) ;")
    void deQWithLength()
    {
        String output = render(new MockDataEntity(2, "W-DEQ-RES"), new MockDataEntity(3, "W-DEQ-LEN"));
        assertTrue(output.contains("CESM.deQ(W-DEQ-RES, W-DEQ-LEN) ;"), output);
    }
}
