package com.publicitas.naca.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NacaRtProperties.class)
public class NacaRtConfig {

    @Bean
    public SequencerConfig sequencerConfig() {
        return new SequencerConfig();
    }
}