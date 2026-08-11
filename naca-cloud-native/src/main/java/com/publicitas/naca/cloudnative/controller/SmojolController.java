package com.publicitas.naca.cloudnative.controller;

import com.publicitas.naca.cloudnative.service.SmojolService;
import com.publicitas.naca.cloudnative.service.SmojolService.ExecutionResult;
import com.publicitas.naca.cloudnative.service.SmojolService.CfgResult;
import com.publicitas.naca.cloudnative.service.SmojolService.AnalysisResult;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for COBOL interpretation and analysis using SMOJOL.
 * Provides APIs for:
 * - COBOL code interpretation and execution
 * - AST building
 * - Control Flow Graph (CFG) generation
 * - Program structure analysis
 */
@RestController
@RequestMapping("/api/smojol")
public class SmojolController {

    private final SmojolService smojolService;

    public SmojolController(SmojolService smojolService) {
        this.smojolService = smojolService;
    }

    /**
     * Interpret and execute COBOL source code.
     * POST /api/smojol/interpret
     *
     * @param request InterpretRequest containing cobolSource and optional inputData
     * @return ExecutionResult with output or errors
     */
    @PostMapping("/interpret")
    public ResponseEntity<InterpretResponse> interpret(@RequestBody InterpretRequest request) {
        if (request.getCobolSource() == null || request.getCobolSource().isEmpty()) {
            InterpretResponse response = new InterpretResponse();
            response.setSuccess(false);
            response.setErrors(List.of("COBOL source code is required"));
            return ResponseEntity.badRequest().body(response);
        }

        ExecutionResult result = smojolService.interpret(
            request.getCobolSource(),
            request.getInputData()
        );

        InterpretResponse response = new InterpretResponse();
        response.setSuccess(result.isSuccess());
        response.setOutput(result.getOutput());
        response.setErrors(result.getErrors());
        response.setExecutedSteps(result.getExecutedSteps());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Build Abstract Syntax Tree from COBOL source.
     * POST /api/smojol/ast
     *
     * @param request Simple request containing cobolSource
     * @return AST representation as Map
     */
    @PostMapping("/ast")
    public ResponseEntity<Map<String, Object>> buildAst(@RequestBody AstRequest request) {
        if (request.getCobolSource() == null || request.getCobolSource().isEmpty()) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "COBOL source code is required"
            );
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> astResult = smojolService.buildAst(request.getCobolSource());
        return Boolean.TRUE.equals(astResult.get("success"))
            ? ResponseEntity.ok(astResult)
            : ResponseEntity.badRequest().body(astResult);
    }

    /**
     * Build Control Flow Graph from COBOL source.
     * POST /api/smojol/cfg
     *
     * @param request CFG request containing cobolSource
     * @return CfgResult with nodes, edges, and DOT format
     */
    @PostMapping("/cfg")
    public ResponseEntity<CfgResponse> buildCfg(@RequestBody CfgRequest request) {
        if (request.getCobolSource() == null || request.getCobolSource().isEmpty()) {
            CfgResponse response = new CfgResponse();
            response.setSuccess(false);
            response.setError("COBOL source code is required");
            return ResponseEntity.badRequest().body(response);
        }

        CfgResult result = smojolService.buildControlFlowGraph(request.getCobolSource());

        CfgResponse response = new CfgResponse();
        response.setSuccess(result.isSuccess());
        if (result.isSuccess()) {
            response.setNodes(result.getNodes());
            response.setEdges(result.getEdges());
            response.setDotFormat(result.getDotFormat());
        } else {
            response.setError(result.getError());
        }

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Analyze COBOL program structure.
     * POST /api/smojol/analyze
     *
     * @param request Analysis request containing cobolSource
     * @return AnalysisResult with program metadata
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponse> analyze(@RequestBody AnalyzeRequest request) {
        if (request.getCobolSource() == null || request.getCobolSource().isEmpty()) {
            AnalyzeResponse response = new AnalyzeResponse();
            response.setSuccess(false);
            response.setError("COBOL source code is required");
            return ResponseEntity.badRequest().body(response);
        }

        AnalysisResult result = smojolService.analyzeProgram(request.getCobolSource());

        AnalyzeResponse response = new AnalyzeResponse();
        response.setSuccess(result.isSuccess());
        if (result.isSuccess()) {
            response.setAnalysis(result.getAnalysis());
        } else {
            response.setError(result.getError());
        }

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get program visualization (CFG as SVG/PNG).
     * GET /api/smojol/visualize?source=...&format=svg
     *
     * @param source COBOL source code
     * @param format Output format (svg or png)
     * @return Visualization as image
     */
    @GetMapping("/visualize")
    public ResponseEntity<?> visualize(
        @RequestParam String source,
        @RequestParam(defaultValue = "svg") String format
    ) {
        if (source == null || source.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "COBOL source code is required"
            ));
        }

        try {
            CfgResult cfgResult = smojolService.buildControlFlowGraph(source);
            if (!cfgResult.isSuccess()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", cfgResult.getError()
                ));
            }

            if ("svg".equalsIgnoreCase(format)) {
                return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("image/svg+xml"))
                    .body(Graphviz.fromString(cfgResult.getDotFormat()).render(Format.SVG).toString());
            }
            if ("png".equalsIgnoreCase(format)) {
                ByteArrayOutputStream image = new ByteArrayOutputStream();
                Graphviz.fromString(cfgResult.getDotFormat()).render(Format.PNG).toOutputStream(image);
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(image.toByteArray());
            }
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "format must be svg or png"
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    // Request/Response DTOs

    public static class InterpretRequest {
        private String cobolSource;
        private String inputData;

        public String getCobolSource() { return cobolSource; }
        public void setCobolSource(String cobolSource) { this.cobolSource = cobolSource; }
        public String getInputData() { return inputData; }
        public void setInputData(String inputData) { this.inputData = inputData; }
    }

    public static class InterpretResponse {
        private boolean success;
        private String output;
        private List<String> errors;
        private int executedSteps;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public int getExecutedSteps() { return executedSteps; }
        public void setExecutedSteps(int executedSteps) { this.executedSteps = executedSteps; }
    }

    public static class AstRequest {
        private String cobolSource;

        public String getCobolSource() { return cobolSource; }
        public void setCobolSource(String cobolSource) { this.cobolSource = cobolSource; }
    }

    public static class CfgRequest {
        private String cobolSource;

        public String getCobolSource() { return cobolSource; }
        public void setCobolSource(String cobolSource) { this.cobolSource = cobolSource; }
    }

    public static class CfgResponse {
        private boolean success;
        private Set<String> nodes;
        private List<Map<String, String>> edges;
        private String dotFormat;
        private String error;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Set<String> getNodes() { return nodes; }
        public void setNodes(Set<String> nodes) { this.nodes = nodes; }
        public List<Map<String, String>> getEdges() { return edges; }
        public void setEdges(List<Map<String, String>> edges) { this.edges = edges; }
        public String getDotFormat() { return dotFormat; }
        public void setDotFormat(String dotFormat) { this.dotFormat = dotFormat; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    public static class AnalyzeRequest {
        private String cobolSource;

        public String getCobolSource() { return cobolSource; }
        public void setCobolSource(String cobolSource) { this.cobolSource = cobolSource; }
    }

    public static class AnalyzeResponse {
        private boolean success;
        private Map<String, Object> analysis;
        private String error;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Map<String, Object> getAnalysis() { return analysis; }
        public void setAnalysis(Map<String, Object> analysis) { this.analysis = analysis; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
