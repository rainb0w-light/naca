package com.publicitas.naca.cloudnative.service;

import com.publicitas.naca.analyzer.CobolAnalyzer;
import com.publicitas.naca.analyzer.CobolAnalyzer.AnalyzerException;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** HTTP-facing adapter for the bounded cobol-rekt integration in {@code naca-analyzer}. */
@Service
public class SmojolService {
    private final CobolAnalyzer analyzer = new CobolAnalyzer();

    /** Interprets COBOL with parser, execution-step, and wall-clock limits. */
    public ExecutionResult interpret(String cobolSource, String inputData) {
        CobolAnalyzer.ExecutionResult result = analyzer.execute(cobolSource, inputData);
        return result.isSuccess()
            ? ExecutionResult.success(result.getOutput(), result.executedSteps())
            : ExecutionResult.failure(result.getErrorMessage());
    }

    /** Builds a real parser AST and returns a JSON-serializable response. */
    public Map<String, Object> buildAst(String cobolSource) {
        try {
            CobolAnalyzer.AnalysisResult result = analyzer.analyze(cobolSource);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("programId", result.programId());
            response.put("ast", result.ast());
            response.put("dataFlow", result.dataFlowResult());
            return response;
        } catch (AnalyzerException exception) {
            return Map.of("success", false, "error", exception.getMessage());
        }
    }

    /** Builds the real cobol-rekt control-flow graph. */
    public CfgResult buildControlFlowGraph(String cobolSource) {
        try {
            CobolAnalyzer.ControlFlowGraph graph = analyzer.analyze(cobolSource).cfg();
            Set<String> nodes = new LinkedHashSet<>();
            graph.nodes().forEach(node -> nodes.add(node.name()));
            List<Map<String, String>> edges = graph.edges().stream()
                .map(edge -> Map.of(
                    "source", edge.source(),
                    "target", edge.target(),
                    "kind", edge.kind()))
                .toList();
            return CfgResult.success(nodes, edges, graph.dot());
        } catch (AnalyzerException exception) {
            return CfgResult.failure(exception.getMessage());
        }
    }

    /** Returns parser-backed program metadata instead of source-text heuristics. */
    public AnalysisResult analyzeProgram(String cobolSource) {
        try {
            CobolAnalyzer.AnalysisResult result = analyzer.analyze(cobolSource);
            Map<String, Object> analysis = new LinkedHashMap<>();
            analysis.put("programId", result.programId());
            analysis.put("astRootType", result.ast().type());
            analysis.put("variableCount", result.dataFlowResult().variableCount());
            analysis.put("cfgNodeCount", result.cfg().nodes().size());
            analysis.put("cfgEdgeCount", result.cfg().edges().size());
            return AnalysisResult.success(analysis);
        } catch (AnalyzerException exception) {
            return AnalysisResult.failure(exception.getMessage());
        }
    }

    /** Releases the virtual-thread executor when the Spring application stops. */
    @PreDestroy
    public void close() {
        analyzer.close();
    }

    /** Result returned by the interpretation endpoint. */
    public static final class ExecutionResult {
        private final boolean success;
        private final String output;
        private final List<String> errors;
        private final int executedSteps;

        private ExecutionResult(
            boolean success,
            String output,
            List<String> errors,
            int executedSteps) {
            this.success = success;
            this.output = output;
            this.errors = List.copyOf(errors);
            this.executedSteps = executedSteps;
        }

        static ExecutionResult success(String output, int executedSteps) {
            return new ExecutionResult(true, output, List.of(), executedSteps);
        }

        static ExecutionResult failure(String error) {
            return new ExecutionResult(false, null, List.of(error), 0);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return output;
        }

        public List<String> getErrors() {
            return errors;
        }

        public int getExecutedSteps() {
            return executedSteps;
        }
    }

    /** Result returned by the CFG endpoint. */
    public static final class CfgResult {
        private final boolean success;
        private final Set<String> nodes;
        private final List<Map<String, String>> edges;
        private final String dotFormat;
        private final String error;

        private CfgResult(
            boolean success,
            Set<String> nodes,
            List<Map<String, String>> edges,
            String dotFormat,
            String error) {
            this.success = success;
            this.nodes = nodes == null ? Set.of() : Set.copyOf(nodes);
            this.edges = edges == null ? List.of() : List.copyOf(edges);
            this.dotFormat = dotFormat;
            this.error = error;
        }

        static CfgResult success(
            Set<String> nodes,
            List<Map<String, String>> edges,
            String dotFormat) {
            return new CfgResult(true, nodes, edges, dotFormat, null);
        }

        static CfgResult failure(String error) {
            return new CfgResult(false, null, null, null, error);
        }

        public boolean isSuccess() {
            return success;
        }

        public Set<String> getNodes() {
            return nodes;
        }

        public List<Map<String, String>> getEdges() {
            return edges;
        }

        public String getDotFormat() {
            return dotFormat;
        }

        public String getError() {
            return error;
        }
    }

    /** Result returned by the program-analysis endpoint. */
    public static final class AnalysisResult {
        private final boolean success;
        private final Map<String, Object> analysis;
        private final String error;

        private AnalysisResult(boolean success, Map<String, Object> analysis, String error) {
            this.success = success;
            this.analysis = analysis == null
                ? Map.of()
                : Map.copyOf(new LinkedHashMap<>(analysis));
            this.error = error;
        }

        static AnalysisResult success(Map<String, Object> analysis) {
            return new AnalysisResult(true, analysis, null);
        }

        static AnalysisResult failure(String error) {
            return new AnalysisResult(false, null, error);
        }

        public boolean isSuccess() {
            return success;
        }

        public Map<String, Object> getAnalysis() {
            return analysis;
        }

        public String getError() {
            return error;
        }
    }
}
