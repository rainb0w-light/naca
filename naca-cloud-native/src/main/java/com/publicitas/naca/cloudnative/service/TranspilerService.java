package com.publicitas.naca.cloudnative.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import generate.CJavaEntityFactoryST;
import generate.CStringExporter;
import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import parser.CParser;
import parser.Cobol.elements.CProgram;
import semantic.CEntityClass;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;
import utils.Transcoder;
import jlib.misc.AsciiEbcdicConverter;

@Service
public class TranspilerService {

    private static boolean autoCompileEnabled = false;
    private static String compileOutputPath = null;

    public TranspilerService() {
        // No initialization needed
    }

    /**
     * Enable auto-compile mode: after transpiling, automatically compile and register with CodeManager.
     */
    public static void enableAutoCompile(String outputPath) {
        autoCompileEnabled = true;
        compileOutputPath = outputPath;
    }

    /**
     * Transpile COBOL source code to Java using full NacaTrans pipeline.
     *
     * @param cobolSource The COBOL source code
     * @param programName The program name
     * @return TranspileResult containing success status, Java source or errors
     */
    public TranspileResult transpile(String cobolSource, String programName) {
        try {
            // Validate COBOL source
            if (cobolSource == null || cobolSource.trim().isEmpty()) {
                return TranspileResult.failure("COBOL source is empty");
            }

            // Check for basic COBOL structure
            String upperSource = cobolSource.toUpperCase();
            if (!upperSource.contains("IDENTIFICATION DIVISION")) {
                return TranspileResult.failure("Invalid COBOL: missing IDENTIFICATION DIVISION");
            }
            if (!upperSource.contains("PROGRAM-ID")) {
                return TranspileResult.failure("Invalid COBOL: missing PROGRAM-ID");
            }

            // Use full NacaTrans pipeline
            String javaSource = transpileWithFullPipeline(cobolSource, programName);

            if (javaSource == null || javaSource.isEmpty()) {
                return TranspileResult.failure("Transpilation returned empty result");
            }

            // Auto-compile if enabled (compilation failures are logged but don't fail transpilation)
            if (autoCompileEnabled && compileOutputPath != null) {
                TranspileResult compileResult = compileAndRegister(programName, javaSource);
                if (!compileResult.isSuccess()) {
                    // Log warning but still return success with Java source
                    System.err.println("Warning: Compilation failed for " + programName + ": " + compileResult.getErrors());
                    System.err.println("Java source code is still available for manual compilation.");
                }
            }

            return TranspileResult.success(javaSource);

        } catch (generate.templates.recursive.MissingTemplateRendererException e) {
            // Fail-closed by design: a construct without an ST binding aborts
            // transpilation with an actionable error rather than silently
            // falling back to the direct generator.
            return TranspileResult.failure("Transpilation failed closed (no direct fallback): "
                + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return TranspileResult.failure("Transpilation error: " + e.getMessage());
        }
    }

    /**
     * Compile Java source and register with CodeManager.
     * Uses external javac command for reliable compilation with full classpath.
     */
    private TranspileResult compileAndRegister(String programName, String javaSource) {
        try {
            System.out.println("Compiling " + programName + " to " + compileOutputPath);

            // Create output directory if it doesn't exist
            java.io.File outDir = new java.io.File(compileOutputPath);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }

            // Write source to output directory with correct filename
            java.io.File sourceFile = new java.io.File(compileOutputPath, programName + ".java");
            java.nio.file.Files.writeString(sourceFile.toPath(), javaSource);

            // Build classpath using project build directories instead of runtime classpath
            // This works because we're compiling against already-built classes
            String userDir = System.getProperty("user.dir");
            String projectRoot = new java.io.File(userDir).getParent();
            String classpath = projectRoot + "/naca-rt/build/classes/java/main:" +
                              projectRoot + "/naca-jlib/build/classes/java/main:" +
                              projectRoot + "/naca-trans/build/classes/java/main:" +
                              System.getProperty("java.home") + "/lib/jrt-fs.jar";

            // Run javac directly
            ProcessBuilder progressBar = new ProcessBuilder(
                "javac",
                "-d", compileOutputPath,
                "-classpath", classpath,
                "-proc:none",
                sourceFile.getAbsolutePath()
            );
            progressBar.redirectErrorStream(true);
            Process process = progressBar.start();

            // Read output
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                // Clean up on failure
                sourceFile.delete();
                System.err.println("Compilation failed for " + programName);
                System.err.println("Classpath used: " + classpath);
                System.err.println("Compiler output: " + output);
                return TranspileResult.failure("Compilation failed (exit " + exitCode + "): " + output);
            }

            // Clean up source file on success (keep only .class)
            sourceFile.delete();

            System.out.println("Compilation successful for " + programName);
            return TranspileResult.success(javaSource);
        } catch (Exception e) {
            e.printStackTrace();
            return TranspileResult.failure("Compilation error: " + e.getMessage());
        }
    }

    /**
     * Transpile using the full NacaTrans pipeline:
     * Lexer -> Parser -> Semantic Analysis -> Code Generation
     */
    private String transpileWithFullPipeline(String cobolSource, String programName) {
        try {
            // Create listing to capture original COBOL lines
            COriginalLisiting listing = new COriginalLisiting();

            // Register original lines
            String[] lines = cobolSource.split("\n");
            for (String line : lines) {
                listing.RegisterNewOriginalLine(line);
            }

            // Step 1: Lexing
            CTokenList tokenList = doLexing(cobolSource, listing);
            if (tokenList == null) {
                return null;
            }

            // Step 2: Parsing
            CParser<CProgram> parser = doParsing(tokenList);
            if (parser == null) {
                return null;
            }

            CProgram program = parser.GetRootElement();
            if (program == null) {
                return null;
            }

            // Step 3: Semantic Analysis and Code Generation
            String javaSource = doSemanticAnalysisAndExport(program, programName, listing);
            return javaSource;

        } catch (generate.templates.recursive.MissingTemplateRendererException e) {
            // Fail-closed: propagate so transpile() surfaces the missing binding
            // instead of collapsing to a black-box "empty result".
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Perform lexing on COBOL source.
     */
    private CTokenList doLexing(String cobolSource, COriginalLisiting listing) {
        try {
            CCobolLexer lexer = new CCobolLexer();
            InputStream inputStream = new ByteArrayInputStream(
                cobolSource.getBytes(StandardCharsets.UTF_8));

            if (!lexer.StartLexer(inputStream, listing)) {
                return null;
            }

            return lexer.GetTokenList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Perform parsing on token list.
     */
    private CParser<CProgram> doParsing(CTokenList tokenList) {
        try {
            // Create parser directly
            parser.Cobol.CCobolParser parser = new parser.Cobol.CCobolParser();
            if (!parser.StartParsing(tokenList)) {
                return null;
            }
            return parser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Perform semantic analysis and export to Java using the ST4-based pipeline.
     * Lexer -> Parser -> Semantic Analysis -> Code Generation (via ST4 Templates)
     */
    private String doSemanticAnalysisAndExport(CProgram program, String programName, COriginalLisiting listing) {
        try {
            AsciiEbcdicConverter.create();
            // A non-null global catalog is required for cross-program references
            // (CALL) and external structure lookups (COPY). When a copybook
            // directory is configured, the global catalog is backed by an include
            // group (IncludeGroupSupport) so COPY <name> resolves the copybook;
            // otherwise program references resolve against an empty registry and
            // sub-programs are treated as external calls.
            Transcoder includeTranscoder = IncludeGroupSupport.getIncludeTranscoder();
            CGlobalCatalog globalCatalog = includeTranscoder != null
                ? new CGlobalCatalog(includeTranscoder, "", "", IncludeGroupSupport.INCLUDE_GROUP_NAME)
                : new CGlobalCatalog(null, "", "", "");
            CObjectCatalog catalog = new CObjectCatalog(globalCatalog, listing, CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
            CStringExporter exporter = new CStringExporter();
            catalog.setExporter(exporter);
            CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
            factory.InitCustomCICSEntities();
            CEntityClass rootEntity = program.DoSemanticAnalysis(factory);
            if (rootEntity == null) return null;
            // Fallback: use programName parameter if parsed program ID is empty
            if (rootEntity.GetName() == null || rootEntity.GetName().isEmpty()) {
                rootEntity.SetName(programName);
            }
			return generate.templates.TemplateLoader.getRecursiveAssembler()
				.renderRoot(rootEntity, generate.templates.recursive.JavaTemplateRole.ROOT);
		} catch (generate.templates.recursive.MissingTemplateRendererException e) {
			// Fail-closed: propagate so the missing ST binding is reported, not hidden.
			throw e;
		} catch (Exception e) {
			return null;
		}
    }


    /**
     * Result class for transpilation operations.
     */
    public static class TranspileResult {
        private final boolean success;
        private final String javaSource;
        private final List<String> errors;

        private TranspileResult(boolean success, String javaSource, List<String> errors) {
            this.success = success;
            this.javaSource = javaSource;
            this.errors = errors;
        }

        public static TranspileResult success(String javaSource) {
            return new TranspileResult(true, javaSource, new ArrayList<>());
        }

        public static TranspileResult failure(String error) {
            List<String> errors = new ArrayList<>();
            errors.add(error);
            return new TranspileResult(false, null, errors);
        }

        public boolean isSuccess() { return success; }
        public String getJavaSource() { return javaSource; }
        public List<String> getErrors() { return errors; }
    }
}
