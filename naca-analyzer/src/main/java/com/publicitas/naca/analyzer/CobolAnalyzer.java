package com.publicitas.naca.analyzer;

import com.mojo.algorithms.id.IncrementingIdProvider;
import com.mojo.algorithms.id.IdProvider;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.smojol.common.ast.CobolTreeVisualiser;
import org.smojol.common.ast.FlowNode;
import org.smojol.common.ast.FlowNodeService;
import org.smojol.common.dependency.ComponentsBuilder;
import org.smojol.common.dialect.LanguageDialect;
import org.smojol.common.navigation.CobolEntityNavigator;
import org.smojol.common.navigation.EntityNavigatorBuilder;
import org.smojol.common.resource.LocalFilesystemOperations;
import org.smojol.common.vm.interpreter.Breakpointer;
import org.smojol.common.vm.interpreter.CobolVmSignal;
import org.smojol.common.vm.interpreter.ExecutionInterceptor;
import org.smojol.common.vm.interpreter.ExecutionListener;
import org.smojol.common.vm.interpreter.ExecutionListeners;
import org.smojol.common.vm.interpreter.FlowControl;
import org.smojol.common.vm.stack.ExecutionContext;
import org.smojol.common.vm.strategy.UnresolvedReferenceDoNothingStrategy;
import org.smojol.common.vm.structure.CobolDataStructure;
import org.smojol.toolkit.analysis.pipeline.ParsePipeline;
import org.smojol.toolkit.analysis.pipeline.config.SourceConfig;
import org.smojol.toolkit.ast.BuildFlowNodesTask;
import org.smojol.toolkit.ast.DisplayFlowNode;
import org.smojol.toolkit.ast.FlowNodeServiceImpl;
import org.smojol.toolkit.interpreter.interpreter.CobolConditionResolver;
import org.smojol.toolkit.interpreter.interpreter.CobolInterpreterFactory;
import org.smojol.toolkit.interpreter.structure.DefaultFormat1DataStructureBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bounded integration facade for the cobol-rekt parser, control-flow model, and interpreter.
 *
 * <p>The facade owns all temporary source files and converts cobol-rekt implementation types
 * into stable Naca records before returning to callers.
 */
public final class CobolAnalyzer implements AutoCloseable {
    private static final String SOURCE_FILE = "PROGRAM.cbl";
    private static final Pattern PROGRAM_ID = Pattern.compile(
        "(?im)^\\s*PROGRAM-ID\\s*\\.?\\s*([A-Z0-9_-]+)\\s*\\.?");
    private static final Pattern ACCEPT_STATEMENT = Pattern.compile("(?im)^\\s*ACCEPT\\b");

    private final Limits limits;
    private final ExecutorService executor;

    /** Creates an analyzer with conservative service-safe limits. */
    public CobolAnalyzer() {
        this(Limits.defaults());
    }

