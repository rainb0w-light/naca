package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.publicitas.naca.cloudnative.carddemo.sql.CardDemoNacaRuntimeBridge;
import com.publicitas.naca.cloudnative.carddemo.cics.PostgresCicsRecordStore;
import com.publicitas.naca.cloudnative.carddemo.session.CardDemoConversationConflictException;
import com.publicitas.naca.cloudnative.carddemo.session.CardDemoConversationState;
import com.publicitas.naca.cloudnative.carddemo.session.CardDemoConversationStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import javax.sql.DataSource;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.CESM.CESMReturnCode;
import nacaLib.cics.CicsRecordReadRequest;
import nacaLib.cics.CicsRecordReadResult;
import nacaLib.sqlSupport.CSQLStatus;
import nacaLib.sqlSupport.SQLCode;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** PostgreSQL 16 acceptance for migrations, readiness and transaction rollback. */
@Tag("carddemo-postgres")
@Testcontainers(disabledWithoutDocker = true)
class CardDemoPostgresAcceptanceTest
{
    private static final String DATABASE_VALUE = "carddemo";
    private static final String DIAGNOSTIC_PROGRAM = "BMSJSON";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName(DATABASE_VALUE)
            .withUsername(DATABASE_VALUE)
            .withPassword(DATABASE_VALUE);

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void emptyDatabaseMigratesAndRollsBackOneUnitOfWork()
    {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
            NacaCloudNativeApplication.class)
            .web(WebApplicationType.NONE)
            .profiles("carddemo")
            .run(
                "--naca.database.url=" + POSTGRES.getJdbcUrl(),
                "--naca.database.username=" + POSTGRES.getUsername(),
                "--naca.database.password=" + POSTGRES.getPassword(),
                "--naca.database.driver-class-name=org.postgresql.Driver"))
        {
            DataSource dataSource = context.getBean(DataSource.class);
            JdbcTemplate jdbc = context.getBean(JdbcTemplate.class);
            Flyway flyway = context.getBean(Flyway.class);
            HealthIndicator health = context.getBean("cardDemoPostgres", HealthIndicator.class);
            CardDemoNacaRuntimeBridge bridge = context.getBean(CardDemoNacaRuntimeBridge.class);
            PostgresCicsRecordStore recordStore = context.getBean(PostgresCicsRecordStore.class);
            CardDemoConversationStore conversations = context.getBean(CardDemoConversationStore.class);
            ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

            assertNotNull(flyway.info().current(),
                "No migration applied from " + Arrays.toString(flyway.getConfiguration().getLocations())
                    + "; pending=" + flyway.info().pending().length
                    + "; all=" + flyway.info().all().length);
            assertEquals("005", flyway.info().current().getVersion().getVersion(),
                "Unexpected Flyway schema version");
            assertEquals(Status.UP, health.health().getStatus(), "PostgreSQL readiness must be UP");

            byte[] userKey = "USER0001".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
            byte[] userRecord = "USER0001PASSWORD".getBytes(
                java.nio.charset.StandardCharsets.US_ASCII);
            jdbc.update("""
                insert into carddemo_vsam.file_manifest
                    (file_name, key_offset, key_length, record_length, encoding)
                values ('USRSEC', 0, 8, 16, 'ASCII')
                on conflict (file_name) do nothing
                """);
            jdbc.update("""
                insert into carddemo_vsam.record_store
                    (file_name, primary_key, record_data, record_length)
                values (?, ?, ?, ?)
                on conflict (file_name, primary_key) do update
                    set record_data = excluded.record_data,
                        record_length = excluded.record_length
                """, "USRSEC", userKey, userRecord, userRecord.length);

            CicsRecordReadResult found = recordStore.read(
                new CicsRecordReadRequest("usrsec", userKey, userRecord.length, false));
            assertTrue(found.isNormal(), "Existing keyed record must report NORMAL");
            assertTrue(Arrays.equals(userRecord, found.record()),
                "CICS READ must preserve fixed record bytes");
            CicsRecordReadResult missing = recordStore.read(
                new CicsRecordReadRequest("USRSEC", "MISSING1".getBytes(
                    java.nio.charset.StandardCharsets.US_ASCII), userRecord.length, false));
            assertEquals(CESMReturnCode.NOT_FOUND.getCondition(), missing.response(),
                "Missing keyed record must report NOTFND");
            CicsRecordReadResult tooLong = recordStore.read(
                new CicsRecordReadRequest("USRSEC", userKey, userRecord.length - 1, false));
            assertEquals(CESMReturnCode.LENGERR.getCondition(), tooLong.response(),
                "An undersized INTO buffer must report LENGERR");

            TransactionTemplate transaction =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));
            transaction.executeWithoutResult(status -> {
                jdbc.update("""
                    insert into carddemo.transaction_type
                        (transaction_type_code, description)
                    values (?, ?)
                    """, "TST1", "rollback probe");
                status.setRollbackOnly();
            });

            Integer rows = jdbc.queryForObject("""
                select count(*)
                  from carddemo.transaction_type
                 where transaction_type_code = 'TST1'
                """, Integer.class);
            assertEquals(0, rows, "Spring transaction must roll back direct JDBC work");

            BaseEnvironment environment = mock(BaseEnvironment.class);
            BaseProgramManager programManager = mock(BaseProgramManager.class);
            CSQLStatus sqlStatus = new CSQLStatus();
            when(programManager.getSQLStatus()).thenReturn(sqlStatus);

            transaction.executeWithoutResult(status -> {
                try (CardDemoNacaRuntimeBridge.RuntimeBinding binding =
                         bridge.bind(environment, programManager))
                {
                    CSQLStatus result = binding.sql().executeUpdate("""
                        insert into carddemo.transaction_type
                            (transaction_type_code, description)
                        values (?, ?)
                        """, statement -> {
                            statement.setString(1, "TST2");
                            statement.setString(2, "NacaRT bridge rollback probe");
                        });
                    assertEquals(SQLCode.SQL_OK, result.getSQLCode(),
                        "NacaRT bridge insert must report SQLCODE 0");
                }
                status.setRollbackOnly();
            });
            transaction.executeWithoutResult(status -> {
                try (CardDemoNacaRuntimeBridge.RuntimeBinding binding =
                         bridge.bind(environment, programManager))
                {
                    CSQLStatus result = binding.sql().queryForVars("""
                        select description
                          from carddemo.transaction_type
                         where transaction_type_code = ?
                        """, statement -> statement.setString(1, "NONE"), ignored -> { });
                    assertEquals(SQLCode.SQL_NO_DATA, result.getSQLCode(),
                        "Missing row must report SQLCODE +100");
                }
            });

            jdbc.update("""
                insert into carddemo.transaction_type(transaction_type_code, description)
                values ('TST3', 'duplicate probe')
                """);
            transaction.executeWithoutResult(status -> {
                try (CardDemoNacaRuntimeBridge.RuntimeBinding binding =
                         bridge.bind(environment, programManager))
                {
                    CSQLStatus result = binding.sql().executeUpdate("""
                        insert into carddemo.transaction_type(transaction_type_code, description)
                        values ('TST3', 'duplicate probe')
                        """, (nacaLib.sql.dsl.CobolSqlTemplate.ParamSetter) null);
                    assertEquals(SQLCode.SQL_DUPLICATE_INDEX_KEY, result.getSQLCode(),
                        "Duplicate PostgreSQL key must report DB2 SQLCODE -803");
                }
                status.setRollbackOnly();
            });
            jdbc.update("delete from carddemo.transaction_type where transaction_type_code = 'TST3'");
            UUID conversationId = UUID.randomUUID();
            CardDemoConversationState first = conversations.save(conversationId, "CC00", DIAGNOSTIC_PROGRAM,
                new byte[] {1, 2, 3}, objectMapper.createObjectNode().put("map", "COSGN0A"),
                0, Duration.ofMinutes(10));
            assertEquals(1, first.version(), "New conversation must start at version one");

            CardDemoConversationState second = conversations.save(conversationId, "CM00", DIAGNOSTIC_PROGRAM,
                new byte[] {4, 5}, objectMapper.createObjectNode().put("map", "COMEN1A"),
                first.version(), Duration.ofMinutes(10));
            assertEquals(2, second.version(), "A resumed conversation must increment its version");
            assertEquals("COMEN1A", conversations.find(conversationId).orElseThrow()
                .terminalState().path("map").asText(), "Terminal snapshot must survive a database read");
            assertThrows(CardDemoConversationConflictException.class,
                () -> conversations.save(conversationId, "CM00", DIAGNOSTIC_PROGRAM, new byte[0],
                    objectMapper.createObjectNode(), first.version(), Duration.ofMinutes(10)),
                "A stale browser request must not overwrite the latest snapshot");

            UUID rollbackConversation = UUID.randomUUID();
            transaction.executeWithoutResult(status -> {
                conversations.save(rollbackConversation, "CC00", DIAGNOSTIC_PROGRAM, new byte[0],
                    objectMapper.createObjectNode(), 0, Duration.ofMinutes(10));
                status.setRollbackOnly();
            });
            assertTrue(conversations.find(rollbackConversation).isEmpty(),
                "Conversation state must share the caller's rollback boundary");
            verify(environment, times(3))
                .setExternalDbConnection(org.mockito.ArgumentMatchers.any());
            verify(environment, times(3)).setRuntimeConfigOption("APPLID", "CARDDEMO");
            verify(environment, times(3)).setRuntimeConfigOption("SYSID", "NACA");
            verify(environment, times(3)).setCicsRecordStore(recordStore);
            verify(environment, times(3)).releaseSQLConnection();
        }
    }

    @Test
    void translatedSignonReadsPostgresAndReturnsTheBusinessError() throws Exception
    {
        Flyway.configure()
            .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
            .locations("classpath:db/migration/carddemo")
            .defaultSchema("carddemo_runtime")
            .schemas("carddemo_runtime", "carddemo_vsam", "carddemo", "carddemo_compat")
            .load().migrate();
        String classes = CardDemoOnlineTranslationBaselineTest
            .generatedSignonClassesDirectory().toString();
        ProcessBuilder runtime = new ProcessBuilder(
            "java", "-cp", System.getProperty("java.class.path") + File.pathSeparator + classes,
            CardDemoSignonProgramRunner.class.getName(), classes,
            POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        runtime.redirectErrorStream(true);
        Process execution = runtime.start();
        String output = new String(execution.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(0, execution.waitFor(), output);
        assertTrue(output.contains("RESULT_JSON="), output);
        assertTrue(output.contains("User not found. Try again"), output);
        assertTrue(output.contains("\"PASSWD\":{\"value\":\"\""),
            "Password must be masked at the REST boundary: " + output);
    }
}
