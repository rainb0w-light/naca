package com.publicitas.naca.tests.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Tag("sample-acceptance")
class CardDemoEndToEndAcceptanceTest
{
    private static final String COMMIT = "59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e";
    private static final String START = "START OF EXECUTION OF PROGRAM CBACT02C";
    private static final String END = "END OF EXECUTION OF PROGRAM CBACT02C";
    private static final String PROGRAM_PATH = "app/cbl/CBACT02C.cbl";
    private static final String COPYBOOK_PATH = "app/cpy/CVACT02Y.cpy";
    private static final String DATA_PATH = "app/data/ASCII/carddata.txt";
    private static final String LICENSE_PATH = "LICENSE";
    private static final Map<String, String> HASHES = Map.of(
        PROGRAM_PATH, "d290cbbbec1e25859847d9dfe6b28040cd6b09c3b71747e39de7b4713d838e76",
        COPYBOOK_PATH, "9f1c62ef31b9d541712f1ed28f79e011ea7338611f5e0f30f2e1cbf624181223",
        DATA_PATH, "da217240d2567c85f84b571aeb465171c683cfd21dad1754046f6bbb10e76c1d",
        LICENSE_PATH, "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b");

    @TempDir
    Path workspace;

    @Test
    void pinnedCardDemoRunsOfflineWithExactRecords()
    {
        assertDoesNotThrow(this::runAcceptance, "CardDemo acceptance failed");
    }

    private void runAcceptance() throws Exception
    {
        Path corpus = locateCorpus();
        Map<String, Path> files = Map.of(
            PROGRAM_PATH, corpus.resolve(PROGRAM_PATH),
            COPYBOOK_PATH, corpus.resolve(COPYBOOK_PATH),
            DATA_PATH, corpus.resolve(DATA_PATH),
            LICENSE_PATH, corpus.resolve(LICENSE_PATH));
        String provenance = Files.readString(corpus.resolve("PROVENANCE.json"));
        assertTrue(provenance.contains("\"commit\": \"" + COMMIT + "\""),
            "provenance commit is not pinned");
        assertTrue(provenance.contains("\"SPDX\": \"Apache-2.0\""),
            "provenance license is not Apache-2.0");
        for (Map.Entry<String, String> entry : HASHES.entrySet())
        {
            assertEquals(entry.getValue(), sha256(files.get(entry.getKey())),
                "hash mismatch for " + entry.getKey());
        }
        List<String> records = Files.readAllLines(files.get(DATA_PATH),
            StandardCharsets.ISO_8859_1);
        assertEquals(50, records.size(), "CardDemo input must contain 50 records");
        assertTrue(records.stream().allMatch(line -> line.length() == 150),
            "CardDemo records must each be 150 bytes");

        Path copyDir = workspace.resolve("copy");
        Files.createDirectories(copyDir);
        Files.copy(files.get(COPYBOOK_PATH), copyDir.resolve("CVACT02Y"));
        IncludeGroupSupport.configure(copyDir.toString(), workspace.resolve("includes").toString());
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(Files.readString(
            files.get(PROGRAM_PATH), StandardCharsets.ISO_8859_1), "CBACT02C");
        assertTrue(result.isSuccess(), "CBACT02C transpilation failed: " + result.getErrors());
        assertNotNull(result.getJavaSource(), "CBACT02C returned no Java source");
        String copySource = IncludeGroupSupport.generateCopybookClass("CVACT02Y");
        assertNotNull(copySource, "CVACT02Y generation failed");

        Path sourceDir = workspace.resolve("sources");
        Files.createDirectories(sourceDir);
        Path program = sourceDir.resolve("Cbact02c.java");
        Path copybook = sourceDir.resolve("Cvact02y.java");
        Files.writeString(program, result.getJavaSource(), StandardCharsets.ISO_8859_1);
        Files.writeString(copybook, copySource, StandardCharsets.ISO_8859_1);
        Path classes = workspace.resolve("classes");
        Files.createDirectories(classes);
        ProcessResult compile = runProcess("javac", "-proc:none", "-classpath",
            System.getProperty("java.class.path"), "-d", classes.toString(),
            program.toString(), copybook.toString());
        assertEquals(0, compile.exitCode(), "generated Java compilation failed: " + compile.output());

        ProcessResult run = runProcess("java", "-cp",
            System.getProperty("java.class.path") + File.pathSeparator + classes,
            "com.publicitas.naca.tests.acceptance.AcceptanceProgramRunner",
            "Cbact02c", classes.toString(), "CARDFILE",
            files.get(DATA_PATH).toString(), "ascii,fb,150");
        assertEquals(0, run.exitCode(), "CardDemo runtime failed: " + run.output());
        List<String> business = run.output().lines()
            .filter(line -> START.equals(line) || END.equals(line) || line.length() == 150)
            .toList();
        List<String> expected = new ArrayList<>();
        expected.add(START);
        expected.addAll(records);
        expected.add(END);
        assertEquals(expected, business, run.output());
    }

    private static String sha256(Path file) throws Exception
    {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(Files.readAllBytes(file)));
    }

    private static Path locateCorpus()
    {
        for (Path candidate : List.of(Path.of("src/test/resources/carddemo"),
            Path.of("naca-rt-tests/src/test/resources/carddemo")))
        {
            if (Files.isRegularFile(candidate.resolve("PROVENANCE.json")))
            {
                return candidate.toAbsolutePath().normalize();
            }
        }
        throw new IllegalStateException("CardDemo corpus not found");
    }

    private ProcessResult runProcess(String... command) throws Exception
    {
        Process process = new ProcessBuilder(command).directory(workspace.toFile())
            .redirectErrorStream(true).start();
        if (!process.waitFor(30, TimeUnit.SECONDS))
        {
            process.destroyForcibly();
            throw new AssertionError("process timed out: " + String.join(" ", command));
        }
        return new ProcessResult(process.exitValue(), new String(
            process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }

    private record ProcessResult(int exitCode, String output) { }
}
