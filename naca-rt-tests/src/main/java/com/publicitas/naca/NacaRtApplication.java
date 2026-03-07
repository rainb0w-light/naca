/*
 * NacaRTTests - Test Suite for NacaRT
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package com.publicitas.naca;

import com.publicitas.naca.config.NacaRtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(NacaRtProperties.class)
@ComponentScan(basePackages = {"com.publicitas.naca", "nacaLib", "idea", "jlib"})
public class NacaRtApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacaRtApplication.class, args);
    }
}