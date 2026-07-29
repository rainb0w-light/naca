package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSSendMap;

/**
 * Embedded CICS SEND MAP rendering through the recursive assembler (the production
 * path). CICS SEND MAP is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSSendMapEntity} binding; this test drives it directly with
 * lightweight mocks and asserts byte-for-byte parity with the output shape the
 * retired {@code CJavaCICSSendMap.DoExport} direct backend produced:
 * <ul>
 *   <li>MAP/MAPSET/FROM -&gt; {@code CESM.sendMap(<map>).mapSet(<set>).dataFrom(<from>) ;}</li>
 *   <li>DATAONLY selects {@code .dataOnlyFrom(<from>)}, LENGTH appends {@code , <len>}</li>
 *   <li>CURSOR chains {@code .cursor()} (bare) or {@code .cursor(<ref>)}</li>
 *   <li>ACCUM/ALARM/ERASE/FREEKB/PAGING/WAIT each chain their no-arg builder, in that order</li>
 * </ul>
 * The production-reachable fixtures are ONLINE1.cbl:228
 * ({@code MAP('ONLINEF') MAPSET('ONLINE1') FROM(ONLINEFI) FREEKB CURSOR ERASE}) and
 * ONLINE1.cbl:237 (same but {@code DATAONLY} instead of {@code ERASE}).
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSSendMap} direct backend.
 */
class CICSSendMapRenderTest
{
    private static String render(CEntityCICSSendMap send)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(send, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("fixture ONLINE1.cbl:228 FROM/FREEKB/CURSOR/ERASE renders the dataFrom form")
    void sendMapFixture228()
    {
        CEntityCICSSendMap send = new CEntityCICSSendMap(1, null);
        send.SetName(new MockDataEntity(2, "\"ONLINEF\""));
        send.SetMapSet(new MockDataEntity(3, "\"ONLINE1\""));
        send.SetDataFrom(new MockDataEntity(4, "ONLINEFI"), null, false);
        send.SetFreeKB(true);
        send.SetCursor(null);
        send.SetErase(true);
        String output = render(send);
        assertTrue(output.contains(
            "CESM.sendMap(\"ONLINEF\").mapSet(\"ONLINE1\").dataFrom(ONLINEFI).cursor().erase().freeKB() ;"),
            output);
    }

    @Test
    @DisplayName("fixture ONLINE1.cbl:237 FROM/FREEKB/CURSOR/DATAONLY renders the dataOnlyFrom form")
    void sendMapFixture237()
    {
        CEntityCICSSendMap send = new CEntityCICSSendMap(1, null);
        send.SetName(new MockDataEntity(2, "\"ONLINEF\""));
        send.SetMapSet(new MockDataEntity(3, "\"ONLINE1\""));
        send.SetDataFrom(new MockDataEntity(4, "ONLINEFI"), null, true);
        send.SetFreeKB(true);
        send.SetCursor(null);
        String output = render(send);
        assertTrue(output.contains(
            "CESM.sendMap(\"ONLINEF\").mapSet(\"ONLINE1\").dataOnlyFrom(ONLINEFI).cursor().freeKB() ;"),
            output);
    }

    @Test
    @DisplayName("full option chain renders every SEND MAP option in order with a compilable WAIT adapter")
    void sendMapFullOptionOrdering()
    {
        CEntityCICSSendMap send = new CEntityCICSSendMap(1, null);
        send.SetName(new MockDataEntity(2, "MAPNAME"));
        send.SetMapSet(new MockDataEntity(3, "SETNAME"));
        send.SetDataFrom(new MockDataEntity(4, "FROMVAR"), new MockDataEntity(5, "LENVAR"), false);
        send.SetCursor(new MockDataEntity(6, "CURVAR"));
        send.SetAccum(true);
        send.SetAlarm(true);
        send.SetErase(true);
        send.SetFreeKB(true);
        send.SetPaging(true);
        send.SetWait(true);
        String output = render(send);
        assertTrue(output.contains(
            "CESM.sendMap(MAPNAME).mapSet(SETNAME).dataFrom(FROMVAR, LENVAR).cursor(CURVAR)"
                + ".accum().alarm().erase().freeKB().paging().waitForCompletion() ;"),
            output);
    }

    @Test
    @DisplayName("bare SEND MAP (name only) renders CESM.sendMap(<map>) ;")
    void sendMapBare()
    {
        CEntityCICSSendMap send = new CEntityCICSSendMap(1, null);
        send.SetName(new MockDataEntity(2, "MAPNAME"));
        String output = render(send);
        assertTrue(output.contains("CESM.sendMap(MAPNAME) ;"), output);
    }
}
