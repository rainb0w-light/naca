package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSEnQ;

/**
 * Embedded CICS ENQ rendering through the recursive assembler (the production
 * path). CICS ENQ is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSEnQEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSEnQ.DoExport} direct backend produced:
 * <ul>
 *   <li>ENQ RESOURCE(res) -&gt; {@code CESM.enQ(<res>) ;}</li>
 *   <li>ENQ RESOURCE(res) LENGTH(len) -&gt; {@code CESM.enQ(<res>, <len>) ;}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSEnQ} direct backend.
 */
class CICSEnQRenderTest
{
    private static String render(CDataEntity resource, CDataEntity length)
    {
        CEntityCICSEnQ enq = new CEntityCICSEnQ(1, null);
        enq.SetResource(resource, length);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(enq, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("ENQ RESOURCE(res) renders CESM.enQ(<res>) ;")
    void bareEnQ()
    {
        String output = render(new MockDataEntity(2, "W-ENQ-RES"), null);
        assertTrue(output.contains("CESM.enQ(W-ENQ-RES) ;"), output);
    }

    @Test
    @DisplayName("ENQ RESOURCE(res) LENGTH(len) renders CESM.enQ(<res>, <len>) ;")
    void enQWithLength()
    {
        String output = render(new MockDataEntity(2, "W-ENQ-RES"), new MockDataEntity(3, "W-ENQ-LEN"));
        assertTrue(output.contains("CESM.enQ(W-ENQ-RES, W-ENQ-LEN) ;"), output);
    }
}
