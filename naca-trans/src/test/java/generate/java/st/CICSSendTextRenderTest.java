package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSSendText;

class CICSSendTextRenderTest
{
    @Test
    void rendersEveryCardDemoTextOption()
    {
        CEntityCICSSendText send = new CEntityCICSSendText(1, null);
        send.setDataFrom(new MockDataEntity(2, "WS-MESSAGE"),
            new MockDataEntity(3, "WS-LENGTH"));
        send.setFlags(true, true);
        send.setResponses(new MockDataEntity(4, "WS-RESP"),
            new MockDataEntity(5, "WS-RESP2"));

        assertEquals("CESM.sendText(WS-MESSAGE).length(WS-LENGTH).erase().freeKB()"
                + ".resp(WS-RESP).resp2(WS-RESP2) ;",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(send, JavaTemplateRole.REFERENCE).trim());
    }

    @Test
    void factoryReturnsSemanticEntity()
    {
        assertInstanceOf(CEntityCICSSendText.class,
            new CJavaEntityFactory(null, null).NewEntityCICSSendText(1));
    }
}
