/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package com.publicitas.naca.tests.sql;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nacaLib.sql.dsl.CobolSqlTemplate;
import nacaLib.sql.dsl.SQLInjectionException;

/**
 * Test cases for SQL injection prevention.
 * Verifies that the SQL whitelist mechanism works correctly.
 */
@DisplayName("SQL Injection Prevention Tests")
public class SqlInjectionTests {

    @BeforeEach
    void setUp() {
        // Clear any custom table additions before each test
        // Note: In a real scenario, we would reset the ALLOWED_TABLES set
    }

    @Test
    @DisplayName("Should accept valid table name from whitelist")
    void shouldAcceptValidTableName() {
        // This should not throw any exception
        assertDoesNotThrow(() -> {
            CobolSqlTemplate.validateTableName("VIT101");
            CobolSqlTemplate.validateTableName("vit101"); // case insensitive
            CobolSqlTemplate.validateTableName("  VIT101  "); // with whitespace
        });
    }

    @Test
    @DisplayName("Should reject table name not in whitelist")
    void shouldRejectInvalidTableName() {
        SQLInjectionException exception = assertThrows(
            SQLInjectionException.class,
            () -> CobolSqlTemplate.validateTableName("UNAUTHORIZED_TABLE")
        );
        assertTrue(exception.getMessage().contains("UNAUTHORIZED_TABLE"));
    }

    @Test
    @DisplayName("Should reject null table name")
    void shouldRejectNullTableName() {
        assertThrows(
            SQLInjectionException.class,
            () -> CobolSqlTemplate.validateTableName(null)
        );
    }

    @Test
    @DisplayName("Should reject empty table name")
    void shouldRejectEmptyTableName() {
        assertThrows(
            SQLInjectionException.class,
            () -> CobolSqlTemplate.validateTableName("")
        );
    }

    @Test
    @DisplayName("Should reject whitespace-only table name")
    void shouldRejectWhitespaceOnlyTableName() {
        assertThrows(
            SQLInjectionException.class,
            () -> CobolSqlTemplate.validateTableName("   ")
        );
    }

    @Test
    @DisplayName("Should reject SQL injection attempts in table name")
    void shouldRejectSqlInjectionAttempts() {
        // Common SQL injection patterns
        String[] injectionAttempts = {
            "VIT101; DROP TABLE VIT101--",
            "VIT101' OR '1'='1",
            "VIT101' UNION SELECT * FROM USERS--",
            "VIT101'; EXEC xp_cmdshell('dir')--",
            "CUSTOMER; DELETE FROM CUSTOMER WHERE '1'='1"
        };

        for (String injection : injectionAttempts) {
            assertThrows(
                SQLInjectionException.class,
                () -> CobolSqlTemplate.validateTableName(injection),
                "Should reject injection attempt: " + injection
            );
        }
    }

    @Test
    @DisplayName("Should allow adding new tables to whitelist")
    void shouldAllowAddingTablesToWhitelist() {
        // Add a new table to the whitelist
        CobolSqlTemplate.allowTable("NEW_TABLE");

        // Should now be valid
        assertDoesNotThrow(() -> {
            CobolSqlTemplate.validateTableName("NEW_TABLE");
        });

        // Case insensitive
        assertDoesNotThrow(() -> {
            CobolSqlTemplate.validateTableName("new_table");
        });
    }

    @Test
    @DisplayName("Should still reject tables after whitelist addition fails")
    void shouldStillRejectUnauthorizedTables() {
        // Try to inject via table name
        assertThrows(
            SQLInjectionException.class,
            () -> CobolSqlTemplate.validateTableName("TABLE'; DELETE FROM USERS; --")
        );
    }
}
