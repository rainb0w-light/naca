/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Database configuration for Naca runtime.
 * Configures HikariCP connection pool and JdbcTemplate.
 *
 * This configuration is externalized and can be customized via:
 * - application.properties/yml
 * - Environment variables
 * - System properties
 */
@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired
    private DatabaseProperties databaseProperties;

    /**
     * Configure HikariCP DataSource.
     * @return DataSource the configured connection pool
     */
    @Bean
    public DataSource dataSource() {
        logger.info("Configuring HikariCP connection pool");

        HikariConfig config = new HikariConfig();

        // Basic connection settings
        config.setJdbcUrl(databaseProperties.getUrl());
        config.setUsername(databaseProperties.getUsername());
        config.setPassword(databaseProperties.getPassword());

        if (databaseProperties.getDriverClassName() != null) {
            config.setDriverClassName(databaseProperties.getDriverClassName());
        }

        // Pool settings
        DatabaseProperties.Pool poolConfig = databaseProperties.getPool();
        config.setMaximumPoolSize(poolConfig.getMaxSize());
        config.setMinimumIdle(poolConfig.getMinIdle());
        config.setConnectionTimeout(poolConfig.getConnectionTimeout());
        config.setIdleTimeout(poolConfig.getIdleTimeout());
        config.setMaxLifetime(poolConfig.getMaxLifetime());

        // Connection pool name
        config.setPoolName("NacaHikariPool");

        // Validation query
        config.setConnectionTestQuery("SELECT 1");

        logger.info("HikariCP configured with URL: {}, maxPoolSize: {}",
            databaseProperties.getUrl(), poolConfig.getMaxSize());

        return new HikariDataSource(config);
    }

    /**
     * Configure JdbcTemplate for database operations.
     * @param dataSource the DataSource to use
     * @return JdbcTemplate the configured template
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        logger.info("Configuring JdbcTemplate");
        return new JdbcTemplate(dataSource);
    }
}
