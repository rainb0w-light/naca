package com.publicitas.naca.cloudnative.carddemo.cics;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/** One keyed CICS READ request against the PostgreSQL record store. */
public record CicsRecordReadRequest(
    String fileName, byte[] key, int maximumLength, boolean forUpdate)
{
    /** Validates and defensively copies the request key. */
    public CicsRecordReadRequest
    {
        fileName = Objects.requireNonNull(fileName, "fileName").trim().toUpperCase(Locale.ROOT);
        key = Arrays.copyOf(Objects.requireNonNull(key, "key"), key.length);
        if (fileName.isEmpty() || key.length == 0 || maximumLength <= 0)
        {
            throw new IllegalArgumentException("CICS READ requires file, key and positive length");
        }
    }

    @Override
    public byte[] key()
    {
        return Arrays.copyOf(key, key.length);
    }
}
