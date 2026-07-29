package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSInquire;

/**
 * Embedded CICS INQUIRE rendering through the recursive assembler (the production
 * path). CICS INQUIRE is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSInquireEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSInquire.DoExport} direct backend produced:
 * <ul>
 *   <li>bare INQUIRE -&gt; {@code CESM.inquire() ;}</li>
 *   <li>PROGRAM only -&gt; {@code CESM.inquire().program(<ref>) ;}</li>
 *   <li>TRANSACTION only -&gt; {@code CESM.inquire().transaction(<ref>) ;}</li>
 *   <li>both PROGRAM and TRANSACTION -&gt; {@code tools.getProgramForTransID(<trans>, <prog>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSInquire} direct backend.
 */
class CICSInquireRenderTest
{
    private static String render(CDataEntity program, CDataEntity transaction)
    {
        CEntityCICSInquire inq = new CEntityCICSInquire(1, null);
        inq.program = program;
        inq.transaction = transaction;
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(inq, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare EXEC CICS INQUIRE renders CESM.inquire() ;")
    void bareInquire()
    {
        String output = render(null, null);
        assertTrue(output.contains("CESM.inquire() ;"), output);
    }

    @Test
    @DisplayName("INQUIRE PROGRAM(ref) renders CESM.inquire().program(<ref>) ;")
    void inquireWithProgram()
    {
        String output = render(new MockDataEntity(2, "MY-PROG"), null);
        assertTrue(output.contains("CESM.inquire().program(MY-PROG) ;"), output);
    }

    @Test
    @DisplayName("INQUIRE TRANSACTION(ref) renders CESM.inquire().transaction(<ref>) ;")
    void inquireWithTransaction()
    {
        String output = render(null, new MockDataEntity(2, "MY-TRANS"));
        assertTrue(output.contains("CESM.inquire().transaction(MY-TRANS) ;"), output);
    }

    @Test
    @DisplayName("INQUIRE PROGRAM(ref) TRANSACTION(ref) renders tools.getProgramForTransID(<trans>, <prog>) ;")
    void inquireWithBoth()
    {
        String output = render(new MockDataEntity(2, "MY-PROG"),
            new MockDataEntity(3, "MY-TRANS"));
        assertTrue(output.contains("tools.getProgramForTransID(MY-TRANS, MY-PROG) ;"), output);
    }
}
