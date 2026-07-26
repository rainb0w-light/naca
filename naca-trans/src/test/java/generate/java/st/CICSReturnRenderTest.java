package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSReturn;

/**
 * Embedded CICS RETURN rendering through the recursive assembler (the production
 * path). CICS RETURN is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSReturnEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSReturn.DoExport} direct backend produced:
 * <ul>
 *   <li>bare RETURN -&gt; {@code CESM.returnTrans() ;}</li>
 *   <li>unchecked TRANSID -&gt; {@code CESM.returnTrans(<ref>) ;}</li>
 *   <li>checked TRANSID -&gt; {@code CESM.returnTrans(<const>.class) ;}</li>
 *   <li>COMMAREA / LENGTH append further reference arguments.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSReturn} direct backend.
 */
class CICSReturnRenderTest
{
    // Parameter names deliberately differ from CEntityCICSReturn's inherited fields
    // (transID/commArea/commLenght): inside the anonymous subclass an unqualified
    // name would resolve to the inherited field and shadow an identically-named
    // parameter, so the getters below return these distinctly-named captures.
    private static String render(CDataEntity tid, CDataEntity comm, CDataEntity len,
        boolean checked, String constValue)
    {
        CEntityCICSReturn ret = new CEntityCICSReturn(1, null)
        {
            @Override
            public CDataEntity getTransID() { return tid; }
            @Override
            public CDataEntity getCommArea() { return comm; }
            @Override
            public CDataEntity getCommLength() { return len; }
            @Override
            public boolean isChecked() { return checked; }
            @Override
            public String getTransIDConstantValue() { return constValue; }
        };
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(ret, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare EXEC CICS RETURN renders CESM.returnTrans() ;")
    void bareReturn()
    {
        String output = render(null, null, null, false, null);
        assertTrue(output.contains("CESM.returnTrans() ;"), output);
    }

    @Test
    @DisplayName("RETURN TRANSID(ref) renders CESM.returnTrans(<ref>) ;")
    void returnWithTransID()
    {
        String output = render(new MockDataEntity(2, "MYTRANS"), null, null, false, null);
        assertTrue(output.contains("CESM.returnTrans(MYTRANS) ;"), output);
    }

    @Test
    @DisplayName("checked RETURN TRANSID renders the program class literal <const>.class")
    void returnWithCheckedTransID()
    {
        String output = render(new MockDataEntity(2, "ignored"), null, null, true, "TRA1");
        assertTrue(output.contains("CESM.returnTrans(TRA1.class) ;"), output);
    }

    @Test
    @DisplayName("RETURN TRANSID + COMMAREA renders CESM.returnTrans(<ref>, <commArea>) ;")
    void returnWithCommArea()
    {
        String output = render(new MockDataEntity(2, "MYTRANS"),
            new MockDataEntity(3, "ONLINEFS"), null, false, null);
        assertTrue(output.contains("CESM.returnTrans(MYTRANS, ONLINEFS) ;"), output);
    }

    @Test
    @DisplayName("RETURN TRANSID + COMMAREA + LENGTH renders all three reference args")
    void returnWithCommAreaAndLength()
    {
        String output = render(new MockDataEntity(2, "MYTRANS"),
            new MockDataEntity(3, "ONLINEFS"), new MockDataEntity(4, "WS-LEN"), false, null);
        assertTrue(output.contains("CESM.returnTrans(MYTRANS, ONLINEFS, WS-LEN) ;"), output);
    }
}
