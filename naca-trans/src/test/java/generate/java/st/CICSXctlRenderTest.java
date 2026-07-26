package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSXctl;

/**
 * Embedded CICS XCTL rendering through the recursive assembler (the production
 * path). CICS XCTL is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSXctlEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSXctl.DoExport} direct backend produced:
 * <ul>
 *   <li>unchecked PROGRAM -&gt; {@code CESM.xctl(<ref>).go() ;}</li>
 *   <li>checked PROGRAM -&gt; {@code CESM.xctl(<const>.class).go() ;}</li>
 *   <li>COMMAREA appends {@code .commarea(<ref>)} instead of {@code .go()}</li>
 *   <li>COMMAREA + LENGTH renders {@code .commarea(<ref>, <len>)}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSXctl} direct backend.
 */
class CICSXctlRenderTest
{
    // Parameter names deliberately differ from CEntityCICSXctl's inherited fields
    // (refProgram/refCommArea/commAreaLength): inside the anonymous subclass an
    // unqualified name would resolve to the inherited field and shadow an
    // identically-named parameter, so the getters below return these
    // distinctly-named captures.
    private static String render(CDataEntity prog, CDataEntity comm, CDataEntity len,
        boolean checked, String constValue)
    {
        CEntityCICSXctl xctl = new CEntityCICSXctl(1, null)
        {
            @Override
            public CDataEntity getProgram() { return prog; }
            @Override
            public CDataEntity getCommArea() { return comm; }
            @Override
            public CDataEntity getCommLength() { return len; }
            @Override
            public boolean isChecked() { return checked; }
            @Override
            public String getProgramConstantValue() { return constValue; }
        };
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(xctl, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("XCTL PROGRAM(ref) renders CESM.xctl(<ref>).go() ;")
    void xctlWithProgram()
    {
        String output = render(new MockDataEntity(2, "ONLINE1"), null, null, false, null);
        assertTrue(output.contains("CESM.xctl(ONLINE1).go() ;"), output);
    }

    @Test
    @DisplayName("checked XCTL PROGRAM renders the program class literal <const>.class")
    void xctlWithCheckedProgram()
    {
        String output = render(new MockDataEntity(2, "ignored"), null, null, true, "ONLINE1");
        assertTrue(output.contains("CESM.xctl(ONLINE1.class).go() ;"), output);
    }

    @Test
    @DisplayName("XCTL PROGRAM + COMMAREA renders CESM.xctl(<ref>).commarea(<commArea>) ;")
    void xctlWithCommArea()
    {
        String output = render(new MockDataEntity(2, "ONLINE1"),
            new MockDataEntity(3, "COMMAREA"), null, false, null);
        assertTrue(output.contains("CESM.xctl(ONLINE1).commarea(COMMAREA) ;"), output);
    }

    @Test
    @DisplayName("XCTL PROGRAM + COMMAREA + LENGTH renders both commarea reference args")
    void xctlWithCommAreaAndLength()
    {
        String output = render(new MockDataEntity(2, "ONLINE1"),
            new MockDataEntity(3, "COMMAREA"), new MockDataEntity(4, "WS-LEN"), false, null);
        assertTrue(output.contains("CESM.xctl(ONLINE1).commarea(COMMAREA, WS-LEN) ;"), output);
    }
}
