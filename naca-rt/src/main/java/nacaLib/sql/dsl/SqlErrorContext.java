/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sql.dsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.exceptions.CGotoException;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;

/**
 * Error context for COBOL SQL error handling.
 * Supports COBOL WHENEVER SQLERROR GOTO semantics.
 *
 * Usage:
 * <pre>
 * // Equivalent to: EXEC SQL WHENEVER SQLERROR GOTO SQL-ERROR END-EXEC
 * sqlTemplate.getErrorContext().onErrorGoto(sqlErrorParagraph);
 *
 * // Equivalent to: EXEC SQL WHENEVER SQLERROR CONTINUE END-EXEC
 * sqlTemplate.getErrorContext().onErrorContinue();
 * </pre>
 */
public class SqlErrorContext {

    private static final Logger logger = LoggerFactory.getLogger(SqlErrorContext.class);

    /**
     * Error action enum for COBOL WHENEVER semantics.
     */
    public enum ErrorAction {
        CONTINUE,      // Continue execution (WHENEVER SQLERROR CONTINUE)
        GOTO_PARAGRAPH, // Goto a specific paragraph (WHENEVER SQLERROR GOTO paragraph)
        GOTO_SECTION    // Goto a specific section (WHENEVER SQLERROR GOTO section)
    }

    private ErrorAction errorAction = ErrorAction.CONTINUE;
    private Paragraph errorParagraph = null;
    private Section errorSection = null;

    /**
     * Configure error handling to goto a paragraph on error.
     * Equivalent to: EXEC SQL WHENEVER SQLERROR GOTO paragraph END-EXEC
     *
     * @param paragraph the paragraph to goto on error
     * @return this context for method chaining
     */
    public SqlErrorContext onErrorGoto(Paragraph paragraph) {
        this.errorAction = ErrorAction.GOTO_PARAGRAPH;
        this.errorParagraph = paragraph;
        this.errorSection = null;
        if (logger.isDebugEnabled()) {
            logger.debug("Configured error handling: GOTO {}", paragraph);
        }
        return this;
    }

    /**
     * Configure error handling to goto a section on error.
     * Equivalent to: EXEC SQL WHENEVER SQLERROR GOTO section END-EXEC
     *
     * @param section the section to goto on error
     * @return this context for method chaining
     */
    public SqlErrorContext onErrorGoto(Section section) {
        this.errorAction = ErrorAction.GOTO_SECTION;
        this.errorSection = section;
        this.errorParagraph = null;
        if (logger.isDebugEnabled()) {
            logger.debug("Configured error handling: GOTO {}", section);
        }
        return this;
    }

    /**
     * Configure error handling to continue on error.
     * Equivalent to: EXEC SQL WHENEVER SQLERROR CONTINUE END-EXEC
     *
     * @return this context for method chaining
     */
    public SqlErrorContext onErrorContinue() {
        this.errorAction = ErrorAction.CONTINUE;
        this.errorParagraph = null;
        this.errorSection = null;
        if (logger.isDebugEnabled()) {
            logger.debug("Configured error handling: CONTINUE");
        }
        return this;
    }

    /**
     * Configure warning handling to goto a paragraph on warning.
     * Equivalent to: EXEC SQL WHENEVER SQLWARNING GOTO paragraph END-EXEC
     *
     * @param paragraph the paragraph to goto on warning
     * @return this context for method chaining
     */
    public SqlErrorContext onWarningGoto(Paragraph paragraph) {
        // For now, treat warnings same as errors
        return onErrorGoto(paragraph);
    }

    /**
     * Configure warning handling to goto a section on warning.
     * Equivalent to: EXEC SQL WHENEVER SQLWARNING GOTO section END-EXEC
     *
     * @param section the section to goto on warning
     * @return this context for method chaining
     */
    public SqlErrorContext onWarningGoto(Section section) {
        // For now, treat warnings same as errors
        return onErrorGoto(section);
    }

    /**
     * Configure warning handling to continue on warning.
     * Equivalent to: EXEC SQL WHENEVER SQLWARNING CONTINUE END-EXEC
     *
     * @return this context for method chaining
     */
    public SqlErrorContext onWarningContinue() {
        // For now, treat warnings same as errors
        return onErrorContinue();
    }

    /**
     * Handle an SQL exception according to the configured error action.
     *
     * @param e the exception to handle
     * @param programManager the program manager for control flow
     */
    public void handleError(Exception e, BaseProgramManager programManager) {
        logger.error("SQL error occurred: {}", e.getMessage(), e);

        switch (errorAction) {
            case GOTO_PARAGRAPH:
                if (errorParagraph != null) {
                    logger.debug("Goto error paragraph: {}", errorParagraph);
                    throw new CGotoException(errorParagraph);
                }
                break;

            case GOTO_SECTION:
                if (errorSection != null) {
                    logger.debug("Goto error section: {}", errorSection);
                    throw new CGotoException(errorSection);
                }
                break;

            case CONTINUE:
            default:
                // Continue execution - just log the error
                logger.warn("SQL error - continuing execution: {}", e.getMessage());
                break;
        }
    }

    /**
     * Get the current error action.
     * @return the current error action
     */
    public ErrorAction getErrorAction() {
        return errorAction;
    }

    /**
     * Check if error handling is configured to continue.
     * @return true if continue action is configured
     */
    public boolean isContinue() {
        return errorAction == ErrorAction.CONTINUE;
    }

    /**
     * Check if error handling is configured to goto a paragraph.
     * @return true if goto paragraph action is configured
     */
    public boolean isGotoParagraph() {
        return errorAction == ErrorAction.GOTO_PARAGRAPH && errorParagraph != null;
    }

    /**
     * Check if error handling is configured to goto a section.
     * @return true if goto section action is configured
     */
    public boolean isGotoSection() {
        return errorAction == ErrorAction.GOTO_SECTION && errorSection != null;
    }

    /**
     * Reset the error context to default state.
     */
    public void reset() {
        this.errorAction = ErrorAction.CONTINUE;
        this.errorParagraph = null;
        this.errorSection = null;
    }
}
