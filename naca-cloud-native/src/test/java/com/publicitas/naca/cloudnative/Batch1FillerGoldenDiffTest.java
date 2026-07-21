package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Guard: the production pipeline (now the recursive assembler root path) renders
 * BATCH1 token-identically to the frozen direct-generator golden, and the FILLER
 * naming stays canonical (working-storage group filler {@code Filler$1},
 * file-section attribute filler {@code Filler$2}). Comparison is
 * whitespace-insensitive (javac ignores whitespace): the assembler formats
 * differently from the legacy WriteWord/WriteLine protocol but emits the same
 * token stream. Also re-verifies BATCH1 + Msgzone still compile.
 */
public class Batch1FillerGoldenDiffTest {

    private static String batch1Source;
    private static Path copybookDir;

    private static String tokens(String s) {
        return s.replaceAll("\\s+", "");
    }

    @BeforeAll
    static void setup() throws IOException {
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol/BATCH1.cbl"),
            Path.of("../NacaSamples/cobol/BATCH1.cbl") }) {
            if (Files.exists(p)) {
                batch1Source = Files.readString(p);
                break;
            }
        }
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol/include"),
            Path.of("../NacaSamples/cobol/include") }) {
            if (Files.isDirectory(p)) {
                copybookDir = p.toAbsolutePath();
                break;
            }
        }
    }

    private static String golden() throws IOException {
        try (InputStream in =
            Batch1FillerGoldenDiffTest.class.getResourceAsStream("/golden/BATCH1_baseline.java")) {
            assertNotNull(in, "golden/BATCH1_baseline.java should be on the test classpath");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String transpileBatch1() {
        String outDir = System.getProperty("java.io.tmpdir") + "/naca-filler-diff-" + System.nanoTime();
        IncludeGroupSupport.configure(copybookDir.toString(), outDir);
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(batch1Source, "BATCH1");
        assertTrue(result.isSuccess(), "BATCH1 should transpile: " + result.getErrors());
        return result.getJavaSource();
    }

    @Test
    @DisplayName("BATCH1 output is byte-identical after the filler fix")
    void batch1OutputIsUnchanged() throws IOException {
        assertNotNull(batch1Source, "BATCH1.cbl should exist");
        assertNotNull(copybookDir, "copybook include dir should exist");

        String golden = golden();
        String actual = transpileBatch1();

        // The production exit now renders BATCH1 through the recursive assembler
        // root path. Its token stream must equal the frozen direct-generator
        // golden (whitespace differs — javac ignores it). Filler numbering follows
        // the canonical section construction order: the working-storage group
        // filler (REDEFINES SYS-TIME) is Filler$1 and the file-section attribute
        // filler (PIC X(68)) is Filler$2.
        assertEquals(tokens(golden), tokens(actual),
            "BATCH1 production output must be token-identical to the direct golden");
        assertTrue(tokens(actual).contains("VarFiller$1=declare.level(1).redefines(SYS_TIME).filler()"),
            "group filler stays Filler$1");
        assertTrue(tokens(actual).contains("VarFiller$2=declare.level(05).picX(68).filler()"),
            "attribute filler stays Filler$2");
    }

    @Test
    @DisplayName("BATCH1 + Msgzone still compile after the filler fix")
    void batch1StillCompiles() throws Exception {
        assertNotNull(batch1Source, "BATCH1.cbl should exist");
        assertNotNull(copybookDir, "copybook include dir should exist");

        String outDir = System.getProperty("java.io.tmpdir") + "/naca-filler-compile-" + System.nanoTime();
        IncludeGroupSupport.configure(copybookDir.toString(), outDir);
        TranspilerService service = new TranspilerService();
        String batch1 = service.transpile(batch1Source, "BATCH1").getJavaSource();
        String msgzone = IncludeGroupSupport.generateCopybookClass("MSGZONE");

        Path srcDir = Path.of(outDir, "src");
        Files.createDirectories(srcDir);
        Files.writeString(srcDir.resolve("Batch1.java"), batch1);
        Files.writeString(srcDir.resolve("Msgzone.java"), msgzone);
        Path classesDir = Path.of(outDir, "classes");
        Files.createDirectories(classesDir);

        Path root = Path.of(System.getProperty("user.dir")).getParent();
        String classpath = root + "/naca-rt/build/classes/java/main:"
            + root + "/naca-jlib/build/classes/java/main";
        ProcessBuilder pb = new ProcessBuilder(
            "javac", "-d", classesDir.toString(), "-classpath", classpath, "-proc:none",
            srcDir.resolve("Batch1.java").toString(),
            srcDir.resolve("Msgzone.java").toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes());
        assertEquals(0, p.waitFor(), "BATCH1 + Msgzone should compile:\n" + output);
    }
}
