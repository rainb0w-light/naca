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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration properties for Naca transpiler.
 * Supports externalized configuration via:
 * - application.properties/yml
 * - Environment variables
 * - System properties
 *
 * Replaces the hardcoded NacaTrans.cfg configuration file.
 *
 * Usage:
 * <pre>
 * # application.yml
 * naca:
 *   transpiler:
 *     rules-file: ${NACA_RULES_FILE:/etc/naca/rules.xml}
 *     input-path: ${NACA_INPUT_PATH:./input}
 *     output-path: ${NACA_OUTPUT_PATH:./output}
 *     log4j-config: ${NACA_LOG4J_CONFIG:./log4j2.xml}
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "naca.transpiler")
public class TranspilerProperties {

    private static final Logger logger = LoggerFactory.getLogger(TranspilerProperties.class);

    /**
     * Path to the XML rules file for transpilation.
     * Can be overridden via environment variable: NACA_RULES_FILE
     */
    private String rulesFile;

    /**
     * Input directory for COBOL source files.
     * Can be overridden via environment variable: NACA_INPUT_PATH
     */
    private String inputPath;

    /**
     * Output directory for generated Java files.
     * Can be overridden via environment variable: NACA_OUTPUT_PATH
     */
    private String outputPath;

    /**
     * Intermediary directory for temporary files.
     */
    private String interPath;

    /**
     * Path to Log4j configuration file.
     */
    private String log4jConfig;

    /**
     * Get the rules file path, checking environment variables first.
     * @return Path to rules file
     */
    public String getRulesFile() {
        String envRules = System.getenv("NACA_RULES_FILE");
        if (envRules != null && !envRules.isEmpty()) {
            logger.debug("Using rules file from environment variable: {}", envRules);
            return envRules;
        }
        String sysRules = System.getProperty("naca.transpiler.rulesFile");
        if (sysRules != null && !sysRules.isEmpty()) {
            logger.debug("Using rules file from system property: {}", sysRules);
            return sysRules;
        }
        return rulesFile;
    }

    public void setRulesFile(String rulesFile) {
        this.rulesFile = rulesFile;
    }

    /**
     * Get the input path, checking environment variables first.
     * @return Path to input directory
     */
    public String getInputPath() {
        String envInput = System.getenv("NACA_INPUT_PATH");
        if (envInput != null && !envInput.isEmpty()) {
            logger.debug("Using input path from environment variable: {}", envInput);
            return envInput;
        }
        String sysInput = System.getProperty("naca.transpiler.inputPath");
        if (sysInput != null && !sysInput.isEmpty()) {
            logger.debug("Using input path from system property: {}", sysInput);
            return sysInput;
        }
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * Get the output path, checking environment variables first.
     * @return Path to output directory
     */
    public String getOutputPath() {
        String envOutput = System.getenv("NACA_OUTPUT_PATH");
        if (envOutput != null && !envOutput.isEmpty()) {
            logger.debug("Using output path from environment variable: {}", envOutput);
            return envOutput;
        }
        String sysOutput = System.getProperty("naca.transpiler.outputPath");
        if (sysOutput != null && !sysOutput.isEmpty()) {
            logger.debug("Using output path from system property: {}", sysOutput);
            return sysOutput;
        }
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getInterPath() {
        String envInter = System.getenv("NACA_INTER_PATH");
        if (envInter != null && !envInter.isEmpty()) {
            return envInter;
        }
        return interPath;
    }

    public void setInterPath(String interPath) {
        this.interPath = interPath;
    }

    public String getLog4jConfig() {
        String envLog4j = System.getenv("NACA_LOG4J_CONFIG");
        if (envLog4j != null && !envLog4j.isEmpty()) {
            return envLog4j;
        }
        return log4jConfig;
    }

    public void setLog4jConfig(String log4jConfig) {
        this.log4jConfig = log4jConfig;
    }

    /**
     * Validate the transpiler configuration.
     * @return true if configuration is valid
     */
    public boolean isValid() {
        boolean valid = true;

        if (inputPath == null || inputPath.trim().isEmpty()) {
            logger.error("Input path is not configured");
            valid = false;
        }

        if (outputPath == null || outputPath.trim().isEmpty()) {
            logger.error("Output path is not configured");
            valid = false;
        }

        return valid;
    }
}
