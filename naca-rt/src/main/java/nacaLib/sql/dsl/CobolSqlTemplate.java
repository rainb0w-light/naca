/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sql.dsl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.sqlSupport.CSQLStatus;
import nacaLib.sqlSupport.SQLCode;
import nacaLib.varEx.VarAndEdit;

/**
 * SQL Template class for COBOL to Java translated programs.
 * Provides parameterized query execution to prevent SQL injection
 * and maintain COBOL SQL semantics.
 */
public class CobolSqlTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CobolSqlTemplate.class);

    /**
     * SQL code for no data found (standard SQL/DB2).
     */
    public static final int SQL_NO_DATA = 100;

    /**
     * Whitelist of allowed table names to prevent SQL injection.
     * This should be configured from external configuration in production.
     */
    private static final Set<String> ALLOWED_TABLES = Collections.synchronizedSet(new HashSet<>());

    static {
        // Default allowed tables - should be externalized via configuration
        ALLOWED_TABLES.add("VIT101");
        ALLOWED_TABLES.add("VIT102");
        ALLOWED_TABLES.add("VIT103");
        ALLOWED_TABLES.add("CUSTOMER");
        ALLOWED_TABLES.add("ORDER");
        ALLOWED_TABLES.add("PRODUCT");
    }

    private final JdbcTemplate jdbcTemplate;
    private final SqlErrorContext errorContext;
    private final BaseProgramManager programManager;

    /**
     * Constructor with DataSource.
     * @param dataSource the DataSource to use
     * @param programManager the program manager for SQL status
     */
    public CobolSqlTemplate(DataSource dataSource, BaseProgramManager programManager) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.errorContext = new SqlErrorContext();
        this.programManager = programManager;
    }

    /**
     * Constructor with JdbcTemplate (for Spring integration).
     * @param jdbcTemplate the JdbcTemplate to use
     * @param programManager the program manager for SQL status
     */
    public CobolSqlTemplate(JdbcTemplate jdbcTemplate, BaseProgramManager programManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.errorContext = new SqlErrorContext();
        this.programManager = programManager;
    }

    /**
     * Add a table name to the whitelist.
     * @param tableName the table name to allow
     */
    public static void allowTable(String tableName) {
        ALLOWED_TABLES.add(tableName.toUpperCase());
    }

    /**
     * Validate a table name against the whitelist.
     * @param tableName the table name to validate
     * @throws SQLInjectionException if table name is not allowed
     */
    public static void validateTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new SQLInjectionException("Table name cannot be empty");
        }
        String upperName = tableName.trim().toUpperCase();
        if (!ALLOWED_TABLES.contains(upperName)) {
            throw new SQLInjectionException("Table '" + tableName + "' is not in the allowed list");
        }
    }

    /**
     * Execute a query and map results to host variables.
     * Equivalent to COBOL: EXEC SQL SELECT ... INTO :host-var END-EXEC
     *
     * @param sql the SQL query with ? placeholders
     * @param paramSetter the parameter setter to bind values
     * @param resultMapper the result mapper to extract values
     * @return CSQLStatus the SQL execution status
     */
    public CSQLStatus queryForVars(String sql,
                                    ParamSetter paramSetter,
                                    ResultMapper resultMapper) {
        CSQLStatus sqlStatus = programManager.getSQLStatus();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing query: {}", sql);
            }

            // Use query with ResultSetExtractor to get the first row
            List<Object> result = jdbcTemplate.query(sql,
                (ps) -> {
                    if (paramSetter != null) {
                        paramSetter.setParameters(ps);
                    }
                },
                (rs) -> {
                    List<Object> row = new ArrayList<>();
                    if (rs.next()) {
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            row.add(rs.getObject(i));
                        }
                        return row;
                    }
                    return null;
                });

            if (result == null) {
                sqlStatus.setSQLCode(CobolSqlTemplate.SQL_NO_DATA);
                return sqlStatus;
            }

            // Map results to host variables
            ResultContext ctx = new ResultContext(result);
            resultMapper.mapResults(ctx);

            sqlStatus.setSQLCode(SQLCode.SQL_OK);

        } catch (Exception e) {
            logger.error("Query failed: {}", e.getMessage(), e);
            sqlStatus.setSQLCode(SQLCode.SQL_ERROR);
            errorContext.handleError(e, programManager);
        }

        return sqlStatus;
    }

    /**
     * Execute a query for a single row with dynamic parameters.
     * @param sql the SQL query with ? placeholders
     * @param params the parameters to bind
     * @return List<Object> the result row, or null if no data
     */
    public List<Object> queryForRow(String sql, Object... params) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing query: {} with params: {}", sql, params);
            }

            return jdbcTemplate.query(sql,
                (ps) -> {
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                },
                (rs) -> {
                    List<Object> row = new ArrayList<>();
                    if (rs.next()) {
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            row.add(rs.getObject(i));
                        }
                        return row;
                    }
                    return null;
                });

        } catch (Exception e) {
            logger.error("Query failed: {}", e.getMessage(), e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * Execute an INSERT, UPDATE, or DELETE statement.
     * Equivalent to COBOL: EXEC SQL INSERT/UPDATE/DELETE ... END-EXEC
     *
     * @param sql the SQL statement with ? placeholders
     * @param paramSetter the parameter setter to bind values
     * @return CSQLStatus the SQL execution status
     */
    public CSQLStatus executeUpdate(String sql, ParamSetter paramSetter) {
        CSQLStatus sqlStatus = programManager.getSQLStatus();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing update: {}", sql);
            }

            int rowsAffected = jdbcTemplate.update(sql, (ps) -> {
                if (paramSetter != null) {
                    paramSetter.setParameters(ps);
                }
            });

            sqlStatus.setRowsAffected(rowsAffected);
            sqlStatus.setSQLCode(SQLCode.SQL_OK);

        } catch (Exception e) {
            logger.error("Update failed: {}", e.getMessage(), e);
            sqlStatus.setSQLCode(SQLCode.SQL_ERROR);
            errorContext.handleError(e, programManager);
        }

        return sqlStatus;
    }

    /**
     * Execute an INSERT statement and return generated keys.
     * @param sql the SQL INSERT statement with ? placeholders
     * @param paramSetter the parameter setter to bind values
     * @return KeyHolder the generated keys
     */
    public KeyHolder executeInsertWithKeys(String sql, ParamSetter paramSetter) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing insert: {}", sql);
            }

            jdbcTemplate.update((conn) -> {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (paramSetter != null) {
                    paramSetter.setParameters(ps);
                }
                return ps;
            }, keyHolder);

        } catch (Exception e) {
            logger.error("Insert failed: {}", e.getMessage(), e);
            throw new RuntimeException("Insert execution failed", e);
        }

        return keyHolder;
    }

    /**
     * Execute an UPDATE statement with dynamic parameters.
     * @param sql the SQL UPDATE statement with ? placeholders
     * @param params the parameters to bind
     * @return int the number of rows affected
     */
    public int executeUpdate(String sql, Object... params) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing update: {} with params: {}", sql, params);
            }

            return jdbcTemplate.update(sql, (ps) -> {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            });

        } catch (Exception e) {
            logger.error("Update failed: {}", e.getMessage(), e);
            throw new RuntimeException("Update execution failed", e);
        }
    }

    /**
     * Execute a batch of SQL statements.
     * @param sqls array of SQL statements to execute
     * @return int[] array of update counts
     */
    public int[] executeBatch(String[] sqls) {
        try {
            return jdbcTemplate.batchUpdate(sqls);
        } catch (Exception e) {
            logger.error("Batch execution failed: {}", e.getMessage(), e);
            throw new RuntimeException("Batch execution failed", e);
        }
    }

    /**
     * Get the error context for configuring error handling.
     * @return SqlErrorContext the error context
     */
    public SqlErrorContext getErrorContext() {
        return errorContext;
    }

    /**
     * Get the JdbcTemplate for advanced operations.
     * @return JdbcTemplate the underlying JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * Get the program manager.
     * @return BaseProgramManager the program manager
     */
    public BaseProgramManager getProgramManager() {
        return programManager;
    }

    /**
     * Functional interface for setting prepared statement parameters.
     */
    @FunctionalInterface
    public interface ParamSetter {
        void setParameters(java.sql.PreparedStatement ps) throws SQLException;
    }

    /**
     * Functional interface for mapping result set to host variables.
     */
    @FunctionalInterface
    public interface ResultMapper {
        void mapResults(ResultContext ctx) throws SQLException;
    }

    /**
     * Context for accessing query results.
     */
    public static class ResultContext {
        private final List<Object> values;
        private int index = 0;

        public ResultContext(List<Object> values) {
            this.values = values;
        }

        public String getString(int index) {
            Object val = values.get(index - 1);
            return val != null ? val.toString() : null;
        }

        public int getInt(int index) {
            Object val = values.get(index - 1);
            return val != null ? Integer.parseInt(val.toString()) : 0;
        }

        public long getLong(int index) {
            Object val = values.get(index - 1);
            return val != null ? Long.parseLong(val.toString()) : 0L;
        }

        public double getDouble(int index) {
            Object val = values.get(index - 1);
            return val != null ? Double.parseDouble(val.toString()) : 0.0;
        }

        public Object getObject(int index) {
            return values.get(index - 1);
        }
    }
}
