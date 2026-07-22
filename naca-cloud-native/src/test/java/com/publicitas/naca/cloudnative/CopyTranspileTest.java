package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;

/**
 * Verifies the COPY/copybook include-group infrastructure: BATCH1 contains
 * {@code COPY MSGZONE}, which must resolve to the MSGZONE copybook structure
 * (fields msg_Zone/msg_No/msg_Text) instead of being silently dropped.
 */
public class CopyTranspileTest {

    private static String batch1Source;
    private static Path copybookDir;

    @BeforeAll
    static void setup() throws IOException {
        Path[] sources = {
            Path.of("NacaSamples/cobol/BATCH1.cbl"),
            Path.of("../NacaSamples/cobol/BATCH1.cbl")
        };
        for (Path p : sources) {
            if (Files.exists(p)) {
                batch1Source = Files.readString(p);
                break;
            }
        }
        Path[] includes = {
            Path.of("NacaSamples/cobol/include"),
            Path.of("../NacaSamples/cobol/include")
        };
        for (Path p : includes) {
            if (Files.isDirectory(p)) {
                copybookDir = p.toAbsolutePath();
                break;
            }
        }
    }

    @Test
    @DisplayName("COPY MSGZONE resolves to the copybook structure")
    void copyResolvesCopybook() {
        assertNotNull(batch1Source, "BATCH1.cbl should exist");
        assertNotNull(copybookDir, "copybook include dir should exist");

        String outDir = System.getProperty("java.io.tmpdir") + "/naca-copy-test-" + System.nanoTime();
        IncludeGroupSupport.configure(copybookDir.toString(), outDir);

        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(batch1Source, "BATCH1");

        assertTrue(result.isSuccess(),
            "BATCH1 transpilation should succeed: " + result.getErrors());
        String java = result.getJavaSource();
        assertNotNull(java);

        // The COPY'd MSGZONE structure must be referenced (not dropped).
        assertTrue(java.toLowerCase().contains("msgzone"),
            "Generated code should reference the COPY'd MSGZONE copybook:\n" + java);
        assertTrue(java.contains("msg_No") || java.contains("msg_Zone") || java.contains("msg_Text"),
            "Generated code should reference MSGZONE fields:\n" + java);
    }

    @Test
    @DisplayName("MSGZONE copybook transpiles to its Java Copy class")
    void copybookClassIsGenerated() {
        assertNotNull(copybookDir, "copybook include dir should exist");
        String outDir = System.getProperty("java.io.tmpdir") + "/naca-copy-class-" + System.nanoTime();
        IncludeGroupSupport.configure(copybookDir.toString(), outDir);

        String msgzone = IncludeGroupSupport.generateCopybookClass("MSGZONE");
        assertNotNull(msgzone, "MSGZONE copybook should resolve to a Java class");
        assertTrue(msgzone.contains("class Msgzone"),
            "Copybook class should declare `class Msgzone`:\n" + msgzone);
        // The copybook class must use the same lower-case identifiers as the
        // ST4-transpiled programs that reference it (msg_No, not MSG_NO), so the
        // generated program + copybook class compile together.
        assertTrue(msgzone.contains("msg_No") && msgzone.contains("msg_Text") && msgzone.contains("msg_Zone"),
            "Copybook class should declare lower-case MSGZONE fields:\n" + msgzone);
    }

    @Test
    @DisplayName("BATCH1 + generated MSGZONE copybook class compile together")
    void batch1AndCopybookCompileTogether() throws Exception {
        assertNotNull(batch1Source, "BATCH1.cbl should exist");
        assertNotNull(copybookDir, "copybook include dir should exist");

        String outDir = System.getProperty("java.io.tmpdir") + "/naca-copy-compile-" + System.nanoTime();
        IncludeGroupSupport.configure(copybookDir.toString(), outDir);
        TranspilerService service = new TranspilerService();

        String batch1 = service.transpile(batch1Source, "BATCH1").getJavaSource();
        String msgzone = IncludeGroupSupport.generateCopybookClass("MSGZONE");
        assertNotNull(batch1);
        assertNotNull(msgzone);

        Path srcDir = Path.of(outDir, "src");
        Files.createDirectories(srcDir);
        Files.writeString(srcDir.resolve("Batch1.java"), batch1);
        Files.writeString(srcDir.resolve("Msgzone.java"), msgzone);

        Path classesDir = Path.of(outDir, "classes");
        Files.createDirectories(classesDir);

        // Classpath: naca-rt + naca-jlib built classes.
        String userDir = System.getProperty("user.dir");
        Path root = Path.of(userDir).getParent();
        String classpath = root + "/naca-rt/build/classes/java/main:"
            + root + "/naca-jlib/build/classes/java/main";

        ProcessBuilder pb = new ProcessBuilder(
            "javac", "-d", classesDir.toString(), "-classpath", classpath, "-proc:none",
            srcDir.resolve("Batch1.java").toString(),
            srcDir.resolve("Msgzone.java").toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes());
        int exit = p.waitFor();
        assertEquals(0, exit, "BATCH1 + Msgzone should compile together:\n" + output);
    }
}
