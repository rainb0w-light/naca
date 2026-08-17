package nacaLib.varEx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import jlib.misc.LineRead;
import jlib.misc.LogicalFileDescriptor;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.batchPrgEnv.BatchSession;
import jlib.xml.Tag;
import org.junit.jupiter.api.Test;

class FileDescriptorStatusTest
{
    @Test
    void sequentialInputUpdatesStatusAndReadsRealRecords() throws Exception
    {
        Path input = Files.createTempFile("file-status", ".dat");
        Files.writeString(input, "FIRST\nSECOND\n");
        try
        {
            Lifecycle.run(input);
        }
        finally
        {
            Files.deleteIfExists(input);
        }
    }

    private static final class Lifecycle
    {
        static void run(Path input) throws Exception
        {
            BaseSession session = new BatchSession(new TestResourceManager());
            session.putLogicalFileDescriptor("CARD",
                new LogicalFileDescriptor("CARD", input + ",ascii,fb,5"));
            TrackingStatus status = new TrackingStatus();
            TrackingRecord record = new TrackingRecord();
            FileDescriptor descriptor = new FileDescriptor("CARD", session)
                .status(status);
            descriptor.setRecordStruct(record);
            assertEquals(descriptor, descriptor.openInput());
            assertEquals("00", status.value);
            assertEquals(RecordDescriptorAtEnd.NotEnd, descriptor.read());
            assertEquals("00", status.value);
            assertEquals("FIRST", record.value);
            assertEquals(RecordDescriptorAtEnd.NotEnd, descriptor.read());
            assertEquals("00", status.value);
            assertEquals("SECOND", record.value);
            assertEquals(RecordDescriptorAtEnd.End, descriptor.read());
            assertEquals("10", status.value);
            descriptor.close();
            assertEquals("00", status.value);
        }
    }

    private static final class TrackingStatus extends VarGroup
    {
        private String value;

        @Override
        public void set(String value)
        {
            this.value = value;
        }
    }

    private static final class TrackingRecord extends VarGroup
    {
        private String value;

        @Override
        public int getTotalSize()
        {
            return 5;
        }

        @Override
        public int setFromLineRead(LineRead lineRead)
        {
            value = lineRead.getChunkAsString();
            return 5;
        }
    }

    private static final class TestResourceManager extends BaseResourceManager
    {
        private TestResourceManager()
        {
            super(false);
        }

        @Override protected void LoadConfigFromFile(Tag root) { }
        @Override protected void initSequenceur(String prefix) { }
        @Override public void doRemoveResourceCache(String form) { }
    }

}
