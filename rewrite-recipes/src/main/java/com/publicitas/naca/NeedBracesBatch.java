package com.publicitas.naca;

import org.openrewrite.RecipeRun;
import org.openrewrite.Recipe;
import org.openrewrite.Result;
import org.openrewrite.internal.InMemoryLargeSourceSet;
import org.openrewrite.staticanalysis.NeedBraces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Applies the OpenRewrite control-flow braces recipe to maintained Java source trees. */
public final class NeedBracesBatch {

    private static final int FIXED_ARGUMENT_COUNT = 4;
    private static final String RECIPE_NEED_BRACES = "need-braces";
    private static final String RECIPE_ALL_VARIABLE_NAMES = "all-variable-names";

    private NeedBracesBatch() {
    }

    /**
     * Runs the reportable AST batch.
     *
     * @param args repository root, report path, apply flag, and source directories
     * @throws IOException when source or report I/O fails
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String[] args) throws IOException {
        if (args.length <= FIXED_ARGUMENT_COUNT) {
            throw new IllegalArgumentException(
                "Expected: <repository-root> <report-path> <apply:true|false> "
                    + "<recipe> <source-directory>..."
            );
        }
        Path repositoryRoot = Path.of(args[0]).toAbsolutePath().normalize();
        Path reportPath = repositoryRoot.resolve(args[1]).normalize();
        boolean apply = Boolean.parseBoolean(args[2]);
        Recipe recipe = recipe(args[3]);
        RewriteBatchSupport.requireWithin(repositoryRoot, reportPath);

        List<Path> sourceRoots = new ArrayList<>();
        for (int index = FIXED_ARGUMENT_COUNT; index < args.length; index++) {
            Path sourceRoot = repositoryRoot.resolve(args[index]).normalize();
            RewriteBatchSupport.requireWithin(repositoryRoot, sourceRoot);
            sourceRoots.add(sourceRoot);
        }
        List<Path> paths = RewriteBatchSupport.discoverJavaFiles(sourceRoots);

        RewriteBatchSupport.ParsedSources parsed = RewriteBatchSupport.parse(paths, repositoryRoot);

        RecipeRun run = recipe.run(
            new InMemoryLargeSourceSet(parsed.sources()), parsed.context()
        );
        List<Result> results = run.getChangeset().getAllResults().stream()
            .sorted(Comparator.comparing(result -> result.getAfter().getSourcePath()))
            .toList();
        if (apply) {
            RewriteBatchSupport.applyChanges(repositoryRoot, sourceRoots, results, "Braces batch");
        }
        writeReport(reportPath, recipe, apply, paths.size(), results);
        System.out.println(recipe.getDisplayName() + " batch: scanned=" + paths.size()
            + ", changedFiles=" + results.size() + ", applied=" + apply);
    }

    @SuppressWarnings({
        "PMD.InsufficientStringBufferDeclaration", "PMD.ConsecutiveLiteralAppends",
        "PMD.AvoidDuplicateLiterals"
    })
    private static void writeReport(Path reportPath, Recipe recipe, boolean applied, int scanned,
            List<Result> results) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n")
            .append("  \"schemaVersion\": 1,\n")
            .append("  \"recipe\": ").append(quote(recipe.getName())).append(",\n")
            .append("  \"sourceEncoding\": \"ISO-8859-1\",\n")
            .append("  \"applied\": ").append(applied).append(",\n")
            .append("  \"sourceFilesScanned\": ").append(scanned).append(",\n")
            .append("  \"filesChanged\": ").append(results.size()).append(",\n")
            .append("  \"changedFiles\": [");
        for (int index = 0; index < results.size(); index++) {
            String path = results.get(index).getAfter().getSourcePath().toString();
            json.append(index == 0 ? "\n" : ",\n")
                .append("    ").append(quote(path));
        }
        json.append("\n  ]\n}\n");
        Files.createDirectories(Objects.requireNonNull(reportPath.getParent()));
        Files.writeString(reportPath, json.toString());
    }

    private static String quote(String value) {
        return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    private static Recipe recipe(String name) {
        if (RECIPE_NEED_BRACES.equals(name)) {
            return new NeedBraces();
        }
        if (RECIPE_ALL_VARIABLE_NAMES.equals(name)) {
            return new NormalizeAllVariableNames();
        }
        throw new IllegalArgumentException("Unknown governance recipe: " + name);
    }
}
