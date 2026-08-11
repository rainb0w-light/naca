package com.publicitas.naca.analyzer;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Integration tests against the locally resolved cobol-rekt parser and interpreter. */
class CobolAnalyzerTest {
    private static final String PROGRAM = """
               IDENTIFICATION DIVISION.
               PROGRAM-ID. ANALYZER-TEST.
               DATA DIVISION.
               WORKING-STORAGE SECTION.
               01 MESSAGE-TEXT PIC X(5) VALUE "HELLO".
               PROCEDURE DIVISION.
               MAIN-PARAGRAPH.
                   DISPLAY MESSAGE-TEXT.
                   STOP RUN.
        """;

    @Test
    void buildsRealAstCfgAndDataLayout() {
        try (CobolAnalyzer analyzer = new CobolAnalyzer()) {
            CobolAnalyzer.AnalysisResult result = analyzer.analyze(PROGRAM);

            assertEquals("ANALYZER-TEST", result.programId());
            assertTrue(result.ast().type().endsWith("Context"));
            assertFalse(result.ast().children().isEmpty());
            assertFalse(result.cfg().nodes().isEmpty());
            assertTrue(result.cfg().edges().size() > 0);
            assertTrue(result.cfg().dot().startsWith("digraph CFG"));
            assertTrue(result.dataFlowResult().variableCount() >= 1);
            assertTrue(result.dataFlowResult().variables().stream()
                .anyMatch(item -> "MESSAGE-TEXT".equals(item.name())));
        }
    }

    @Test
    void interpretsDisplayUsingRuntimeData() {
        try (CobolAnalyzer analyzer = new CobolAnalyzer()) {
            String literalDisplay = PROGRAM.replace("DISPLAY MESSAGE-TEXT", "DISPLAY \"HELLO\"");
            CobolAnalyzer.ExecutionResult result = analyzer.execute(literalDisplay, null);

            assertTrue(result.isSuccess(), result.getErrorMessage());
            assertEquals("HELLO" + System.lineSeparator(), result.getOutput());
            assertTrue(result.executedSteps() > 0);
        }
    }

    @Test
    void graphIdentifiersAreDeterministic() {
        try (CobolAnalyzer analyzer = new CobolAnalyzer()) {
            assertEquals(analyzer.generateCFG(PROGRAM), analyzer.generateCFG(PROGRAM));
        }
    }

    @Test
    void rejectsSourceAndInteractiveInputBeyondConfiguredContract() {
        CobolAnalyzer.Limits limits = new CobolAnalyzer.Limits(
            16, 100, 100, 100, Duration.ofSeconds(2));
        try (CobolAnalyzer analyzer = new CobolAnalyzer(limits)) {
            assertThrows(CobolAnalyzer.AnalyzerLimitException.class,
                () -> analyzer.analyze(PROGRAM));
        }

        try (CobolAnalyzer analyzer = new CobolAnalyzer()) {
            CobolAnalyzer.ExecutionResult result = analyzer.execute(PROGRAM, "input");
            assertFalse(result.isSuccess());
            assertTrue(result.getErrorMessage().contains("input binding"));
        }
    }

    @Test
    void stopsProgramsThatExceedTheExecutionStepBudget() {
        String loop = """
                   IDENTIFICATION DIVISION.
                   PROGRAM-ID. LOOP-TEST.
                   DATA DIVISION.
                   WORKING-STORAGE SECTION.
                   01 LOOP-VALUE PIC X VALUE "X".
                   PROCEDURE DIVISION.
                   LOOP-PARAGRAPH.
                       DISPLAY "LOOP".
                       GO TO LOOP-PARAGRAPH.
            """;
        CobolAnalyzer.Limits limits = new CobolAnalyzer.Limits(
            10_000, 10_000, 10_000, 12, Duration.ofSeconds(3));

        try (CobolAnalyzer analyzer = new CobolAnalyzer(limits)) {
            CobolAnalyzer.ExecutionResult result = analyzer.execute(loop, null);

            assertFalse(result.isSuccess());
            assertTrue(result.getErrorMessage().contains("step limit"), result.getErrorMessage());
        }
    }

    @Test
    void enforcesAstNodeAndWallClockBudgets() {
        CobolAnalyzer.Limits nodeLimits = new CobolAnalyzer.Limits(
            10_000, 5, 10_000, 100, Duration.ofSeconds(3));
        try (CobolAnalyzer analyzer = new CobolAnalyzer(nodeLimits)) {
            assertThrows(CobolAnalyzer.AnalyzerLimitException.class,
                () -> analyzer.analyze(PROGRAM));
        }

        CobolAnalyzer.Limits timeoutLimits = new CobolAnalyzer.Limits(
            10_000, 10_000, 10_000, 100, Duration.ofNanos(1));
        try (CobolAnalyzer analyzer = new CobolAnalyzer(timeoutLimits)) {
            CobolAnalyzer.AnalyzerLimitException error = assertThrows(
                CobolAnalyzer.AnalyzerLimitException.class,
                () -> analyzer.analyze(PROGRAM));
            assertTrue(error.getMessage().contains("timed out"));
        }
    }
}
