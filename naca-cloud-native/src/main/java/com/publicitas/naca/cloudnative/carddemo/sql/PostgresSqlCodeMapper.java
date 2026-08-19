package com.publicitas.naca.cloudnative.carddemo.sql;

import java.sql.SQLException;
import nacaLib.sqlSupport.SQLCode;
import org.springframework.stereotype.Component;

/** Maps PostgreSQL SQLSTATE values to DB2-compatible SQLCODE values observed by COBOL. */
@Component
public class PostgresSqlCodeMapper
{
    public static final int FOREIGN_KEY_VIOLATION = -530;
    public static final int NOT_NULL_VIOLATION = -407;
    public static final int TRANSACTION_ROLLBACK = -911;

    /** Returns a stable DB2-compatible SQLCODE for a JDBC/Spring exception chain. */
    public int map(Throwable failure)
    {
        SQLException sqlException = findSqlException(failure);
        if (sqlException == null || sqlException.getSQLState() == null)
        {
            return SQLCode.SQL_ERROR;
        }
        return switch (sqlException.getSQLState())
        {
            case "23505" -> SQLCode.SQL_DUPLICATE_INDEX_KEY;
            case "23503" -> FOREIGN_KEY_VIOLATION;
            case "23502" -> NOT_NULL_VIOLATION;
            case "40001", "40P01" -> TRANSACTION_ROLLBACK;
            default -> SQLCode.SQL_ERROR;
        };
    }

    private static SQLException findSqlException(Throwable failure)
    {
        Throwable current = failure;
        while (current != null)
        {
            if (current instanceof SQLException sqlException)
            {
                return sqlException;
            }
            current = current.getCause();
        }
        return null;
    }
}
