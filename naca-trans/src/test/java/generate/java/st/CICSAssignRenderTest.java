package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSAssign;

/**
 * Embedded CICS ASSIGN rendering through the recursive assembler (the production
 * path). CICS ASSIGN is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSAssignEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSAssign.DoExport} direct backend produced:
 * <ul>
 *   <li>APPLID(ref) -&gt; {@code CESM.assign().APPLID(<ref>) ;};</li>
 *   <li>TCTUALENG(ref) -&gt; {@code CESM.assign().TCTUALENG(<ref>) ;}</li>
 *   <li>multiple requests chain on one CESM statement, in AddRequest order, e.g.
 *       {@code CESM.assign().APPLID(<ref>).TCTUALENG(<ref>) ;};</li>
 *   <li>a statement whose requests are all ignored is ignored itself, so the
 *       assembler's active-children selection drops it exactly as the legacy
 *       ExportChildren loop did.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSAssign} direct backend.
 */
class CICSAssignRenderTest
{
    private static String render(CEntityCICSAssign assign)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("ASSIGN APPLID(ref) renders CESM.assign().APPLID(<ref>) ;")
    void assignWithApplid()
    {
        CEntityCICSAssign assign = new CEntityCICSAssign(1, null);
        assign.AddRequest("APPLID", new MockDataEntity(2, "W-APPLID"));
        String output = render(assign);
        assertTrue(output.contains("CESM.assign().APPLID(W-APPLID) ;"), output);
    }

    @Test
    @DisplayName("ASSIGN TCTUALENG(ref) renders CESM.assign().TCTUALENG(<ref>) ;")
    void assignWithTctualeng()
    {
        CEntityCICSAssign assign = new CEntityCICSAssign(1, null);
        assign.AddRequest("TCTUALENG", new MockDataEntity(2, "W-LEN"));
        String output = render(assign);
        assertTrue(output.contains("CESM.assign().TCTUALENG(W-LEN) ;"), output);
    }

    @Test
    @DisplayName("ASSIGN with several requests chains them on one CESM statement in order")
    void assignWithChainedRequests()
    {
        CEntityCICSAssign assign = new CEntityCICSAssign(1, null);
        assign.AddRequest("APPLID", new MockDataEntity(2, "W-APPLID"));
        assign.AddRequest("TCTUALENG", new MockDataEntity(3, "W-LEN"));
        String output = render(assign);
        assertTrue(output.contains("CESM.assign().APPLID(W-APPLID).TCTUALENG(W-LEN) ;"),
            output);
    }

    @Test
    @DisplayName("ASSIGN with an active request is not ignored")
    void assignWithRequestIsNotIgnored()
    {
        CEntityCICSAssign assign = new CEntityCICSAssign(1, null);
        assign.AddRequest("APPLID", new MockDataEntity(2, "W-APPLID"));
        assertFalse(assign.ignore(), "an ASSIGN with an active request must not be ignored");
    }

    @Test
    @DisplayName("ASSIGN whose requests are all ignored is ignored (dropped by the assembler)")
    void assignWithOnlyIgnoredRequests()
    {
        CEntityCICSAssign assign = new CEntityCICSAssign(1, null);
        assign.AddRequest("APPLID", new MockDataEntity(2, "W-APPLID")
        {
            @Override
            public boolean ignore()
            {
                return true;
            }
        });
        assertTrue(assign.ignore(),
            "an ASSIGN whose requests are all ignored must be ignored itself");
    }

    @Test
    @DisplayName("ASSIGN with no request at all is ignored")
    void assignWithNoRequest()
    {
        CEntityCICSAssign assign = new CEntityCICSAssign(1, null);
        assertTrue(assign.ignore(), "a request-less ASSIGN must be ignored");
    }
}
