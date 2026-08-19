package com.publicitas.naca.cloudnative.carddemo.cics;

/** Runtime port for CICS keyed record access. */
@FunctionalInterface
public interface CicsRecordStore
{
    /** Reads one fixed record and returns CICS-compatible response codes. */
    CicsRecordReadResult read(CicsRecordReadRequest request);
}
