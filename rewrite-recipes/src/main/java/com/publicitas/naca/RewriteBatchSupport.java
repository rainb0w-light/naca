package com.publicitas.naca;

import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Result;
import org.openrewrite.SourceFile;
import org.openrewrite.java.JavaParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/** Shared, fail-closed source discovery, parsing, and write support for AST batches. */
final class RewriteBatchSupport {

    private RewriteBatchSupport() {
    }

    static List<Path> discoverJavaFiles(List<Path> sourceRoots) throws IOException {
        List<Path> paths = new ArrayList<>();
        for (Path sourceRoot : sourceRoots) {
            if (!Files.isDirectory(sourceRoot)) {
                continue;
            }
            try (Stream<Path> files = Files.walk(sourceRoot)) {
                files.filter(path -> path.toString().endsWith(".java"))
                    .forEach(paths::add);
            }
        }
        paths.sort(Comparator.naturalOrder());
        return paths;
    }

    static ParsedSources parse(List<Path> paths, Path repositoryRoot) {
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
        return new ParsedSources(sources, context);
    }

    static void applyChanges(Path repositoryRoot, List<Path> sourceRoots,
            List<Result> results, String batchName) throws IOException {
        for (Result result : results) {
            if (result.getBefore() == null || result.getAfter() == null) {
                throw new IllegalStateException(batchName + " must not create or delete files");
            }
            Path destination = repositoryRoot.resolve(result.getAfter().getSourcePath()).normalize();
            if (sourceRoots.stream().noneMatch(destination::startsWith)) {
                throw new IllegalStateException(destination + " is outside configured source roots");
            }
            Files.write(destination, result.getAfter().printAllAsBytes());
        }
    }

    static void requireWithin(Path root, Path target) {
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException(target + " is outside " + root);
        }
    }

    record ParsedSources(List<SourceFile> sources, InMemoryExecutionContext context) {
    }
}
