/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unified logging utility for NacaRT.
 * Replaces System.out.println and direct Log4j calls with SLF4J.
 *
 * Usage:
 * <pre>
 * // Debug logging
 * NacaLogger.debug("Debug message");
 *
 * // Info logging
 * NacaLogger.info("Info message");
 *
 * // Warning logging
 * NacaLogger.warn("Warning message");
 *
 * // Error logging
 * NacaLogger.error("Error message", exception);
 *
 * // SQL logging (special category)
 * NacaLogger.logSql("SELECT * FROM table");
 * </pre>
 */
public class NacaLogger {

    // Main logger for general NacaRT logging
    private static final Logger logger = LoggerFactory.getLogger(NacaLogger.class);

    // SQL logger for SQL statement logging
    private static final Logger sqlLogger = LoggerFactory.getLogger("nacaLib.sql");

    // Performance logger for timing information
    private static final Logger perfLogger = LoggerFactory.getLogger("nacaLib.perf");

    /**
     * Log a debug message.
     * @param message the message to log
     */
    public static void debug(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * Log a debug message with exception.
     * @param message the message to log
     * @param t the exception
     */
    public static void debug(String message, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, t);
        }
    }

    /**
     * Log an info message.
     * @param message the message to log
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * Log an info message with exception.
     * @param message the message to log
     * @param t the exception
     */
    public static void info(String message, Throwable t) {
        logger.info(message, t);
    }

    /**
     * Log a warning message.
     * @param message the message to log
     */
    public static void warn(String message) {
        logger.warn(message);
    }

    /**
     * Log a warning message with exception.
     * @param message the message to log
     * @param t the exception
     */
    public static void warn(String message, Throwable t) {
        logger.warn(message, t);
    }

    /**
     * Log an error message.
     * @param message the message to log
     */
    public static void error(String message) {
        logger.error(message);
    }

    /**
     * Log an error message with exception.
     * @param message the message to log
     * @param t the exception
     */
    public static void error(String message, Throwable t) {
        logger.error(message, t);
    }

    /**
     * Log an important error message (replaces Log.logImportant).
     * @param message the message to log
     */
    public static void logImportant(String message) {
        logger.error(message);
    }

    /**
     * Log a SQL statement.
     * @param sql the SQL statement
     */
    public static void logSql(String sql) {
        if (sqlLogger.isDebugEnabled()) {
            sqlLogger.debug("SQL: {}", sql);
        }
    }

    /**
     * Log SQL debug information.
     * @param message the debug message
     */
    public static void logSqlDebug(String message) {
        if (sqlLogger.isDebugEnabled()) {
            sqlLogger.debug(message);
        }
    }

    /**
     * Log SQL parameter value.
     * @param paramName the parameter name
     * @param value the parameter value
     */
    public static void logSqlParam(String paramName, String value) {
        if (sqlLogger.isDebugEnabled()) {
            sqlLogger.debug("Param {} = {}", paramName, value);
        }
    }

    /**
     * Log SQL parameter value (int).
     * @param paramName the parameter name/number
     * @param value the parameter value
     */
    public static void logSqlParam(String paramName, int value) {
        if (sqlLogger.isDebugEnabled()) {
            sqlLogger.debug("Param {} = {}", paramName, value);
        }
    }

    /**
     * Log SQL parameter value (double).
     * @param paramName the parameter name/number
     * @param value the parameter value
     */
    public static void logSqlParam(String paramName, double value) {
        if (sqlLogger.isDebugEnabled()) {
            sqlLogger.debug("Param {} = {}", paramName, value);
        }
    }

    /**
     * Log a performance timing message.
     * @param message the message to log
     */
    public static void logPerf(String message) {
        if (perfLogger.isInfoEnabled()) {
            perfLogger.info(message);
        }
    }

    /**
     * Check if debug logging is enabled.
     * @return true if debug logging is enabled
     */
    public static boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * Check if SQL debug logging is enabled.
     * @return true if SQL debug logging is enabled
     */
    public static boolean isSqlDebugEnabled() {
        return sqlLogger.isDebugEnabled();
    }

    /**
     * Check if the logger is enabled at any level.
     * @return true if any logging is enabled
     */
    public static boolean isEnabled() {
        return logger.isInfoEnabled() || logger.isDebugEnabled() || logger.isWarnEnabled() || logger.isErrorEnabled();
    }
}
