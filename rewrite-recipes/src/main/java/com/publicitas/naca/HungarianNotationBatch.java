package com.publicitas.naca;

import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.RecipeRun;
import org.openrewrite.Result;
import org.openrewrite.SourceFile;
import org.openrewrite.internal.InMemoryLargeSourceSet;
import org.openrewrite.java.JavaParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/** Applies {@link RemoveStringHungarianPrefix} to one source tree and writes its audit report. */
public final class HungarianNotationBatch {

    private static final int ARGUMENT_COUNT = 4;

    private HungarianNotationBatch() {
    }

    /**
     * Runs one source-tree audit or apply operation.
     *
     * @param args repository root, source directory, report path, and apply flag
     * @throws IOException when a source or report cannot be read or written
     */
    @SuppressWarnings("PMD.SystemPrintln") // This class is a command-line batch entry point.
    public static void main(String[] args) throws IOException {
        if (args.length != ARGUMENT_COUNT) {
            throw new IllegalArgumentException(
                "Expected: <repository-root> <source-directory> <report-path> <apply:true|false>"
            );
        }
        Path repositoryRoot = Path.of(args[0]).toAbsolutePath().normalize();
        Path sourceDirectory = repositoryRoot.resolve(args[1]).normalize();
        Path reportPath = repositoryRoot.resolve(args[2]).normalize();
        boolean apply = Boolean.parseBoolean(args[3]);
        requireWithin(repositoryRoot, sourceDirectory);
        requireWithin(repositoryRoot, reportPath);

        List<Path> paths;
        try (Stream<Path> files = Files.walk(sourceDirectory)) {
            paths = files.filter(path -> path.toString().endsWith(".java"))
                .sorted()
                .toList();
        }

        AtomicReference<Throwable> parseFailure = new AtomicReference<>();
        InMemoryExecutionContext context = new InMemoryExecutionContext(parseFailure::set);
        JavaParser parser = JavaParser.fromJavaVersion()
            .charset(StandardCharsets.ISO_8859_1)
            .logCompilationWarningsAndErrors(false)
            .build();
        List<SourceFile> sources = parser.parse(paths, repositoryRoot, context).toList();
        if (parseFailure.get() != null) {
            throw new IllegalStateException("OpenRewrite parse failed", parseFailure.get());
        }
        if (sources.size() != paths.size()) {
            throw new IllegalStateException(
                "Parsed " + sources.size() + " files but discovered " + paths.size()
            );
        }

        RemoveStringHungarianPrefix recipe = new RemoveStringHungarianPrefix();
        RecipeRun run = recipe.run(new InMemoryLargeSourceSet(sources), context);
        List<HungarianNotationOutcomes.Row> rows = new ArrayList<>(
            run.getDataTableRows(HungarianNotationOutcomes.class)
        );
        rows.sort(Comparator.comparing(HungarianNotationOutcomes.Row::getSourcePath)
            .thenComparing(HungarianNotationOutcomes.Row::getOldName));
        List<Result> results = run.getChangeset().getAllResults();

        if (apply) {
            for (Result result : results) {
                if (result.getBefore() == null || result.getAfter() == null) {
                    throw new IllegalStateException("Rename batch must not create or delete source files");
                }
                Path destination = repositoryRoot.resolve(result.getAfter().getSourcePath()).normalize();
                requireWithin(sourceDirectory, destination);
                Files.write(destination, result.getAfter().printAllAsBytes());
            }
        }

        writeReport(reportPath, args[1], apply, paths.size(), rows, results);
        long renamed = rows.stream().filter(row -> "renamed".equals(row.getOutcome())).count();
        System.out.println("Hungarian notation batch: scanned=" + rows.size()
            + ", eligible=" + renamed + ", renamed=" + renamed
            + ", changedFiles=" + results.size() + ", applied=" + apply);
    }

    /**
     * Serializes the deterministic recipe audit. The local maps are single-threaded and the
     * deliberately explicit JSON assembly keeps the command free of a second serialization API.
     */
    @SuppressWarnings({
        "PMD.UseConcurrentHashMap", "PMD.InsufficientStringBufferDeclaration",
        "PMD.ConsecutiveLiteralAppends", "PMD.AvoidDuplicateLiterals",
        "PMD.AvoidLiteralsInIfCondition"
    })
    private static void writeReport(Path reportPath, String sourceDirectory, boolean applied,
            int filesScanned, List<HungarianNotationOutcomes.Row> rows, List<Result> results)
            throws IOException {
        Map<String, Integer> skipped = new TreeMap<>();
        List<HungarianNotationOutcomes.Row> renamedRows = new ArrayList<>();
        for (HungarianNotationOutcomes.Row row : rows) {
            if ("renamed".equals(row.getOutcome())) {
                renamedRows.add(row);
            } else {
                skipped.merge(row.getReason(), 1, Integer::sum);
            }
        }
        Map<String, Boolean> changedFiles = new LinkedHashMap<>();
        for (Result result : results) {
            if (result.getAfter() != null) {
                changedFiles.put(result.getAfter().getSourcePath().toString(), Boolean.TRUE);
            }
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n")
            .append("  \"schemaVersion\": 1,\n")
            .append("  \"recipe\": \"com.publicitas.naca.RemoveStringHungarianPrefix\",\n")
            .append("  \"astPattern\": \"java.lang.String csXxx -> xxx\",\n")
            .append("  \"sourceDirectory\": ").append(quote(sourceDirectory)).append(",\n")
            .append("  \"sourceEncoding\": \"ISO-8859-1\",\n")
            .append("  \"applied\": ").append(applied).append(",\n")
            .append("  \"sourceFilesScanned\": ").append(filesScanned).append(",\n")
            .append("  \"scanned\": ").append(rows.size()).append(",\n")
            .append("  \"eligible\": ").append(renamedRows.size()).append(",\n")
            .append("  \"renamed\": ").append(renamedRows.size()).append(",\n")
            .append("  \"filesChanged\": ").append(changedFiles.size()).append(",\n")
            .append("  \"skippedByReason\": {");
        appendIntegerMap(json, skipped, 4);
        json.append("\n  },\n  \"renamedSymbols\": [");
        for (int index = 0; index < renamedRows.size(); index++) {
            HungarianNotationOutcomes.Row row = renamedRows.get(index);
            json.append(index == 0 ? "\n" : ",\n")
                .append("    {\"file\": ").append(quote(row.getSourcePath()))
                .append(", \"kind\": ").append(quote(row.getDeclarationKind()))
                .append(", \"before\": ").append(quote(row.getOldName()))
                .append(", \"after\": ").append(quote(row.getNewName())).append('}');
        }
        json.append("\n  ]\n").append("}\n");
        Files.createDirectories(Objects.requireNonNull(reportPath.getParent()));
        Files.writeString(reportPath, json.toString());
    }

    private static void appendIntegerMap(StringBuilder json, Map<String, Integer> values, int indent) {
        int index = 0;
        String spaces = " ".repeat(indent);
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            json.append(index == 0 ? "\n" : ",\n")
                .append(spaces).append(quote(entry.getKey())).append(": ").append(entry.getValue());
            index++;
        }
    }

    private static String quote(String value) {
        return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    private static void requireWithin(Path root, Path target) {
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException(target + " is outside " + root);
        }
    }
}
