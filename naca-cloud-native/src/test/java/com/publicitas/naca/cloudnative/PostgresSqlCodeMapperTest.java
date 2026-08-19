package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.publicitas.naca.cloudnative.carddemo.sql.PostgresSqlCodeMapper;
import java.sql.SQLException;
import java.util.stream.Stream;
import nacaLib.sqlSupport.SQLCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;

/** Locks the PostgreSQL SQLSTATE to COBOL/DB2 SQLCODE contract without Docker. */
class PostgresSqlCodeMapperTest
{
    private final PostgresSqlCodeMapper mapper = new PostgresSqlCodeMapper();

    @ParameterizedTest(name = "SQLSTATE {0} maps to SQLCODE {1}")
    @MethodSource("sqlStateMappings")
    void mapsPostgresSqlState(String sqlState, int expectedSqlCode)
    {
        SQLException sql = new SQLException("probe", sqlState);
        int actual = mapper.map(new DataIntegrityViolationException("wrapped", sql));
        assertEquals(expectedSqlCode, actual, "Unexpected SQLCODE for SQLSTATE " + sqlState);
    }

    @Test
    void nonJdbcFailureFailsClosed()
    {
        assertEquals(SQLCode.SQL_ERROR, mapper.map(new IllegalStateException("not JDBC")),
            "A non-JDBC failure must not appear successful");
    }

    private static Stream<Arguments> sqlStateMappings()
    {
        return Stream.of(
            Arguments.of("23505", SQLCode.SQL_DUPLICATE_INDEX_KEY),
            Arguments.of("23503", PostgresSqlCodeMapper.FOREIGN_KEY_VIOLATION),
            Arguments.of("23502", PostgresSqlCodeMapper.NOT_NULL_VIOLATION),
            Arguments.of("40001", PostgresSqlCodeMapper.TRANSACTION_ROLLBACK),
            Arguments.of("40P01", PostgresSqlCodeMapper.TRANSACTION_ROLLBACK),
            Arguments.of("42601", SQLCode.SQL_ERROR));
    }
}
