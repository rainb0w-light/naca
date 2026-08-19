package com.publicitas.naca.cloudnative.carddemo.health;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Readiness evidence for the database, required schemas and Flyway state. */
@Component("cardDemoPostgres")
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
public class CardDemoPostgresHealthIndicator extends AbstractHealthIndicator
{
    private static final String SCHEMA_QUERY = """
        select schema_name
          from information_schema.schemata
         where schema_name in ('carddemo_runtime', 'carddemo_vsam', 'carddemo', 'carddemo_compat')
        """;

    private final DataSource dataSource;
    private final Flyway flyway;

    /** Creates a readiness indicator for the CardDemo database. */
    public CardDemoPostgresHealthIndicator(DataSource dataSource, Flyway flyway)
    {
        this.dataSource = dataSource;
        this.flyway = flyway;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception
    {
        Map<String, Boolean> schemas = new ConcurrentHashMap<>();
        schemas.put("carddemo_runtime", false);
        schemas.put("carddemo_vsam", false);
        schemas.put("carddemo", false);
        schemas.put("carddemo_compat", false);

        String database;
        try (Connection connection = dataSource.getConnection())
        {
            database = connection.getCatalog();
            try (PreparedStatement statement = connection.prepareStatement(SCHEMA_QUERY);
                 ResultSet result = statement.executeQuery())
            {
                while (result.next())
                {
                    schemas.put(result.getString(1), true);
                }
            }
        }

        MigrationInfo[] pending = flyway.info().pending();
        MigrationInfo current = flyway.info().current();
        boolean allSchemasPresent = schemas.values().stream().allMatch(Boolean::booleanValue);
        if (allSchemasPresent && pending.length == 0)
        {
            builder.up();
        }
        else
        {
            builder.down();
        }
        builder.withDetail("database", database)
            .withDetail("schemas", schemas)
            .withDetail("flywayVersion", current == null ? "none" : current.getVersion().toString())
            .withDetail("pendingMigrations", pending.length);
    }
}
