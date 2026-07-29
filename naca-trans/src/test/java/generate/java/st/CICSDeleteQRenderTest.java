package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSDeleteQ;

/**
 * Embedded CICS DELETEQ rendering through the recursive assembler (the production
 * path). CICS DELETEQ is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSDeleteQEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSDeleteQ.DoExport} direct backend produced:
 * <ul>
 *   <li>DELETEQ TD QUEUE(q) -&gt; {@code CESM.deleteTransiantQueue(<q>) ;}</li>
 *   <li>DELETEQ TS QUEUE(q) -&gt; {@code CESM.deleteTempQueue(<q>) ;}</li>
 *   <li>DELETEQ TS QUEUE(q) SYSID(s) -&gt; {@code CESM.deleteTempQueue(<q>).sysID(<s>) ;}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSDeleteQ} direct backend.
 */
class CICSDeleteQRenderTest
{
    private static String render(boolean persistent, CDataEntity name, CDataEntity sysID)
    {
        CEntityCICSDeleteQ delq = new CEntityCICSDeleteQ(1, null, persistent);
        delq.SetName(name);
        if (sysID != null)
        {
            delq.SetSysID(sysID);
        }
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(delq, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("DELETEQ TD QUEUE(name) renders CESM.deleteTransiantQueue(<name>) ;")
    void deleteQTransiant()
    {
        String output = render(true, new MockDataEntity(2, "W-QNAME"), null);
        assertTrue(output.contains("CESM.deleteTransiantQueue(W-QNAME) ;"), output);
    }

    @Test
    @DisplayName("DELETEQ TS QUEUE(name) renders CESM.deleteTempQueue(<name>) ;")
    void deleteQTemp()
    {
        String output = render(false, new MockDataEntity(2, "W-QNAME"), null);
        assertTrue(output.contains("CESM.deleteTempQueue(W-QNAME) ;"), output);
    }

    @Test
    @DisplayName("DELETEQ TS QUEUE(name) SYSID(sys) renders CESM.deleteTempQueue(<name>).sysID(<sys>) ;")
    void deleteQWithSysID()
    {
        String output = render(false, new MockDataEntity(2, "W-QNAME"), new MockDataEntity(3, "W-SYSID"));
        assertTrue(output.contains("CESM.deleteTempQueue(W-QNAME).sysID(W-SYSID) ;"), output);
    }
}
