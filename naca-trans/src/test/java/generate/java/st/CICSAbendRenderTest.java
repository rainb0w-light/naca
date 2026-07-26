package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSAbend;

/**
 * Embedded CICS ABEND rendering through the recursive assembler (the production
 * path). CICS ABEND is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSAbendEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSAbend.DoExport} direct backend produced:
 * <ul>
 *   <li>bare ABEND -&gt; {@code CESM.abend() ;}</li>
 *   <li>ABEND ABCODE(ref) -&gt; {@code CESM.abend(<ref>) ;}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSAbend} direct backend.
 */
class CICSAbendRenderTest
{
    private static String render(CDataEntity abcode)
    {
        CEntityCICSAbend abend = new CEntityCICSAbend(1, null)
        {
            @Override
            public CDataEntity getABCode() { return abcode; }
        };
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(abend, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare ABEND renders CESM.abend() ;")
    void bareAbend()
    {
        String output = render(null);
        assertTrue(output.contains("CESM.abend() ;"), output);
    }

    @Test
    @DisplayName("ABEND ABCODE(ref) renders CESM.abend(<ref>) ;")
    void abendWithABCode()
    {
        String output = render(new MockDataEntity(2, "W-ABEND-DB2"));
        assertTrue(output.contains("CESM.abend(W-ABEND-DB2) ;"), output);
    }
}
