/*
 * NacaRTTests - Test Suite for NacaRT
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package com.publicitas.naca.tests.unit;

import com.publicitas.naca.tests.base.AbstractNacaTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Extra tests for NacaRT functionality.
 * Migrated from JUnit 3/4 to JUnit 5.
 */
public class ExtraTests extends AbstractNacaTest {

    @Test
    @DisplayName("Test MSBTST program")
    public void testMsbtst() {
        assertProgramOutput("msbtst");
    }

    @Test
    @DisplayName("Test IFAAI program")
    public void testIfaai() {
        assertProgramOutput("ifaai");
    }

    @Test
    @DisplayName("Test Math program")
    public void testMath() {
        assertProgramOutput("math");
    }

    @Test
    @DisplayName("Test CondTest program")
    public void testCondtest() {
        assertProgramOutput("condtest");
    }

    @Test
    @DisplayName("Test Leading Zeroes program")
    public void testLeadingZeroes() {
        assertProgramOutput("lz");
    }

    @Test
    @DisplayName("Test Substring program")
    public void testSubstring() {
        assertProgramOutput("substring");
    }

    @Test
    @DisplayName("Test Goto program")
    public void testGoto() {
        assertProgramOutput("goto");
    }

    @Test
    @DisplayName("Test Goto2 program")
    public void testGoto2() {
        assertProgramOutput("goto2");
    }

    @Test
    @DisplayName("Test After program")
    public void testAfter() {
        assertProgramOutput("after");
    }

    @Test
    @DisplayName("Test Batch program")
    public void testBatch() {
        String actual = runProgram("batch", true);
        try {
            String reportContent = readReportFile("batch.report");
            assertEquals(
                    normalizeOutput(readExpectedOutput("batch")),
                    normalizeOutput(reportContent),
                    "Batch program output mismatch"
            );
        } finally {
            new File("batch.report").delete();
        }
    }

    /**
     * Assert that a program's output matches the expected output.
     */
    private void assertProgramOutput(String programName) {
        String actual = runProgram(programName);
        String expected = readExpectedOutput(programName);
        assertEquals(
                normalizeOutput(expected),
                normalizeOutput(actual),
                () -> "Program '" + programName + "' output mismatch"
        );
    }

    /**
     * Read a report file (for batch programs).
     */
    private String readReportFile(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            StringBuilder content = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(fis))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Report file not found: " + filename, e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read report file: " + filename, e);
        }
    }
}