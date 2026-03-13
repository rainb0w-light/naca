/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sql.dsl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.sqlSupport.CSQLStatus;
import nacaLib.sqlSupport.SQLCode;
import nacaLib.varEx.VarAndEdit;

/**
 * Cursor management for COBOL to Java translated programs.
 * Supports COBOL-style cursor operations: DECLARE, OPEN, FETCH, CLOSE.
 *
 * Usage example:
 * <pre>
 * // Equivalent to:
 * // EXEC SQL DECLARE C1 CURSOR FOR
 * //   SELECT NOM, PRENOM FROM VIT101 WHERE ID > :ws-id
 * // END-EXEC
 * CobolCursor cursor = new CobolCursor(sqlTemplate, "C1");
 *
 * // EXEC SQL OPEN C1 END-EXEC
 * cursor.open("SELECT NOM, PRENOM FROM VIT101 WHERE ID > ?", wsId.get());
 *
 * // EXEC SQL FETCH C1 INTO :ws-nom, :ws-prenom END-EXEC
 * while (cursor.fetch(wsNom, wsPrenom)) {
 *     // Process row
 * }
 *
 * // EXEC SQL CLOSE C1 END-EXEC
 * cursor.close();
 * </pre>
 */
public class CobolCursor {

    private static final Logger logger = LoggerFactory.getLogger(CobolCursor.class);

    private final String cursorName;
    private final CobolSqlTemplate sqlTemplate;
    private final SqlErrorContext errorContext;
    private final BaseProgramManager programManager;

    private ResultSet resultSet;
    private List<Object> currentRow;
    private boolean isOpen = false;
    private int sqlCode = SQLCode.SQL_OK;
    private int fetchCount = 0;

    /**
     * Constructor.
     * @param sqlTemplate the SQL template to use
     * @param cursorName the cursor name
     */
    public CobolCursor(CobolSqlTemplate sqlTemplate, String cursorName) {
        this.sqlTemplate = sqlTemplate;
        this.cursorName = cursorName;
        this.programManager = sqlTemplate.getProgramManager();
        this.errorContext = sqlTemplate.getErrorContext();
    }

    /**
     * Open the cursor with a query and parameters.
     * Equivalent to: EXEC SQL OPEN cursor-name END-EXEC
     *
     * @param sql the SELECT query with ? placeholders
     * @param params the parameters to bind
     * @return CSQLStatus the SQL execution status
     */
    public CSQLStatus open(String sql, Object... params) {
        CSQLStatus sqlStatus = programManager.getSQLStatus();

        if (isOpen) {
            sqlStatus.setSQLCode(SQLCode.SQL_CURSOR_ALREADY_OPENED);
            return sqlStatus;
        }

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Opening cursor {} with query: {} and params: {}",
                    cursorName, sql, params);
            }

            JdbcTemplate jdbcTemplate = sqlTemplate.getJdbcTemplate();

            // Use Connection callback to get a ResultSet that we can iterate
            resultSet = jdbcTemplate.execute((Connection conn) -> {
                var ps = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
                return ps.executeQuery();
            });

            isOpen = true;
            fetchCount = 0;
            sqlStatus.setSQLCode(SQLCode.SQL_OK);

        } catch (Exception e) {
            logger.error("Failed to open cursor {}: {}", cursorName, e.getMessage(), e);
            sqlStatus.setSQLCode(SQLCode.SQL_ERROR);
            sqlCode = SQLCode.SQL_ERROR;
            errorContext.handleError(e, programManager);
        }

        return sqlStatus;
    }

    /**
     * Fetch the next row into host variables.
     * Equivalent to: EXEC SQL FETCH cursor-name INTO :host-var END-EXEC
     *
     * @param hostVars the host variables to populate
     * @return true if a row was fetched, false if no more rows
     */
    public boolean fetch(VarAndEdit... hostVars) {
        CSQLStatus sqlStatus = programManager.getSQLStatus();

        if (!isOpen || resultSet == null) {
            sqlStatus.setSQLCode(SQLCode.SQL_CURSOR_NOT_OPEN);
            return false;
        }

        try {
            if (resultSet.next()) {
                fetchCount++;
                currentRow = new ArrayList<>();

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount && i <= hostVars.length; i++) {
                    Object value = resultSet.getObject(i);
                    currentRow.add(value);

                    // Set the value to the host variable
                    if (value != null) {
                        hostVars[i - 1].set(value.toString());
                    } else {
                        hostVars[i - 1].set("");
                    }
                }

                sqlStatus.setSQLCode(SQLCode.SQL_OK);
                sqlCode = SQLCode.SQL_OK;
                return true;
            } else {
                // No more rows
                sqlStatus.setSQLCode(SQLCode.SQL_NO_DATA);
                sqlCode = SQLCode.SQL_NO_DATA;
                return false;
            }

        } catch (SQLException e) {
            logger.error("Fetch failed on cursor {}: {}", cursorName, e.getMessage(), e);
            sqlStatus.setSQLCode(SQLCode.SQL_ERROR);
            sqlCode = SQLCode.SQL_ERROR;
            errorContext.handleError(e, programManager);
            return false;
        }
    }

    /**
     * Fetch the next row and return values as a list.
     * @return List of column values, or null if no more rows
     */
    public List<Object> fetchRow() {
        if (!isOpen || resultSet == null) {
            return null;
        }

        try {
            if (resultSet.next()) {
                fetchCount++;
                currentRow = new ArrayList<>();

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    currentRow.add(resultSet.getObject(i));
                }

                return currentRow;
            } else {
                return null;
            }

        } catch (SQLException e) {
            logger.error("Fetch failed on cursor {}: {}", cursorName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Close the cursor.
     * Equivalent to: EXEC SQL CLOSE cursor-name END-EXEC
     *
     * @return CSQLStatus the SQL execution status
     */
    public CSQLStatus close() {
        CSQLStatus sqlStatus = programManager.getSQLStatus();

        if (!isOpen) {
            sqlStatus.setSQLCode(SQLCode.SQL_CURSOR_NOT_OPEN);
            return sqlStatus;
        }

        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
            isOpen = false;
            resultSet = null;
            currentRow = null;
            sqlStatus.setSQLCode(SQLCode.SQL_OK);
            sqlCode = SQLCode.SQL_OK;

            if (logger.isDebugEnabled()) {
                logger.debug("Closed cursor {} (fetched {} rows)", cursorName, fetchCount);
            }

        } catch (SQLException e) {
            logger.error("Failed to close cursor {}: {}", cursorName, e.getMessage(), e);
            sqlStatus.setSQLCode(SQLCode.SQL_ERROR);
            sqlCode = SQLCode.SQL_ERROR;
            errorContext.handleError(e, programManager);
        }

        return sqlStatus;
    }

    /**
     * Check if the cursor is open.
     * @return true if the cursor is open
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Get the cursor name.
     * @return the cursor name
     */
    public String getCursorName() {
        return cursorName;
    }

    /**
     * Get the SQL code from the last operation.
     * @return the SQL code
     */
    public int getSqlCode() {
        return sqlCode;
    }

    /**
     * Get the number of rows fetched.
     * @return the fetch count
     */
    public int getFetchCount() {
        return fetchCount;
    }

    /**
     * Get the error context for configuring error handling.
     * @return the error context
     */
    public SqlErrorContext getErrorContext() {
        return errorContext;
    }
}
