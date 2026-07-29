package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSWriteQ;

class CICSWriteQRenderTest
{
    private static String render(CEntityCICSWriteQ write)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(write, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void tempQueueRetainsOutputAndFacilityOptions()
    {
        CEntityCICSWriteQ write = new CEntityCICSWriteQ(1, null, false);
        write.SetName(new MockDataEntity(2, "QUEUE-NAME"));
        write.SetDataRef(new MockDataEntity(3, "PAYLOAD"),
            new MockDataEntity(4, "PAYLOAD-LEN"));
        write.WriteItem(new MockDataEntity(5, "ITEM-NUMBER"));
        write.WriteNumItem(new MockDataEntity(6, "ITEM-COUNT"));
        write.SetMain();
        write.SetSysID(new MockDataEntity(7, "\"SYS1\""));

        assertEquals("CESM.writeTempQueue(QUEUE-NAME)"
            + ".from(PAYLOAD, PAYLOAD-LEN).item(ITEM-NUMBER)"
            + ".numItem(ITEM-COUNT).main().sysID(\"SYS1\") ;", render(write));
    }

    @Test
    void transientRewriteRetainsPersistentMode()
    {
        CEntityCICSWriteQ write = new CEntityCICSWriteQ(1, null, true);
        write.SetName(new MockDataEntity(2, "\"QUEUE-A\""));
        write.SetDataRef(new MockDataEntity(3, "PAYLOAD"), null);
        write.WriteItem(new MockDataEntity(4, "REWRITE-ITEM"));
        write.SetRewrite();
        write.SetAuxiliary();

        assertTrue(write.isPersistent());
        assertEquals("CESM.writeTransiantQueue(\"QUEUE-A\", REWRITE-ITEM)"
            + ".from(PAYLOAD).auxiliary() ;", render(write));
    }

    @Test
    void factoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityCICSWriteQ.class,
            new CJavaEntityFactoryST(null, null).NewEntityCICSWriteQ(1, true));
    }
}
