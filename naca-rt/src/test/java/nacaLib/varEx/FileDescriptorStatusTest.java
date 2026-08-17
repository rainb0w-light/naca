package nacaLib.varEx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
            assertDoesNotThrow(() -> Lifecycle.run(input),
                "sequential input lifecycle should complete without errors");
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
            BaseSession session = new BatchSession(new FixtureResourceManager());
            session.putLogicalFileDescriptor("CARD",
                new LogicalFileDescriptor("CARD", input + ",ascii,fb,5"));
            TrackingStatus status = new TrackingStatus();
            TrackingRecord record = new TrackingRecord();
            FileDescriptor descriptor = new FileDescriptor("CARD", session)
                .status(status);
            descriptor.setRecordStruct(record);
            assertEquals(descriptor, descriptor.openInput(), "OPEN INPUT should return descriptor");
            assertEquals("00", status.value, "successful OPEN INPUT should set status 00");
            assertEquals(RecordDescriptorAtEnd.NotEnd, descriptor.read(),
                "first READ should return a record");
            assertEquals("00", status.value, "successful first READ should set status 00");
            assertEquals("FIRST", record.value, "first READ should capture FIRST");
            assertEquals(RecordDescriptorAtEnd.NotEnd, descriptor.read(),
                "second READ should return a record");
            assertEquals("00", status.value, "successful second READ should set status 00");
            assertEquals("SECOND", record.value, "second READ should capture SECOND");
            assertEquals(RecordDescriptorAtEnd.End, descriptor.read(),
                "READ after the final record should return EOF");
            assertEquals("10", status.value, "EOF READ should set status 10");
            descriptor.close();
            assertEquals("00", status.value, "successful CLOSE should set status 00");
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

    private static final class FixtureResourceManager extends BaseResourceManager
    {
        private FixtureResourceManager()
        {
            super(false);
        }

        @Override protected void LoadConfigFromFile(Tag root) { }
        @Override protected void initSequenceur(String prefix) { }
        @Override public void doRemoveResourceCache(String form) { }
    }

}
