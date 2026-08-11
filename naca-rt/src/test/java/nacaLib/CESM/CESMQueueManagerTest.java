package nacaLib.CESM;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nacaLib.varEx.InternalCharBuffer;
import org.junit.jupiter.api.Test;

class CESMQueueManagerTest
{
    private final CESMQueueManager manager = new CESMQueueManager(null);
    private final InternalCharBuffer record = new InternalCharBuffer(new char[] {'A'});

    @Test
    void temporaryAndTransientQueuesHaveIndependentStorage()
    {
        assertEquals(1, manager.writeQueue(false, "QUEUE-A", record));
        assertEquals(1, manager.writeQueue(true, "QUEUE-A", record));
        assertEquals(2, manager.writeQueue(false, "QUEUE-A", record));
        assertEquals(2, manager.writeQueue(true, "QUEUE-A", record));
    }

    @Test
    void closedTransientQueueRejectsWritesUntilReopened()
    {
        manager.setTransientQueueOpen("QUEUE-A", false);
        assertEquals(0, manager.writeQueue(true, "QUEUE-A", record));

        manager.setTransientQueueOpen("QUEUE-A", true);
        assertEquals(1, manager.writeQueue(true, "QUEUE-A", record));
    }

    @Test
    void deletingATransientQueueDoesNotDeleteTemporaryStorageWithTheSameName()
    {
        assertEquals(1, manager.writeQueue(false, "QUEUE-A", record));
        assertEquals(1, manager.writeQueue(true, "QUEUE-A", record));

        manager.deleteQueue(true, "QUEUE-A");

        assertEquals(2, manager.writeQueue(false, "QUEUE-A", record));
        assertEquals(1, manager.writeQueue(true, "QUEUE-A", record));
    }
}
