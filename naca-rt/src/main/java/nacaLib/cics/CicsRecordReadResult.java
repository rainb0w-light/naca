package nacaLib.cics;

import java.util.Arrays;

/** Result of one CICS read, including COBOL-visible RESP and RESP2 values. */
public record CicsRecordReadResult(
    byte[] record, int actualLength, long version, int response, int response2)
{
    /** Defensively copies record bytes returned by the storage backend. */
    public CicsRecordReadResult
    {
        record = Arrays.copyOf(record, record.length);
    }

    @Override
    public byte[] record()
    {
        return Arrays.copyOf(record, record.length);
    }

    /** Returns whether the operation completed normally. */
    public boolean isNormal()
    {
        return response == 0;
    }
}
