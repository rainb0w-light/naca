package com.publicitas.naca.cloudnative;

import com.publicitas.naca.cloudnative.controller.SmojolController;
import com.publicitas.naca.cloudnative.service.SmojolService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies that cloud endpoints are backed by real Analyzer results. */
class SmojolServiceTest {
    private static final String PROGRAM = """
               IDENTIFICATION DIVISION.
               PROGRAM-ID. CLOUD-ANALYZER.
               DATA DIVISION.
               WORKING-STORAGE SECTION.
               01 MESSAGE-TEXT PIC X(5).
               PROCEDURE DIVISION.
               MAIN-PARAGRAPH.
                   DISPLAY "HELLO".
                   STOP RUN.
        """;

    private final SmojolService service = new SmojolService();

    @AfterEach
    void closeService() {
        service.close();
    }

    @Test
    void returnsParserBackedAstCfgAndAnalysis() {
        Map<String, Object> ast = service.buildAst(PROGRAM);
        SmojolService.CfgResult cfg = service.buildControlFlowGraph(PROGRAM);
        SmojolService.AnalysisResult analysis = service.analyzeProgram(PROGRAM);

        assertEquals(Boolean.TRUE, ast.get("success"));
        assertNotNull(ast.get("ast"));
        assertTrue(cfg.isSuccess(), cfg.getError());
        assertFalse(cfg.getNodes().isEmpty());
        assertFalse(cfg.getEdges().isEmpty());
        assertTrue(cfg.getDotFormat().startsWith("digraph CFG"));
        assertTrue(analysis.isSuccess(), analysis.getError());
        assertEquals("CLOUD-ANALYZER", analysis.getAnalysis().get("programId"));
    }

    @Test
    void interpretsProgramAndReportsExecutedSteps() {
        SmojolService.ExecutionResult result = service.interpret(PROGRAM, null);

        assertTrue(result.isSuccess(), String.join("; ", result.getErrors()));
        assertEquals("HELLO" + System.lineSeparator(), result.getOutput());
        assertTrue(result.getExecutedSteps() > 0);
    }

    @Test
    void rendersTheRealControlFlowGraphAsSvg() {
        ResponseEntity<?> response = new SmojolController(service).visualize(PROGRAM, "svg");

        assertTrue(response.getStatusCode().is2xxSuccessful(), String.valueOf(response.getBody()));
        assertTrue(response.getBody() instanceof String);
        assertTrue(((String) response.getBody()).contains("<svg"));
        assertFalse(((String) response.getBody()).contains("Coming Soon"));
    }
}