    /** Creates an analyzer with caller-supplied resource limits. */
    public CobolAnalyzer(Limits limits) {
        this.limits = limits;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    /** Parses the source and builds a serializable AST, CFG, and data-layout summary. */
    public AnalysisResult analyze(String cobolSource) {
        validateSource(cobolSource);
        return bounded("analysis", () -> withModel(cobolSource, model -> new AnalysisResult(
            ast(model.pipeline().getTree(), new NodeBudget(limits.maxAstNodes())),
            graph(model.flowRoot()),
            dataSummary(model.dataStructures()),
            extractProgramId(cobolSource))));
    }

    /** Executes supported COBOL statements with a hard instruction budget and timeout. */
    public ExecutionResult execute(String cobolSource, String input) {
        try {
            validateSource(cobolSource);
            if ((input != null && !input.isBlank()) || ACCEPT_STATEMENT.matcher(cobolSource).find()) {
                return ExecutionResult.failure("SMOJOL ACCEPT input binding is not available");
            }
            return bounded("execution", () -> withModel(cobolSource, model -> execute(model)));
        } catch (AnalyzerException exception) {
            return ExecutionResult.failure(exception.getMessage());
        }
    }

    /** Generates deterministic GraphViz DOT from the real cobol-rekt control-flow graph. */
    public String generateCFG(String cobolSource) {
        validateSource(cobolSource);
        return bounded("CFG generation", () -> withModel(cobolSource, model -> graph(model.flowRoot()).dot()));
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }

    private ExecutionResult execute(ParsedModel model) {
        OutputCollector output = new OutputCollector();
        StepLimitInterceptor stepLimit = new StepLimitInterceptor(limits.maxExecutionSteps());
        model.flowRoot().acceptInterpreter(
            CobolInterpreterFactory.executingInterpreter(
                CobolConditionResolver.EVALUATING_RESOLVER,
                model.dataStructures(),
                List.of(stepLimit),
                new ExecutionListeners(List.of(output)),
                new NoOpBreakpointer()),
            FlowControl::CONTINUE);
        return ExecutionResult.success(output.output(), stepLimit.steps());
    }

    private <T> T withModel(String source, ModelOperation<T> operation) {
        Path directory = null;
        try {
            directory = Files.createTempDirectory("naca-analyzer-");
            Files.writeString(directory.resolve(SOURCE_FILE), source, StandardCharsets.UTF_8);

            IdProvider ids = new IncrementingIdProvider();
            ComponentsBuilder components = new ComponentsBuilder(
                new CobolTreeVisualiser(),
                new EntityNavigatorBuilder(),
                new UnresolvedReferenceDoNothingStrategy(),
                new DefaultFormat1DataStructureBuilder(),
                ids,
                new LocalFilesystemOperations());
            SourceConfig sourceConfig = new SourceConfig(
                SOURCE_FILE, directory.toString(), List.of(directory.toFile()), null);
            ParsePipeline pipeline = new ParsePipeline(sourceConfig, components, LanguageDialect.COBOL);
            CobolEntityNavigator navigator = pipeline.parse();
            CobolDataStructure dataStructures = pipeline.getDataStructures();
            ParseTree procedure = navigator.procedureDivisionBody(navigator.getRoot());
            FlowNodeService nodeService = new FlowNodeServiceImpl(navigator, dataStructures, ids);
            FlowNode root = new BuildFlowNodesTask(nodeService).run(procedure);
            root.resolve(
                new org.smojol.common.pseudocode.SmojolSymbolTable(
                    dataStructures,
                    new org.smojol.common.pseudocode.SymbolReferenceBuilder(ids)),
                dataStructures);
            return operation.apply(new ParsedModel(pipeline, dataStructures, root));
        } catch (IOException exception) {
            throw new AnalyzerException("Unable to analyze COBOL source", exception);
        } finally {
            deleteTemporaryDirectory(directory);
        }
    }

    private AstNode ast(ParseTree tree, NodeBudget budget) {
        budget.consume("AST");
        List<AstNode> children = new ArrayList<>(tree.getChildCount());
        for (int index = 0; index < tree.getChildCount(); index++) {
            children.add(ast(tree.getChild(index), budget));
        }
        int startLine = lineOf(tree, true);
        int endLine = lineOf(tree, false);
        return new AstNode(
            tree.getClass().getSimpleName(), boundedText(tree.getText()), startLine, endLine,
            List.copyOf(children));
    }

    @SuppressWarnings("deprecation")
    private ControlFlowGraph graph(FlowNode root) {
        Map<String, FlowNode> discovered = new LinkedHashMap<>();
        List<FlowEdge> edges = new ArrayList<>();
        Set<FlowNode> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<FlowNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            FlowNode current = queue.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            if (visited.size() > limits.maxCfgNodes()) {
                throw new AnalyzerLimitException("CFG node limit exceeded: " + limits.maxCfgNodes());
            }
            discovered.put(current.id(), current);
            for (FlowNode child : current.astChildren()) {
                edges.add(new FlowEdge(current.id(), child.id(), "CONTAINS"));
                queue.addLast(child);
            }
            for (FlowNode target : current.getOutgoingNodes()) {
                edges.add(new FlowEdge(current.id(), target.id(), "FLOW"));
                queue.addLast(target);
            }
        }

        List<FlowGraphNode> nodes = discovered.values().stream()
            .map(node -> new FlowGraphNode(
                node.id(), node.name(), node.type().name(), boundedText(node.originalText()),
                lineOf(node.getExecutionContext(), true)))
            .toList();
        return new ControlFlowGraph(nodes, List.copyOf(edges), dot(nodes, edges));
    }

