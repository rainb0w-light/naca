package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSSetTDQueue;

class CICSSetTDQueueRenderTest
{
    private static String render(CEntityCICSSetTDQueue set)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(set, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    @DisplayName("SET TDQUEUE OPEN retains its state and renders a literal queue")
    void openLiteralQueue()
    {
        CEntityCICSSetTDQueue set = new CEntityCICSSetTDQueue(1, null);
        set.SetQueue(new MockDataEntity(2, "\"QUEUE-A\""));
        set.SetOpen(true);

        assertTrue(set.isOpen());
        assertFalse(set.isClosed());
        assertEquals("CESM.setTDQueueOpen(\"QUEUE-A\") ;", render(set));
    }

    @Test
    @DisplayName("SET TDQUEUE CLOSED renders a variable queue")
    void closedVariableQueue()
    {
        CEntityCICSSetTDQueue set = new CEntityCICSSetTDQueue(1, null);
        set.SetQueue(new MockDataEntity(2, "QUEUE-NAME"));
        set.SetOpen(false);

        assertFalse(set.isOpen());
        assertTrue(set.isClosed());
        assertEquals("CESM.setTDQueueClosed(QUEUE-NAME) ;", render(set));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure SET TDQUEUE semantic entity")
    void factoryReturnsSemanticEntity()
    {
        assertInstanceOf(CEntityCICSSetTDQueue.class,
            new CJavaEntityFactory(null, null).NewEntityCICSSetTDQueue(1));
    }
}
