package com.publicitas.naca.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Provides naca rt config behavior. */
@Configuration
@EnableConfigurationProperties(NacaRtProperties.class)
public class NacaRtConfig {

    /** Executes the sequencer config operation. */
    @Bean
    public SequencerConfig sequencerConfig() {
        return new SequencerConfig();
    }
}