    private static String dot(List<FlowGraphNode> nodes, List<FlowEdge> edges) {
        StringBuilder result = new StringBuilder("digraph CFG {\n  rankdir=TB;\n");
        for (FlowGraphNode node : nodes) {
            result.append("  ").append(quote(node.id())).append(" [label=")
                .append(quote(node.name())).append("];\n");
        }
        for (FlowEdge edge : edges) {
            result.append("  ").append(quote(edge.source())).append(" -> ")
                .append(quote(edge.target()));
            if ("CONTAINS".equals(edge.kind())) {
                result.append(" [style=dashed]");
            }
            result.append(";\n");
        }
        return result.append("}\n").toString();
    }

    private static String quote(String value) {
        return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    private static DataFlowSummary dataSummary(CobolDataStructure root) {
        List<DataItem> items = new ArrayList<>();
        ArrayDeque<CobolDataStructure> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            CobolDataStructure current = queue.removeFirst();
            if (current != root && current.getLevelNumber() > 0) {
                items.add(new DataItem(
                    current.name(), current.getLevelNumber(),
                    current.getDataType().abstractType().name(),
                    current.getSourceSection().name()));
            }
            queue.addAll(current.subStructures());
        }
        return new DataFlowSummary(items.size(), List.copyOf(items));
    }

    private <T> T bounded(String operation, Supplier<T> task) {
        Future<T> future = executor.submit(task::get);
        try {
            return future.get(limits.timeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            future.cancel(true);
            throw new AnalyzerLimitException(
                operation + " timed out after " + limits.timeout().toMillis() + " ms", exception);
        } catch (InterruptedException exception) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new AnalyzerException(operation + " was interrupted", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof AnalyzerException analyzerException) {
                throw analyzerException;
            }
            throw new AnalyzerException(operation + " failed: " + messageOf(cause), cause);
        }
    }

    private void validateSource(String source) {
        if (source == null || source.isBlank()) {
            throw new AnalyzerException("COBOL source is empty");
        }
        if (source.length() > limits.maxSourceCharacters()) {
            throw new AnalyzerLimitException(
                "COBOL source limit exceeded: " + limits.maxSourceCharacters() + " characters");
        }
    }

    private static String extractProgramId(String source) {
        Matcher matcher = PROGRAM_ID.matcher(source);
        return matcher.find() ? matcher.group(1) : "UNKNOWN";
    }

    private static int lineOf(ParseTree tree, boolean start) {
        if (tree instanceof ParserRuleContext context) {
            if (start && context.getStart() != null) {
                return context.getStart().getLine();
            }
            if (!start && context.getStop() != null) {
                return context.getStop().getLine();
            }
        }
        return 0;
    }

    private static String boundedText(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= 240 ? text : text.substring(0, 240) + "...";
    }

    private static String messageOf(Throwable throwable) {
        return throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage();
    }

