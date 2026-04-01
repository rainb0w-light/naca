package com.publicitas.naca.cloudnative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class
})
public class NacaCloudNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacaCloudNativeApplication.class, args);
    }
}
