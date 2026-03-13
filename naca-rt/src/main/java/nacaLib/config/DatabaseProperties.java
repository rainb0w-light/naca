/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration properties for Naca database settings.
 * Supports externalized configuration via:
 * - application.properties/yml
 * - Environment variables
 * - System properties
 *
 * Usage:
 * <pre>
 * # application.yml
 * naca:
 *   database:
 *     url: jdbc:postgresql://localhost:5432/mydb
 *     username: ${DB_USERNAME}
 *     password: ${DB_PASSWORD}
 *     driver-class-name: org.postgresql.Driver
 *     pool:
 *       max-size: 20
 *       min-idle: 5
 *       connection-timeout: 30000
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "naca.database")
public class DatabaseProperties {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseProperties.class);

    /**
     * JDBC URL for the database connection.
     * Can be overridden via environment variable: NACA_DATABASE_URL
     */
    private String url;

    /**
     * Database username.
     * Can be overridden via environment variable: NACA_DATABASE_USERNAME
     */
    private String username;

    /**
     * Database password.
     * Can be overridden via environment variable: NACA_DATABASE_PASSWORD
     */
    private String password;

    /**
     * JDBC driver class name.
     */
    private String driverClassName;

    /**
     * Connection pool settings.
     */
    private Pool pool = new Pool();

    /**
     * SQL settings.
     */
    private Sql sql = new Sql();

    public static class Pool {
        /**
         * Maximum number of connections in the pool.
         */
        private int maxSize = 20;

        /**
         * Minimum number of idle connections.
         */
        private int minIdle = 5;

        /**
         * Connection timeout in milliseconds.
         */
        private long connectionTimeout = 30000;

        /**
         * Idle timeout in milliseconds.
         */
        private long idleTimeout = 600000;

        /**
         * Maximum lifetime of a connection in milliseconds.
         */
        private long maxLifetime = 1800000;

        // Getters and Setters
        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
        public int getMinIdle() { return minIdle; }
        public void setMinIdle(int minIdle) { this.minIdle = minIdle; }
        public long getConnectionTimeout() { return connectionTimeout; }
        public void setConnectionTimeout(long connectionTimeout) { this.connectionTimeout = connectionTimeout; }
        public long getIdleTimeout() { return idleTimeout; }
        public void setIdleTimeout(long idleTimeout) { this.idleTimeout = idleTimeout; }
        public long getMaxLifetime() { return maxLifetime; }
        public void setMaxLifetime(long maxLifetime) { this.maxLifetime = maxLifetime; }
    }

    public static class Sql {
        /**
         * Enable SQL logging.
         */
        private boolean showSql = false;

        /**
         * Enable SQL formatting for logging.
         */
        private boolean formatSql = true;

        /**
         * Enable use of EXPLAIN PLAN.
         */
        private boolean useExplain = false;

        // Getters and Setters
        public boolean isShowSql() { return showSql; }
        public void setShowSql(boolean showSql) { this.showSql = showSql; }
        public boolean isFormatSql() { return formatSql; }
        public void setFormatSql(boolean formatSql) { this.formatSql = formatSql; }
        public boolean isUseExplain() { return useExplain; }
        public void setUseExplain(boolean useExplain) { this.useExplain = useExplain; }
    }

    // Getters and Setters
    public String getUrl() {
        // Check environment variable first
        String envUrl = System.getenv("NACA_DATABASE_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            logger.debug("Using database URL from environment variable");
            return envUrl;
        }
        // Then check system property
        String sysUrl = System.getProperty("naca.database.url");
        if (sysUrl != null && !sysUrl.isEmpty()) {
            logger.debug("Using database URL from system property");
            return sysUrl;
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        String envUsername = System.getenv("NACA_DATABASE_USERNAME");
        if (envUsername != null && !envUsername.isEmpty()) {
            return envUsername;
        }
        String sysUsername = System.getProperty("naca.database.username");
        if (sysUsername != null && !sysUsername.isEmpty()) {
            return sysUsername;
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        String envPassword = System.getenv("NACA_DATABASE_PASSWORD");
        if (envPassword != null && !envPassword.isEmpty()) {
            return envPassword;
        }
        String sysPassword = System.getProperty("naca.database.password");
        if (sysPassword != null && !sysPassword.isEmpty()) {
            return sysPassword;
        }
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public Sql getSql() {
        return sql;
    }

    public void setSql(Sql sql) {
        this.sql = sql;
    }

    /**
     * Validate the database configuration.
     * @return true if configuration is valid
     */
    public boolean isValid() {
        if (url == null || url.trim().isEmpty()) {
            logger.error("Database URL is not configured");
            return false;
        }
        if (driverClassName == null || driverClassName.trim().isEmpty()) {
            logger.error("Database driver class name is not configured");
            return false;
        }
        return true;
    }
}
