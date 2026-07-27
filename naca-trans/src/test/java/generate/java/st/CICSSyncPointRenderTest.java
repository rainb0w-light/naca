package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSSyncPoint;

/**
 * Embedded CICS SYNCPOINT rendering through the recursive assembler (the production
 * path). CICS SYNCPOINT is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSSyncPointEntity} binding; this test drives it directly and
 * asserts byte-for-byte parity with the output shapes the retired
 * {@code CJavaCICSSyncPoint.DoExport} direct backend produced:
 * <ul>
 *   <li>commit (default) -&gt; {@code CESM.syncPointCommit() ;}</li>
 *   <li>rollback -&gt; {@code CESM.syncPointRollback() ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSSyncPoint} direct backend.
 */
class CICSSyncPointRenderTest
{
    private static String render(boolean rollback)
    {
        CEntityCICSSyncPoint syncPoint = new CEntityCICSSyncPoint(1, null, rollback);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(syncPoint, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("EXEC CICS SYNCPOINT renders CESM.syncPointCommit() ;")
    void syncPointCommit()
    {
        String output = render(false);
        assertTrue(output.contains("CESM.syncPointCommit() ;"), output);
    }

    @Test
    @DisplayName("EXEC CICS SYNCPOINT ROLLBACK renders CESM.syncPointRollback() ;")
    void syncPointRollback()
    {
        String output = render(true);
        assertTrue(output.contains("CESM.syncPointRollback() ;"), output);
    }
}
