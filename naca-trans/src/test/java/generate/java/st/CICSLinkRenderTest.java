package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSLink;

/**
 * Embedded CICS LINK rendering through the recursive assembler (the production
 * path). CICS LINK is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSLinkEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSLink.DoExport} direct backend produced:
 * <ul>
 *   <li>unchecked PROGRAM -&gt; {@code CESM.link(<ref>).go() ;}</li>
 *   <li>checked PROGRAM -&gt; {@code CESM.link(<const>.class).go() ;}</li>
 *   <li>COMMAREA appends {@code .commarea(<ref>, -1, -1)} (LINK always emits all
 *       three commarea args, defaulting omitted LENGTH/DATALENGTH to -1)</li>
 *   <li>COMMAREA + LENGTH renders {@code .commarea(<ref>, <len>, -1)}</li>
 *   <li>COMMAREA + LENGTH + DATALENGTH renders {@code .commarea(<ref>, <len>, <dlen>)}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSLink} direct backend.
 */
class CICSLinkRenderTest
{
    // Parameter names deliberately differ from CEntityCICSLink's inherited fields
    // (refProgram/refCommArea/commAreaLength/commAreaDataLength): inside the
    // anonymous subclass an unqualified name would resolve to the inherited field
    // and shadow an identically-named parameter, so the getters below return these
    // distinctly-named captures.
    private static String render(CDataEntity prog, CDataEntity comm, CDataEntity len,
        CDataEntity dlen, boolean checked, String constValue)
    {
        CEntityCICSLink link = new CEntityCICSLink(1, null)
        {
            @Override
            public CDataEntity getProgram() { return prog; }
            @Override
            public CDataEntity getCommArea() { return comm; }
            @Override
            public CDataEntity getCommLength() { return len; }
            @Override
            public CDataEntity getCommDataLength() { return dlen; }
            @Override
            public boolean isChecked() { return checked; }
            @Override
            public String getProgramConstantValue() { return constValue; }
        };
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(link, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("LINK PROGRAM(ref) renders CESM.link(<ref>).go() ;")
    void linkWithProgram()
    {
        String output = render(new MockDataEntity(2, "CALLMSG"), null, null, null, false, null);
        assertTrue(output.contains("CESM.link(CALLMSG).go() ;"), output);
    }

    @Test
    @DisplayName("checked LINK PROGRAM renders the program class literal <const>.class")
    void linkWithCheckedProgram()
    {
        String output = render(new MockDataEntity(2, "ignored"), null, null, null, true, "CALLMSG");
        assertTrue(output.contains("CESM.link(CALLMSG.class).go() ;"), output);
    }

    @Test
    @DisplayName("LINK PROGRAM + COMMAREA renders all three commarea args with -1 defaults")
    void linkWithCommArea()
    {
        String output = render(new MockDataEntity(2, "CALLMSG"),
            new MockDataEntity(3, "MSG-ZONE"), null, null, false, null);
        assertTrue(output.contains("CESM.link(CALLMSG).commarea(MSG-ZONE, -1, -1) ;"), output);
    }

    @Test
    @DisplayName("LINK PROGRAM + COMMAREA + LENGTH renders <ref>, <len>, -1")
    void linkWithCommAreaAndLength()
    {
        String output = render(new MockDataEntity(2, "CALLMSG"),
            new MockDataEntity(3, "MSG-ZONE"), new MockDataEntity(4, "WS-LEN"), null, false, null);
        assertTrue(output.contains("CESM.link(CALLMSG).commarea(MSG-ZONE, WS-LEN, -1) ;"), output);
    }

    @Test
    @DisplayName("LINK PROGRAM + COMMAREA + LENGTH + DATALENGTH renders all three references")
    void linkWithCommAreaLengthAndDataLength()
    {
        String output = render(new MockDataEntity(2, "CALLMSG"),
            new MockDataEntity(3, "MSG-ZONE"), new MockDataEntity(4, "WS-LEN"),
            new MockDataEntity(5, "WS-DLEN"), false, null);
        assertTrue(output.contains("CESM.link(CALLMSG).commarea(MSG-ZONE, WS-LEN, WS-DLEN) ;"), output);
    }
}
