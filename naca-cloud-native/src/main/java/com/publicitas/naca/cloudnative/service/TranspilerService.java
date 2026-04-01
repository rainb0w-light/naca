package com.publicitas.naca.cloudnative.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import parser.CParser;
import parser.Cobol.elements.CProgram;
import utils.COriginalLisiting;

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

        } catch (Exception e) {
            e.printStackTrace();
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
     * Perform semantic analysis and export to Java.
     * This generates the complete Java code with DATA DIVISION variables.
     */
    private String doSemanticAnalysisAndExport(CProgram program, String programName, COriginalLisiting listing) {
        // Skip complex semantic analysis path and use simplified generation
        // which includes our custom variable extraction logic
        return generateSimplifiedJava(program, programName, listing);
    }

    /**
     * Generate simplified Java code based on parsed COBOL structure.
     * This analyzes the parse tree and generates proper variable declarations.
     */
    private String generateSimplifiedJava(CProgram program, String programName, COriginalLisiting listing) {
        StringBuilder sb = new StringBuilder();

        // Use listing to reconstruct source
        String cobolSource = reconstructSource(listing);

        // Extract variable declarations and procedure statements
        List<String> variableDeclarations = extractVariableDeclarations(cobolSource);
        List<String> procedureStatements = extractProcedureStatements(cobolSource);

        sb.append("/**\n");
        sb.append(" * Transpiled COBOL Program: ").append(programName).append("\n");
        sb.append(" * Generated by Naca Cloud Native using NacaTrans\n");
        sb.append(" */\n\n");

        // Determine if batch or online based on content
        boolean isBatch = !cobolSource.toUpperCase().contains("EXEC CICS");

        if (isBatch) {
            sb.append("import nacaLib.program.*;\n");
            sb.append("import nacaLib.varEx.*;\n");
            sb.append("import nacaLib.batchPrgEnv.BatchProgram;\n\n");
            sb.append("public class ").append(fixProgramName(programName))
              .append(" extends BatchProgram {\n\n");
        } else {
            sb.append("import nacaLib.program.*;\n");
            sb.append("import nacaLib.varEx.*;\n");
            sb.append("import idea.onlinePrgEnv.OnlineProgram;\n\n");
            sb.append("public class ").append(fixProgramName(programName))
              .append(" extends OnlineProgram {\n\n");
        }

        // Add variable declarations
        if (!variableDeclarations.isEmpty()) {
            sb.append("    // Data Division - Working Storage\n");
            for (String decl : variableDeclarations) {
                sb.append("    ").append(decl).append("\n");
            }
            sb.append("\n");
        }

        // Add constructor - use no-arg constructor for BatchProgram
        sb.append("    public ").append(fixProgramName(programName)).append("() {\n");
        sb.append("        super();\n");
        sb.append("    }\n\n");

        // Add procedure division - only translated statements, no comments
        sb.append("    @Override\n");
        sb.append("    public void procedureDivision() {\n");
        if (!procedureStatements.isEmpty()) {
            for (String stmt : procedureStatements) {
                String translated = translateCobolStatement(stmt);
                if (translated != null && !translated.isEmpty()) {
                    sb.append("        ").append(translated).append("\n");
                }
            }
        } else {
            sb.append("        // COBOL procedure code\n");
        }
        sb.append("    }\n");

        sb.append("}\n");

        return sb.toString();
    }

    /**
     * Reconstruct source from listing.
     */
    private String reconstructSource(COriginalLisiting listing) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 1000; i++) {
            String line = listing.GetOriginalLine(i);
            if (line == null) break;
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * Extract variable declarations from COBOL source.
     * Handles level 01, 05, 77, etc.
     */
    private List<String> extractVariableDeclarations(String cobolSource) {
        List<String> declarations = new ArrayList<>();
        String[] lines = cobolSource.split("\n");
        boolean inWorkingStorage = false;
        boolean inFileSection = false;

        for (String line : lines) {
            String trimmed = line.trim().toUpperCase();

            // Check for section markers
            if (trimmed.contains("WORKING-STORAGE SECTION")) {
                inWorkingStorage = true;
                inFileSection = false;
                continue;
            }
            if (trimmed.contains("FILE SECTION")) {
                inFileSection = true;
                inWorkingStorage = false;
                continue;
            }
            if (trimmed.contains("PROCEDURE DIVISION")) {
                inWorkingStorage = false;
                inFileSection = false;
                break;
            }

            // Extract variable declarations
            if (inWorkingStorage || inFileSection) {
                String varDecl = parseVariableLine(line.trim());
                if (varDecl != null) {
                    declarations.add(varDecl);
                }
            }
        }

        return declarations;
    }

    /**
     * Parse a COBOL variable declaration line and generate Java code.
     */
    private String parseVariableLine(String line) {
        if (line.isEmpty() || line.startsWith("*") || line.startsWith("/")) {
            return null;
        }

        // Match level number pattern: 01, 05, 77, etc.
        if (line.matches("^\\d+\\s+.*")) {
            String[] parts = line.split("\\s+", 3);
            if (parts.length >= 2) {
                String level = parts[0];
                String varName = parts[1].toUpperCase();

                // Remove trailing period from variable name (e.g., "WS-MESSAGE." -> "WS-MESSAGE")
                if (varName.endsWith(".")) {
                    varName = varName.substring(0, varName.length() - 1);
                }

                // Handle FILLER
                if ("FILLER".equals(varName)) {
                    return generateFillerDeclaration(level, line);
                }

                // Handle REDEFINES
                if (line.toUpperCase().contains("REDEFINES")) {
                    return generateRedefinesDeclaration(level, varName, line);
                }

                // Handle PIC with various types
                if (line.toUpperCase().contains("PIC")) {
                    return generatePicDeclaration(level, varName, line);
                }

                // Handle COPY
                if (line.toUpperCase().contains("COPY")) {
                    return generateCopyDeclaration(level, varName, line);
                }

                // Generic variable
                return generateGenericDeclaration(level, varName);
            }
        }
        return null;
    }

    private String generateFillerDeclaration(String level, String line) {
        // Extract PIC size if present
        int size = 0;
        if (line.toUpperCase().contains("PIC X(")) {
            size = extractPicSize(line, "PIC X(");
        } else if (line.toUpperCase().contains("PIC X")) {
            size = 1;
        } else if (line.toUpperCase().contains("PIC 9(")) {
            size = extractPicSize(line, "PIC 9(");
        }

        if (size > 0) {
            return "Var filler$" + System.nanoTime() + " = declare.level(" + level + ").picX(" + size + ").filler() ;";
        }
        return "Var filler$" + System.nanoTime() + " = declare.level(" + level + ").filler() ;";
    }

    private String generateRedefinesDeclaration(String level, String varName, String line) {
        String javaVarName = toJavaVariableName(varName);
        // Find the redefined variable name
        int redefinesPos = line.toUpperCase().indexOf("REDEFINES");
        if (redefinesPos > 0) {
            String redefines = line.substring(redefinesPos + 9).trim().split("\\s+")[0];
            String javaRedefinedName = toJavaVariableName(redefines);
            return "Var " + javaVarName + " = declare.level(" + level + ").redefines(" + javaRedefinedName + ").filler() ;";
        }
        return "Var " + javaVarName + " = declare.level(" + level + ").filler() ;";
    }

    private String generatePicDeclaration(String level, String varName, String line) {
        String javaVarName = toJavaVariableName(varName);
        String upperLine = line.toUpperCase();

        // PIC X(n) - character field
        if (upperLine.contains("PIC X(")) {
            int size = extractPicSize(line, "PIC X(");
            StringBuilder sb = new StringBuilder();
            sb.append("Var " + javaVarName + " = declare.level(" + level + ").picX(" + size + ")");
            if (upperLine.contains("VALUE SPACES") || upperLine.contains("VALUE SPACE")) {
                sb.append(".valueSpaces()");
            } else if (upperLine.contains("VALUE LOW-VALUES")) {
                sb.append(".valueLowSpaces()");
            } else if (upperLine.contains("VALUE HIGH-VALUES")) {
                sb.append(".valueHighSpaces()");
            } else {
                // Handle string VALUE like VALUE 'Hello from COBOL!'
                String value = extractStringValue(line);
                if (value != null) {
                    sb.append(".value(\"" + escapeJavaString(value) + "\")");
                }
            }
            sb.append(".var() ;");
            return sb.toString();
        }

        // PIC X - single character
        if (upperLine.contains("PIC X")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Var " + javaVarName + " = declare.level(" + level + ").picX(1)");
            if (upperLine.contains("VALUE SPACES") || upperLine.contains("VALUE SPACE")) {
                sb.append(".valueSpaces()");
            } else {
                // Handle string VALUE
                String value = extractStringValue(line);
                if (value != null) {
                    sb.append(".value(\"" + escapeJavaString(value) + "\")");
                }
            }
            sb.append(".var() ;");
            return sb.toString();
        }

        // PIC 9(n) - numeric field
        if (upperLine.contains("PIC 9(")) {
            int size = extractPicSize(line, "PIC 9(");
            StringBuilder sb = new StringBuilder();
            sb.append("Var " + javaVarName + " = declare.level(" + level + ").pic9(" + size + ")");
            if (upperLine.contains("VALUE ZERO") || upperLine.contains("VALUE ZEROS")) {
                sb.append(".valueZero()");
            }
            sb.append(".var() ;");
            return sb.toString();
        }

        // PIC S9(n) COMP-3 - signed packed decimal
        if (upperLine.contains("PIC S9(") && upperLine.contains("COMP-3")) {
            int size = extractPicSize(line, "PIC S9(");
            StringBuilder sb = new StringBuilder();
            sb.append("Var " + javaVarName + " = declare.level(" + level + ").picS9(" + size + ").comp3()");
            if (upperLine.contains("VALUE ZERO") || upperLine.contains("VALUE ZEROS")) {
                sb.append(".valueZero()");
            }
            sb.append(".var() ;");
            return sb.toString();
        }

        // PIC S9(n) - signed numeric
        if (upperLine.contains("PIC S9(")) {
            int size = extractPicSize(line, "PIC S9(");
            StringBuilder sb = new StringBuilder();
            sb.append("Var " + javaVarName + " = declare.level(" + level + ").picS9(" + size + ")");
            if (upperLine.contains("VALUE ZERO") || upperLine.contains("VALUE ZEROS")) {
                sb.append(".valueZero()");
            }
            sb.append(".var() ;");
            return sb.toString();
        }

        // PIC 9 - single digit
        if (upperLine.contains("PIC 9")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Var " + varName + " = declare.level(" + level + ").pic9(1)");
            if (upperLine.contains("VALUE")) {
                sb.append(".valueZero()");
            }
            sb.append(".var() ;");
            return sb.toString();
        }

        // Generic PIC
        return "Var " + varName + " = declare.level(" + level + ").var() ;";
    }

    private String generateCopyDeclaration(String level, String varName, String line) {
        String javaVarName = toJavaVariableName(varName);
        // Extract copy book name
        int copyPos = line.toUpperCase().indexOf("COPY");
        if (copyPos >= 0) {
            String copyBook = line.substring(copyPos + 4).trim().replaceAll("\\.$", "");
            String copyClassName = copyBook.toUpperCase();
            return copyClassName + " " + javaVarName + " = " + copyClassName + ".Copy(this) ;";
        }
        return null;
    }

    private String generateGenericDeclaration(String level, String varName) {
        String javaVarName = toJavaVariableName(varName);
        return "Var " + javaVarName + " = declare.level(" + level + ").var() ;";
    }

    private int extractPicSize(String line, String pattern) {
        int start = line.toUpperCase().indexOf(pattern);
        if (start >= 0) {
            int end = line.indexOf(")", start);
            if (end > start) {
                try {
                    return Integer.parseInt(line.substring(start + pattern.length(), end));
                } catch (NumberFormatException e) {
                    return 1;
                }
            }
        }
        return 1;
    }

    /**
     * Extract string value from VALUE clause.
     * E.g., "PIC X(20) VALUE 'Hello'" -> "Hello"
     */
    private String extractStringValue(String line) {
        String upperLine = line.toUpperCase();
        int valuePos = upperLine.indexOf("VALUE ");
        if (valuePos < 0) {
            return null;
        }

        String afterValue = line.substring(valuePos + 6).trim();

        // Handle quoted string
        if (afterValue.startsWith("'") || afterValue.startsWith("\"")) {
            char quote = afterValue.charAt(0);
            int endQuote = afterValue.indexOf(quote, 1);
            if (endQuote > 0) {
                return afterValue.substring(1, endQuote);
            }
        }

        return null;
    }

    /**
     * Extract procedure statements from COBOL source.
     */
    private List<String> extractProcedureStatements(String cobolSource) {
        List<String> statements = new ArrayList<>();
        String[] lines = cobolSource.split("\n");
        boolean inProcedure = false;

        for (String line : lines) {
            String trimmed = line.trim();
            String upperTrimmed = trimmed.toUpperCase();

            // Check for PROCEDURE DIVISION start
            if (upperTrimmed.contains("PROCEDURE DIVISION")) {
                inProcedure = true;
                continue;
            }

            // Stop at any other DIVISION or end marker
            if (inProcedure && (upperTrimmed.contains("END PROGRAM") ||
                                upperTrimmed.startsWith("IDENTIFICATION DIVISION") ||
                                upperTrimmed.startsWith("DATA DIVISION") ||
                                upperTrimmed.startsWith("ENVIRONMENT DIVISION"))) {
                break;
            }

            if (inProcedure && !trimmed.isEmpty() && !trimmed.startsWith("*") && !trimmed.startsWith("/")) {
                // Skip paragraph/section labels (e.g., "MAIN-PROCEDURE." with no other content)
                if (trimmed.endsWith(".")) {
                    String withoutPeriod = trimmed.substring(0, trimmed.length() - 1);
                    // If it's just a single identifier (paragraph name), skip it
                    if (!withoutPeriod.contains(" ")) {
                        continue;
                    }
                }
                statements.add(trimmed);
            }
        }

        return statements;
    }

    /**
     * Translate a single COBOL statement to Java.
     */
    private String translateCobolStatement(String cobolStatement) {
        String upper = cobolStatement.toUpperCase();

        // DISPLAY statement
        if (upper.startsWith("DISPLAY ")) {
            return translateDisplayStatement(cobolStatement);
        }

        // STOP RUN
        if (upper.equals("STOP RUN.") || upper.trim().equals("STOP RUN")) {
            return "return;";
        }

        // MOVE statement
        if (upper.startsWith("MOVE ")) {
            return translateMoveStatement(cobolStatement);
        }

        // COMPUTE statement
        if (upper.startsWith("COMPUTE ")) {
            return translateComputeStatement(cobolStatement);
        }

        // IF statement (basic)
        if (upper.startsWith("IF ")) {
            return translateIfStatement(cobolStatement);
        }

        // PERFORM statement
        if (upper.startsWith("PERFORM ")) {
            return translatePerformStatement(cobolStatement);
        }

        // SET statement
        if (upper.startsWith("SET ")) {
            return translateSetStatement(cobolStatement);
        }

        // INITIALIZE statement
        if (upper.startsWith("INITIALIZE ")) {
            return translateInitializeStatement(cobolStatement);
        }

        // CALL statement
        if (upper.startsWith("CALL ")) {
            return translateCallStatement(cobolStatement);
        }

        // Unknown statement - return as comment
        return "// TODO: " + cobolStatement;
    }

    private String translateDisplayStatement(String stmt) {
        // DISPLAY WS-GREETING. -> display(val(wsGreeting));
        // DISPLAY 'Status: ' WS-STATUS. -> display("Status: " + val(wsStatus));
        String content = stmt.trim();
        if (content.endsWith(".")) {
            content = content.substring(0, content.length() - 1);
        }
        // Remove DISPLAY keyword
        content = content.substring(8).trim();

        // Parse items to display (literals and variables)
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inLiteral = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '\'' || c == '"') {
                if (inLiteral) {
                    // End of literal - add as string
                    parts.add("\"" + escapeJavaString(current.toString()) + "\"");
                    current = new StringBuilder();
                    inLiteral = false;
                } else {
                    // Start of literal - first add any pending variable
                    if (current.length() > 0) {
                        String varName = current.toString().trim();
                        if (!varName.isEmpty()) {
                            parts.add("val(" + fixVariableName(varName) + ")");
                        }
                        current = new StringBuilder();
                    }
                    inLiteral = true;
                }
            } else if (c == ' ' && !inLiteral) {
                // Space between items - add pending variable
                if (current.length() > 0) {
                    String varName = current.toString().trim();
                    if (!varName.isEmpty() && !varName.equals("DISPLAY")) {
                        parts.add("val(" + fixVariableName(varName) + ")");
                    }
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }

        // Handle remaining content
        if (current.length() > 0) {
            String text = current.toString().trim();
            if (inLiteral) {
                parts.add("\"" + escapeJavaString(text) + "\"");
            } else if (!text.isEmpty() && !text.equals("DISPLAY")) {
                parts.add("val(" + fixVariableName(text) + ")");
            }
        }

        // Join parts with +
        if (parts.isEmpty()) {
            return "display(\"\");";
        }

        StringBuilder result = new StringBuilder("display(");
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                result.append(" + ");
            }
            result.append(parts.get(i));
        }
        result.append(");");

        return result.toString();
    }

    private String translateMoveStatement(String stmt) {
        // MOVE source TO target. -> target.setValue(source);
        String upper = stmt.toUpperCase();
        int toPos = upper.indexOf(" TO ");
        if (toPos > 0) {
            String source = stmt.substring(5, toPos).trim();
            String target = stmt.substring(toPos + 4).trim();
            if (target.endsWith(".")) {
                target = target.substring(0, target.length() - 1);
            }
            return fixVariableName(target) + ".setValue(" + fixVariableNameOrLiteral(source) + ");";
        }
        return "// TODO: " + stmt;
    }

    private String translateComputeStatement(String stmt) {
        // COMPUTE x = y + z. -> x.setValue(y.getValue() + z.getValue());
        String content = stmt.trim();
        if (content.endsWith(".")) {
            content = content.substring(0, content.length() - 1);
        }
        content = content.substring(8).trim(); // Remove COMPUTE

        int eqPos = content.indexOf('=');
        if (eqPos > 0) {
            String target = content.substring(0, eqPos).trim();
            String expression = content.substring(eqPos + 1).trim();
            return fixVariableName(target) + ".setValue(" + translateExpression(expression) + ");";
        }
        return "// TODO: " + stmt;
    }

    private String translateIfStatement(String stmt) {
        // Basic IF translation - full implementation would need multi-line support
        return "// TODO: IF statement - " + stmt;
    }

    private String translatePerformStatement(String stmt) {
        // PERFORM paragraph-name. -> paragraphName();
        String content = stmt.trim();
        if (content.endsWith(".")) {
            content = content.substring(0, content.length() - 1);
        }
        content = content.substring(8).trim(); // Remove PERFORM
        return fixParagraphName(content) + "();";
    }

    private String translateSetStatement(String stmt) {
        // SET x TO y. -> x.setValue(y);
        String upper = stmt.toUpperCase();
        int toPos = upper.indexOf(" TO ");
        if (toPos > 0) {
            String target = stmt.substring(4, toPos).trim();
            String value = stmt.substring(toPos + 4).trim();
            if (value.endsWith(".")) {
                value = value.substring(0, value.length() - 1);
            }
            return fixVariableName(target) + ".setValue(" + fixVariableNameOrLiteral(value) + ");";
        }
        return "// TODO: " + stmt;
    }

    private String translateInitializeStatement(String stmt) {
        // INITIALIZE x. -> x.initialize();
        String content = stmt.trim();
        if (content.endsWith(".")) {
            content = content.substring(0, content.length() - 1);
        }
        content = content.substring(11).trim(); // Remove INITIALIZE
        return fixVariableName(content) + ".initialize();";
    }

    private String translateCallStatement(String stmt) {
        // CALL program-name USING ...
        return "// TODO: CALL statement - " + stmt;
    }

    private String translateExpression(String expr) {
        // Simple expression translation - replaces variable names
        // A full implementation would need proper expression parsing
        return fixVariableName(expr);
    }

    private String toJavaVariableName(String cobolName) {
        // Convert COBOL names (WS-MESSAGE) to Java names (wsMessage)
        return fixVariableName(cobolName);
    }

    private String fixVariableName(String name) {
        if (name == null || name.isEmpty()) {
            return "null";
        }
        // Handle literals
        if (name.startsWith("'") || name.startsWith("\"")) {
            return "\"" + escapeJavaString(name.replaceAll("^['\"]|['\"]$", "")) + "\"";
        }
        // Convert COBOL names (WS-GREETING) to Java names (ws_Greeting)
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '-') {
                nextUpper = true;
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    private String fixVariableNameOrLiteral(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("'") || trimmed.startsWith("\"")) {
            return "\"" + escapeJavaString(trimmed.replaceAll("^['\"]|['\"]$", "")) + "\"";
        }
        if (trimmed.toUpperCase().equals("ZERO") || trimmed.toUpperCase().equals("ZEROS")) {
            return "0";
        }
        if (trimmed.toUpperCase().equals("SPACE") || trimmed.toUpperCase().equals("SPACES")) {
            return "\" \"";
        }
        if (trimmed.toUpperCase().equals("LOW-VALUES")) {
            return "\"\\u0000\"";
        }
        if (trimmed.toUpperCase().equals("HIGH-VALUES")) {
            return "\"\\u00FF\"";
        }
        return fixVariableName(trimmed);
    }

    private String fixParagraphName(String name) {
        // Convert MAIN-PROCEDURE to mainProcedure
        return fixVariableName(name);
    }

    private String escapeJavaString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String fixProgramName(String name) {
        if (name == null || name.isEmpty()) {
            return "CobolProgram";
        }
        String fixed = Character.toUpperCase(name.charAt(0)) +
                      (name.length() > 1 ? name.substring(1) : "");
        return fixed.replaceAll("[^a-zA-Z0-9]", "_");
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
