package com.publicitas.naca.tests.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Canonical, strict COBOL -> Java acceptance pipeline owned by naca-rt-tests. */
@Tag("sample-acceptance")
class SampleEndToEndAcceptanceTest {

    private static final String SAMPLE = "TEST-A-STANDALONE.cbl";
    private static final String JAVA_CLASS = "Test_a";

    @TempDir
    Path workspace;

    @Test
    void generatedJavaMatchesGnuCobolForAllTestAScenarios() throws Exception {
        Path corpus = locateCorpus();
        assertFalse(Files.exists(repositoryRoot().resolve("NacaSamples")),
            "the retired root NacaSamples project must not be recreated");

        Path cobolSource = corpus.resolve("source/cobol").resolve(SAMPLE);
        assertTrue(Files.isRegularFile(cobolSource), "missing acceptance source: " + cobolSource);
        requireCommand("cobc", "--version");
        requireCommand("javac", "-version");

        List<String> expected = canonicalOutput(runGnuCobol(cobolSource));
        assertEquals(34, expected.size(), "GnuCOBOL must emit the complete 16-scenario baseline");

        TranspileResult result = new TranspilerService().transpile(
            Files.readString(cobolSource, StandardCharsets.ISO_8859_1), "TEST-A");
        assertTrue(result.isSuccess(), "Naca transpilation failed: " + result.getErrors());
        assertNotNull(result.getJavaSource(), "Naca returned no Java source");

        Path classes = workspace.resolve("java-classes");
        Files.createDirectories(classes);
        compileJava(result.getJavaSource(), classes);
        List<String> actual = canonicalOutput(runJava(classes));

        assertEquals(expected, actual,
            "NacaRT output must match GnuCOBOL for every TEST-A line and field");
    }

    private static Path locateCorpus() {
        for (Path candidate : List.of(
            Path.of("src/test/resources/naca-samples"),
            Path.of("naca-rt-tests/src/test/resources/naca-samples"))) {
            if (Files.isDirectory(candidate.resolve("source/cobol"))) {
                return candidate.toAbsolutePath().normalize();
            }
        }
        throw new IllegalStateException("naca-rt-tests sample corpus not found");
    }

    private static Path repositoryRoot() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        return Files.isRegularFile(current.resolve("settings.gradle.kts"))
            ? current
            : current.getParent();
    }

    private void requireCommand(String... command) throws Exception {
        ProcessResult result = runProcess(workspace, command);
        assertEquals(0, result.exitCode(),
            "required acceptance tool is unavailable: " + String.join(" ", command)
                + "\n" + result.output());
    }

    private String runGnuCobol(Path source) throws Exception {
        Path executable = workspace.resolve("test-a-gnucobol");
        ProcessResult compile = runProcess(workspace,
            "cobc", "-x", "-o", executable.toString(), source.toString());
        assertEquals(0, compile.exitCode(), "GnuCOBOL compilation failed:\n" + compile.output());
        ProcessResult run = runProcess(workspace, executable.toString());
        assertEquals(0, run.exitCode(), "GnuCOBOL execution failed:\n" + run.output());
        return run.output();
    }

    private void compileJava(String javaSource, Path classes) throws Exception {
        Path source = workspace.resolve(JAVA_CLASS + ".java");
        Files.writeString(source, javaSource, StandardCharsets.ISO_8859_1);
        ProcessResult compile = runProcess(workspace,
            "javac", "-proc:none", "-classpath", System.getProperty("java.class.path"),
            "-d", classes.toString(), source.toString());
        assertEquals(0, compile.exitCode(), "generated Java compilation failed:\n" + compile.output());
    }

    private String runJava(Path classes) throws Exception {
        String classpath = System.getProperty("java.class.path") + File.pathSeparator + classes;
        ProcessResult run = runProcess(workspace,
            "java", "-cp", classpath,
            "com.publicitas.naca.tests.acceptance.AcceptanceProgramRunner",
            JAVA_CLASS, classes.toString());
        assertEquals(0, run.exitCode(), "generated Java execution failed:\n" + run.output());
        return run.output();
    }

    private static List<String> canonicalOutput(String output) {
        return output.lines()
            .filter(line -> line.startsWith("=== TEST-A")
                || line.startsWith("A1-")
                || line.startsWith("=== DONE"))
            .toList();
    }

    private static ProcessResult runProcess(Path directory, String... command) throws Exception {
        Process process = new ProcessBuilder(command)
            .directory(directory.toFile())
            .redirectErrorStream(true)
            .start();
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new AssertionError("process timed out: " + String.join(" ", command));
        }
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return new ProcessResult(process.exitValue(), output);
    }

    private record ProcessResult(int exitCode, String output) {
    }
}
