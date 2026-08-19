package com.publicitas.naca.cloudnative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/** Provides naca cloud native application behavior. */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class
})
public class NacaCloudNativeApplication {

    /** Executes the main operation. */
    public static void main(String[] args) {
        SpringApplication.run(NacaCloudNativeApplication.class, args);
    }
}
