package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSAddress;

/**
 * Embedded CICS ADDRESS rendering through the recursive assembler (the production
 * path). CICS ADDRESS is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSAddressEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shapes the
 * retired {@code CJavaCICSAddress.DoExport} direct backend produced:
 * <ul>
 *   <li>TCTUA(ref) -&gt; {@code CESM.getAddressOfTCTUA(<ref>) ;} (the
 *       production-reachable ONLINE1.cbl:110 form);</li>
 *   <li>CWA(ref) -&gt; {@code CESM.getAddressOfCWA(<ref>) ;}</li>
 *   <li>TWA(ref) -&gt; {@code CESM.getAddressOfTWA(<ref>) ;}</li>
 *   <li>multiple references chain on one CESM statement, e.g.
 *       {@code CESM.getAddressOfCWA(<cwa>).getAddressOfTCTUA(<tctua>) ;};</li>
 *   <li>no active reference -&gt; renders nothing (the entity is ignored).</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSAddress} direct backend.
 */
class CICSAddressRenderTest
{
    private static CEntityCICSAddress address(CDataEntity cwa, CDataEntity tctua,
        CDataEntity twa)
    {
        CEntityCICSAddress address = new CEntityCICSAddress(1, null);
        address.SetRefForCWA(cwa);
        address.SetRefForTCTUA(tctua);
        address.SetRefForTWA(twa);
        return address;
    }

    private static String render(CEntityCICSAddress address)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(address, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("ADDRESS TCTUA(ref) renders CESM.getAddressOfTCTUA(<ref>) ;")
    void addressWithTCTUA()
    {
        String output = render(address(null, new MockDataEntity(2, "TUA-ZONE"), null));
        assertTrue(output.contains("CESM.getAddressOfTCTUA(TUA-ZONE) ;"), output);
    }

    @Test
    @DisplayName("ADDRESS CWA(ref) renders CESM.getAddressOfCWA(<ref>) ;")
    void addressWithCWA()
    {
        String output = render(address(new MockDataEntity(2, "W-CWA"), null, null));
        assertTrue(output.contains("CESM.getAddressOfCWA(W-CWA) ;"), output);
    }

    @Test
    @DisplayName("ADDRESS TWA(ref) renders CESM.getAddressOfTWA(<ref>) ;")
    void addressWithTWA()
    {
        String output = render(address(null, null, new MockDataEntity(2, "W-TWA")));
        assertTrue(output.contains("CESM.getAddressOfTWA(W-TWA) ;"), output);
    }

    @Test
    @DisplayName("ADDRESS with several references chains them on one CESM statement")
    void addressWithChainedReferences()
    {
        String output = render(address(new MockDataEntity(2, "W-CWA"),
            new MockDataEntity(3, "TUA-ZONE"), null));
        assertTrue(output.contains("CESM.getAddressOfCWA(W-CWA).getAddressOfTCTUA(TUA-ZONE) ;"),
            output);
    }

    @Test
    @DisplayName("ADDRESS with no active reference renders no CESM call")
    void addressWithNoReference()
    {
        CEntityCICSAddress address = address(null, null, null);
        assertTrue(address.ignore(), "an ADDRESS with no reference must be ignored");
        String output = render(address);
        assertFalse(output.contains("CESM"), output);
    }
}
