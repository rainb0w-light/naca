package com.publicitas.naca.cloudnative.carddemo.sql;

import java.sql.Connection;
import java.util.Objects;
import javax.sql.DataSource;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.sqlSupport.SQLConnection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

/** Binds the Spring transaction connection to one NacaRT execution environment. */
@Component
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
public class CardDemoNacaRuntimeBridge
{
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final PostgresSqlCodeMapper sqlCodeMapper;

    /** Creates the request-scoped bridge factory. */
    public CardDemoNacaRuntimeBridge(DataSource dataSource, JdbcTemplate jdbcTemplate,
        PostgresSqlCodeMapper sqlCodeMapper)
    {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.sqlCodeMapper = sqlCodeMapper;
    }

    /**
     * Binds the transaction-aware Spring connection and returns a closeable execution handle.
     * The caller owns the surrounding Spring transaction; closing only unbinds NacaRT.
     */
    public RuntimeBinding bind(BaseEnvironment environment, BaseProgramManager programManager)
    {
        Objects.requireNonNull(environment, "environment");
        Objects.requireNonNull(programManager, "programManager");
        Connection connection = DataSourceUtils.getConnection(dataSource);
        SQLConnection runtimeConnection =
            new SQLConnection(connection, "carddemo", "postgresql", true, false, null);
        environment.setExternalDbConnection(runtimeConnection);
        PostgresCobolSqlTemplate sql =
            new PostgresCobolSqlTemplate(jdbcTemplate, programManager, sqlCodeMapper);
        return new RuntimeBinding(environment, connection, sql);
    }

    /** One request-scoped NacaRT/PostgreSQL binding. */
    public final class RuntimeBinding implements AutoCloseable
    {
        private final BaseEnvironment environment;
        private final Connection connection;
        private final PostgresCobolSqlTemplate sqlTemplate;
        private boolean closed;

        private RuntimeBinding(BaseEnvironment environment, Connection connection,
            PostgresCobolSqlTemplate sqlTemplate)
        {
            this.environment = environment;
            this.connection = connection;
            this.sqlTemplate = sqlTemplate;
        }

        /** Returns the PostgreSQL-aware implementation of the NacaRT COBOL SQL API. */
        public PostgresCobolSqlTemplate sql()
        {
            return sqlTemplate;
        }

        @Override
        public void close()
        {
            if (!closed)
            {
                environment.releaseSQLConnection();
                DataSourceUtils.releaseConnection(connection, dataSource);
                closed = true;
            }
        }
    }
}
