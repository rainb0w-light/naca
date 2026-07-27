package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSReceiveMap;

/**
 * Embedded CICS RECEIVE MAP rendering through the recursive assembler (the
 * production path). CICS RECEIVE MAP is rendered as a REFERENCE-role executable
 * child via the {@code recursiveCICSReceiveMapEntity} binding; this test drives
 * it directly with lightweight mocks and asserts byte-for-byte parity with the
 * output shape the retired {@code CJavaCICSReceiveMap.DoExport} direct backend
 * produced:
 * <ul>
 *   <li>MAP/MAPSET/INTO -&gt; {@code CESM.receiveMap(<map>).mapSet(<set>).into(<into>) ;}</li>
 * </ul>
 * The production-reachable fixture ONLINE1.cbl:163 is
 * {@code MAP('ONLINEF') MAPSET('ONLINE1') INTO(ONLINEFI)}, which renders
 * {@code CESM.receiveMap("ONLINEF").mapSet("ONLINE1").into(ONLINEFI) ;}.
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSReceiveMap} direct backend.
 */
class CICSReceiveMapRenderTest
{
    private static String render(CDataEntity map, CDataEntity mapSet, CDataEntity into)
    {
        CEntityCICSReceiveMap recv = new CEntityCICSReceiveMap(1, null, map);
        recv.SetMapSet(mapSet);
        recv.SetDataInto(into);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(recv, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("RECEIVE MAP/MAPSET/INTO renders CESM.receiveMap(<map>).mapSet(<set>).into(<into>) ;")
    void receiveMapFull()
    {
        String output = render(new MockDataEntity(2, "ONLINEF"),
            new MockDataEntity(3, "ONLINE1"), new MockDataEntity(4, "ONLINEFI"));
        assertTrue(output.contains("CESM.receiveMap(ONLINEF).mapSet(ONLINE1).into(ONLINEFI) ;"),
            output);
    }

    @Test
    @DisplayName("fixture ONLINE1.cbl:163 literal form renders the quoted map/mapset literals")
    void receiveMapFixtureLiteralParity()
    {
        String output = render(new MockDataEntity(2, "\"ONLINEF\""),
            new MockDataEntity(3, "\"ONLINE1\""), new MockDataEntity(4, "ONLINEFI"));
        assertTrue(output.contains("CESM.receiveMap(\"ONLINEF\").mapSet(\"ONLINE1\").into(ONLINEFI) ;"),
            output);
    }
}
