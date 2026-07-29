package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSReadQ;

class CICSReadQRenderTest
{
    private static String render(CEntityCICSReadQ read)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(read, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    @DisplayName("READQ TS NEXT INTO LENGTH NUMITEMS renders the complete fluent chain")
    void tempQueueNext()
    {
        CEntityCICSReadQ read = new CEntityCICSReadQ(1, null, false);
        read.SetName(new MockDataEntity(2, "\"QUEUE-A\""));
        read.SetDataRef(new MockDataEntity(3, "TARGET"), new MockDataEntity(4, "LENGTH-VAR"));
        read.ReadNext();
        read.ReadNumItem(new MockDataEntity(5, "ITEM-COUNT"));
        assertEquals(
            "CESM.readTempQueue(\"QUEUE-A\").nextInto(TARGET, LENGTH-VAR)"
                + ".numItem(ITEM-COUNT) ;",
            render(read));
    }

    @Test
    @DisplayName("READQ TD ITEM INTO LENGTH renders the transient indexed form")
    void transientQueueItem()
    {
        CEntityCICSReadQ read = new CEntityCICSReadQ(1, null, true);
        read.SetName(new MockDataEntity(2, "QUEUE-NAME"));
        read.SetDataRef(new MockDataEntity(3, "TARGET"), new MockDataEntity(4, "LENGTH-VAR"));
        read.ReadItem(new MockDataEntity(5, "ITEM-NUMBER"));
        assertEquals(
            "CESM.readTransiantQueue(QUEUE-NAME)"
                + ".itemInto(ITEM-NUMBER, TARGET, LENGTH-VAR) ;",
            render(read));
    }

    @Test
    @DisplayName("READQ TS NUMITEMS is valid without INTO")
    void queueCountOnly()
    {
        CEntityCICSReadQ read = new CEntityCICSReadQ(1, null, false);
        read.SetName(new MockDataEntity(2, "QUEUE-NAME"));
        read.ReadNumItem(new MockDataEntity(3, "ITEM-COUNT"));
        assertEquals(
            "CESM.readTempQueue(QUEUE-NAME).numItem(ITEM-COUNT) ;",
            render(read));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure READQ semantic entity")
    void factoryReturnsSemanticEntity()
    {
        assertInstanceOf(CEntityCICSReadQ.class,
            new CJavaEntityFactoryST(null, null).NewEntityCICSReadQ(1, false));
    }
}
