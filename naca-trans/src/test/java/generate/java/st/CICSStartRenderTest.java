package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSStart;

class CICSStartRenderTest
{
    private static String render(CEntityCICSStart start)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(start, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void startWithIntervalAndData()
    {
        CEntityCICSStart start =
            new CEntityCICSStart(1, null, new MockDataEntity(2, "TRANS-ID"));
        start.SetInterval(new MockDataEntity(3, "DELAY"));
        start.SetTermID(new MockDataEntity(4, "TERM-ID"));
        start.SetSysID(new MockDataEntity(5, "\"SYS1\""));
        start.SetDataFrom(new MockDataEntity(6, "PAYLOAD"),
            new MockDataEntity(7, "PAYLOAD-LEN"));

        assertEquals("CESM.start(TRANS-ID).interval(DELAY).termID(TERM-ID)"
            + ".sysID(\"SYS1\").dataFrom(PAYLOAD, PAYLOAD-LEN).doStart() ;",
            render(start));
    }

    @Test
    void verifiedStartRetainsTime()
    {
        CEntityCICSStart start =
            new CEntityCICSStart(1, null, new MockDataEntity(2, "ONLINE1"));
        MockDataEntity time = new MockDataEntity(3, "START-TIME");
        start.setVerified(true);
        start.SetTime(time);

        assertSame(time, start.getTime());
        assertEquals("CESM.start(ONLINE1.class).time(START-TIME).doStart() ;",
            render(start));
    }

    @Test
    void factoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityCICSStart.class,
            new CJavaEntityFactoryST(null, null)
                .NewEntityCICSStart(1, new MockDataEntity(2, "TRANS-ID")));
    }
}
