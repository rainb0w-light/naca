package com.publicitas.naca.cloudnative.carddemo.cics;

import java.util.Arrays;

/** Result of one CICS READ, including COBOL-visible RESP and RESP2 values. */
public record CicsRecordReadResult(
    byte[] record, int actualLength, long version, int response, int response2)
{
    /** Defensively copies record bytes returned from JDBC. */
    public CicsRecordReadResult
    {
        record = Arrays.copyOf(record, record.length);
    }

    @Override
    public byte[] record()
    {
        return Arrays.copyOf(record, record.length);
    }

    /** Returns whether CICS completed the operation normally. */
    public boolean isNormal()
    {
        return response == 0;
    }
}
