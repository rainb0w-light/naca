package com.publicitas.naca.cloudnative.carddemo.cics;

import java.util.List;
import nacaLib.CESM.CESMReturnCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** PostgreSQL implementation of keyed CICS fixed-record reads. */
@Component
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
public class PostgresCicsRecordStore implements CicsRecordStore
{
    private static final int NO_RESP2 = 0;
    private final JdbcTemplate jdbcTemplate;

    /** Creates the record store over the transaction-aware Spring JDBC template. */
    public PostgresCicsRecordStore(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CicsRecordReadResult read(CicsRecordReadRequest request)
    {
        String lockClause = request.forUpdate() ? " for update of records" : "";
        List<CicsRecordReadResult> matches = jdbcTemplate.query("""
            select records.record_data, records.record_length, records.version
              from carddemo_vsam.record_store records
              join carddemo_vsam.file_manifest manifest
                on manifest.file_name = records.file_name
             where records.file_name = ?
               and records.primary_key = ?
            """ + lockClause,
            (resultSet, rowNumber) -> successfulRead(
                resultSet.getBytes("record_data"),
                resultSet.getInt("record_length"),
                resultSet.getLong("version"),
                request.maximumLength()),
            request.fileName(), request.key());
        if (matches.isEmpty())
        {
            return new CicsRecordReadResult(new byte[0], 0, 0,
                CESMReturnCode.NOT_FOUND.getCondition(), NO_RESP2);
        }
        return matches.getFirst();
    }

    private static CicsRecordReadResult successfulRead(
        byte[] record, int actualLength, long version, int maximumLength)
    {
        if (actualLength > maximumLength)
        {
            return new CicsRecordReadResult(new byte[0], actualLength, version,
                CESMReturnCode.LENGERR.getCondition(), NO_RESP2);
        }
        return new CicsRecordReadResult(record, actualLength, version,
            CESMReturnCode.NORMAL.getCondition(), NO_RESP2);
    }
}
