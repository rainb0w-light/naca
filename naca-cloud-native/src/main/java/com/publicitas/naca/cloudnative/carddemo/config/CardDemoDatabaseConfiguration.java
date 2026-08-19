package com.publicitas.naca.cloudnative.carddemo.config;

import nacaLib.config.DatabaseConfig;
import nacaLib.config.DatabaseProperties;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** Enables the existing NacaRT Hikari/JDBC configuration for the CardDemo profile. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "carddemo", name = "enabled", havingValue = "true")
@Import({DatabaseProperties.class, DatabaseConfig.class})
public class CardDemoDatabaseConfiguration
{
    /** Guarantees migration execution when the application supplies its own DataSource. */
    @Bean
    public SmartInitializingSingleton cardDemoFlywayMigration(Flyway flyway)
    {
        return flyway::migrate;
    }
}