    private static void deleteTemporaryDirectory(Path directory) {
        if (directory == null) {
            return;
        }
        try (var paths = Files.walk(directory)) {
            paths.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Best-effort cleanup; the OS can reclaim analyzer scratch files.
                }
            });
        } catch (IOException ignored) {
            // Best-effort cleanup; analysis results are already detached from the files.
        }
    }

    /** Analyzer resource limits. */
    public record Limits(
        int maxSourceCharacters,
        int maxAstNodes,
        int maxCfgNodes,
        int maxExecutionSteps,
        Duration timeout) {

        /** Returns service-safe default limits. */
        public static Limits defaults() {
            return new Limits(1_000_000, 50_000, 20_000, 100_000, Duration.ofSeconds(10));
        }

        /** Validates that every configured budget is positive. */
        public Limits {
            if (maxSourceCharacters <= 0 || maxAstNodes <= 0 || maxCfgNodes <= 0
                || maxExecutionSteps <= 0 || timeout == null || timeout.isNegative() || timeout.isZero()) {
                throw new IllegalArgumentException("Analyzer limits must be positive");
            }
        }
    }

    /** Parsed program analysis detached from cobol-rekt implementation objects. */
    public record AnalysisResult(
        AstNode ast,
        ControlFlowGraph cfg,
        DataFlowSummary dataFlowResult,
        String programId) {

        public AstNode getAst() {
            return ast;
        }

        public ControlFlowGraph getCfg() {
            return cfg;
        }

        public DataFlowSummary getDataFlowResult() {
            return dataFlowResult;
        }
    }

    /** Interpreter result including the number of bounded execution steps. */
    public record ExecutionResult(String output, boolean success, String errorMessage, int executedSteps) {
        static ExecutionResult success(String output, int steps) {
            return new ExecutionResult(output, true, null, steps);
        }

        static ExecutionResult failure(String error) {
            return new ExecutionResult(null, false, error, 0);
        }

        public String getOutput() {
            return output;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /** Stable AST node returned to API clients. */
    public record AstNode(String type, String text, int startLine, int endLine, List<AstNode> children) { }

    /** Stable control-flow graph returned to API clients. */
    public record ControlFlowGraph(List<FlowGraphNode> nodes, List<FlowEdge> edges, String dot) { }

    /** Stable CFG node. */
    public record FlowGraphNode(String id, String name, String type, String text, int line) { }

    /** Stable CFG edge. */
    public record FlowEdge(String source, String target, String kind) { }

    /** Data-layout summary derived from the parsed DATA DIVISION. */
    public record DataFlowSummary(int variableCount, List<DataItem> variables) { }

    /** One parsed COBOL data item. */
    public record DataItem(String name, int level, String type, String section) { }

    /** Base exception for invalid input, parser failures, and resource exhaustion. */
    public static class AnalyzerException extends RuntimeException {
        /** Creates an Analyzer failure with a safe caller-facing message. */
        public AnalyzerException(String message) {
            super(message);
        }

        /** Creates an Analyzer failure retaining its diagnostic cause. */
        public AnalyzerException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** Signals a configured analyzer limit. */
    public static final class AnalyzerLimitException extends AnalyzerException {
        /** Creates a limit failure with the exhausted budget in its message. */
        public AnalyzerLimitException(String message) {
            super(message);
        }

        /** Creates a limit failure retaining its diagnostic cause. */
        public AnalyzerLimitException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private record ParsedModel(
        ParsePipeline pipeline,
        CobolDataStructure dataStructures,
        FlowNode flowRoot) { }

    @FunctionalInterface
    private interface ModelOperation<T> {
        T apply(ParsedModel model);
    }

    private static final class NodeBudget {
        private final int limit;
        private int nodes;

        private NodeBudget(int limit) {
            this.limit = limit;
        }

        private void consume(String model) {
            nodes++;
            if (nodes > limit) {
                throw new AnalyzerLimitException(model + " node limit exceeded: " + limit);
            }
        }
    }

    private static final class StepLimitInterceptor implements ExecutionInterceptor {
        private final int limit;
        private int steps;

        private StepLimitInterceptor(int limit) {
            this.limit = limit;
        }

        @Override
        public CobolVmSignal run(
            Supplier<CobolVmSignal> execution,
            ExecutionContext executionContext) {
            steps++;
            if (steps > limit) {
                throw new AnalyzerLimitException("Execution step limit exceeded: " + limit);
            }
            return execution.get();
        }

        private int steps() {
            return steps;
        }
    }

    private static final class NoOpBreakpointer implements Breakpointer {
        @Override
        public void addBreakpoint(org.smojol.common.ast.FlowNodeCondition breakpoint) { }

        @Override
        public CobolVmSignal run(
            Supplier<CobolVmSignal> execution,
            ExecutionContext executionContext) {
            return execution.get();
        }
    }

    private static final class OutputCollector implements ExecutionListener {
        private final StringBuilder output = new StringBuilder();

        @Override
        public void notify(String message, FlowNode node, FlowNodeService nodeService) { }

        @Override
        public void visit(FlowNode node, FlowNodeService nodeService) {
            if (!(node instanceof DisplayFlowNode display)) {
                return;
            }
            String line = String.join("", display.getOperandExpressions().stream()
                .map(expression -> displayValue(
                    expression.evalAsString(nodeService.getDataStructures())))
                .toList());
            output.append(line).append(System.lineSeparator());
        }

        @Override
        public void visitTermination() { }

        @Override
        public void notifyTermination() { }

        private String output() {
            return output.toString();
        }

        private static String displayValue(String value) {
            if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                    || (value.startsWith("'") && value.endsWith("'")))) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
    }
}
