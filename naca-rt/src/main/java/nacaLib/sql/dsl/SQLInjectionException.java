/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sql.dsl;

/**
 * Exception thrown when SQL injection is detected.
 * This is a RuntimeException that indicates a security violation
 * due to an attempt to use an unauthorized table or column name.
 */
public class SQLInjectionException extends RuntimeException {

    /**
     * Constructs a new SQLInjectionException with the specified detail message.
     *
     * @param message the detail message
     */
    public SQLInjectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new SQLInjectionException with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public SQLInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
