package com.publicitas.naca.tests.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** End-to-end acceptance for batch files, COPY data, and a dynamically called program. */
@Tag("sample-acceptance")
class BatchMultiProgramAcceptanceTest {

    private static final String BATCH_PROGRAM = "BATCH1";
    private static final String CALLED_PROGRAM = "CALLMSG";
    private static final String COPYBOOK = "MSGZONE";

    @TempDir
    Path workspace;

    @Test
    void batchProgramReadsAndWritesFilesAndCallsAnotherProgram() throws Exception {
        Path corpus = locateCorpus();
        Path sourceDirectory = corpus.resolve("source/cobol");
        Path copybookDirectory = corpus.resolve("source/copybooks");
        Path inputFixture = corpus.getParent().resolve("testdata/FILEIN.dat");
        assertTrue(Files.isRegularFile(inputFixture), "missing batch input: " + inputFixture);

        IncludeGroupSupport.configure(copybookDirectory.toString(), workspace.toString());
        TranspilerService service = new TranspilerService();
        String batchSource = transpile(service, sourceDirectory.resolve("BATCH1.cbl"), BATCH_PROGRAM);
        String calledSource = transpile(service, sourceDirectory.resolve("CALLMSG.cbl"), CALLED_PROGRAM);
        String copybookSource = IncludeGroupSupport.generateCopybookClass(COPYBOOK);
        assertNotNull(copybookSource, "MSGZONE copybook generation failed");

        Path classes = workspace.resolve("java-classes");
        Files.createDirectories(classes);
        compileJava(classes, batchSource, calledSource, copybookSource);

        Path input = workspace.resolve("FILEIN.dat");
        Path output = workspace.resolve("FILEOUT.dat");
        Files.copy(inputFixture, input, StandardCopyOption.REPLACE_EXISTING);
        ProcessResult result = runJava(classes, input, output);
        assertEquals(0, result.exitCode(), "generated batch execution failed:\n" + result.output());

        List<String> businessOutput = result.output().lines()
            .filter(line -> line.startsWith("DEBUG 1")
                || line.startsWith("DEBUG 2")
                || line.startsWith("STAT "))
            .toList();
        assertEquals(List.of(
            "DEBUG 1 - 1This is test record 1 with code 1 - should be written to FILEOUT now",
            "DEBUG 2 - Problem BATCH" + " ".repeat(65),
            "DEBUG 1 - 1This is test record 3 with code 1 - should be written to FILEOUT now",
            "STAT FILEIN  - READ RECORDS   : 0000003",
            "STAT FILEOUT - WRITE RECORDS  : 0000002"), businessOutput,
            diagnosticSource(batchSource, calledSource));

        assertTrue(Files.isRegularFile(output), "BATCH1 did not create FILEOUT");
        assertEquals(List.of(
            "1This is test record 1 with code 1 - should be written to FILEOUT now",
            "1This is test record 3 with code 1 - should be written to FILEOUT now"),
            Files.readAllLines(output, StandardCharsets.ISO_8859_1));
    }

    private static String transpile(TranspilerService service, Path source, String programName)
            throws Exception {
        assertTrue(Files.isRegularFile(source), "missing COBOL source: " + source);
        TranspileResult result = service.transpile(
            Files.readString(source, StandardCharsets.ISO_8859_1), programName);
        assertTrue(result.isSuccess(), programName + " transpilation failed: " + result.getErrors());
        assertNotNull(result.getJavaSource(), programName + " returned no Java source");
        return result.getJavaSource();
    }

    private void compileJava(Path classes, String batch, String called, String copybook)
            throws Exception {
        Path sources = workspace.resolve("java-sources");
        Files.createDirectories(sources);
        Path batchFile = writeJava(sources, "Batch1.java", batch);
        Path calledFile = writeJava(sources, "Callmsg.java", called);
        Path copybookFile = writeJava(sources, "Msgzone.java", copybook);
        ProcessResult result = runProcess(workspace,
            "javac", "-proc:none", "-classpath", System.getProperty("java.class.path"),
            "-d", classes.toString(), batchFile.toString(), calledFile.toString(),
            copybookFile.toString());
        assertEquals(0, result.exitCode(), "generated Java compilation failed:\n" + result.output());
    }

    private static Path writeJava(Path directory, String name, String source) throws Exception {
        Path file = directory.resolve(name);
        Files.writeString(file, source, StandardCharsets.ISO_8859_1);
        return file;
    }

    private static String diagnosticSource(String batch, String called) {
        return "Generated linkage/call lines:\n" + (batch + "\n" + called).lines()
            .filter(line -> line.contains("call(")
                || line.contains("callParameters")
                || line.contains("msg_"))
            .reduce("", (left, right) -> left + right + "\n");
    }

    private ProcessResult runJava(Path classes, Path input, Path output) throws Exception {
        String classpath = System.getProperty("java.class.path") + File.pathSeparator + classes;
        return runProcess(workspace,
            "java", "-cp", classpath,
            "com.publicitas.naca.tests.acceptance.AcceptanceProgramRunner",
            "Batch1", classes.toString(), input.toString(), output.toString());
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
