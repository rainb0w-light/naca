package com.publicitas.naca.cloudnative.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Compiles Java source code in memory and saves .class files to a specified output directory.
 */
public class InMemoryJavaCompiler {

    private final String outputDirectory;
    private final String classpath;

    public InMemoryJavaCompiler(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        // Use current classpath for compilation
        this.classpath = System.getProperty("java.class.path");
    }

    /**
     * Compile Java source code and save .class file to output directory.
     *
     * @param className The class name (e.g., "BATCH1")
     * @param sourceCode The Java source code
     * @return CompilationResult with success status and any error messages
     */
    public CompilationResult compile(String className, String sourceCode) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return CompilationResult.failure("System Java compiler not available. Make sure you're running with JDK, not JRE.");
        }

        // Create file object for source
        JavaFileObject sourceFile = new JavaSourceCodeObject(className, sourceCode);

        // Use standard file manager for output
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            // Set output directory, classpath, and disable annotation processing
            List<String> options = Arrays.asList(
                "-d", outputDirectory,
                "-classpath", classpath,
                "-proc:none",
                "-Xlint:-options"
            );

            // Create compilation task
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            Boolean success = compiler.getTask(null, fileManager, diagnostics, options, null, Arrays.asList(sourceFile)).call();

            if (success == null || !success) {
                StringBuilder errors = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    errors.append("Line ").append(diagnostic.getLineNumber())
                          .append(": ").append(diagnostic.getMessage(null))
                          .append("\n");
                }
                return CompilationResult.failure(errors.toString());
            }

            return CompilationResult.success();

        } catch (IOException e) {
            e.printStackTrace();
            return CompilationResult.failure("Compilation IO error: " + e.getMessage());
        }
    }

    /**
     * Simple JavaFileObject implementation for in-memory source code.
     */
    private static class JavaSourceCodeObject extends SimpleJavaFileObject {
        private final String sourceCode;

        protected JavaSourceCodeObject(String className, String sourceCode) {
            super(createURI(className), Kind.SOURCE);
            this.sourceCode = sourceCode;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return sourceCode;
        }

        private static java.net.URI createURI(String className) {
            return java.net.URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension);
        }
    }

    /**
     * Compilation result holder.
     */
    public static class CompilationResult {
        private final boolean success;
        private final String errorMessage;

        private CompilationResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static CompilationResult success() {
            return new CompilationResult(true, null);
        }

        public static CompilationResult failure(String errorMessage) {
            return new CompilationResult(false, errorMessage);
        }

        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
    }
}
