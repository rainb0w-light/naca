package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * One-off baseline capture (tag {@code baseline-capture}, excluded from the
 * default gate): transpiles BATCH1 through the current pipeline and writes the
 * generated Java to {@code src/test/resources/golden/BATCH1_baseline.java}.
 * Run via:
 * {@code ./gradlew :naca-cloud-native:test --tests '*Batch1BaselineCaptureTest*' --include-tag baseline-capture}
 * (or temporarily remove the tag). The captured file is the pre-filler-fix
 * golden used by {@code Batch1FillerGoldenDiffTest}.
 */
@Tag("baseline-capture")
public class Batch1BaselineCaptureTest {

    private static Path firstExisting(Path... candidates) {
        for (Path p : candidates) {
            if (Files.exists(p)) {
                return p;
            }
        }
        return null;
    }

    @Test
    void captureBatch1Baseline() throws Exception {
        Path batch1 = firstExisting(
            Path.of("NacaSamples/cobol/BATCH1.cbl"),
            Path.of("../NacaSamples/cobol/BATCH1.cbl"));
        Path includeDir = firstExisting(
            Path.of("NacaSamples/cobol/include"),
            Path.of("../NacaSamples/cobol/include"));
        assertNotNull(batch1, "BATCH1.cbl should exist");
        assertNotNull(includeDir, "copybook include dir should exist");

        String outDir = System.getProperty("java.io.tmpdir") + "/naca-baseline-" + System.nanoTime();
        IncludeGroupSupport.configure(includeDir.toAbsolutePath().toString(), outDir);
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(Files.readString(batch1), "BATCH1");
        assertTrue(result.isSuccess(), "BATCH1 should transpile: " + result.getErrors());
        String java = result.getJavaSource();
        assertNotNull(java);

        Path golden = firstExisting(Path.of("src/test/resources"), Path.of("naca-cloud-native/src/test/resources"));
        assertNotNull(golden, "test resources dir should exist");
        Path target = golden.resolve("golden/BATCH1_baseline.java");
        Files.createDirectories(target.getParent());
        Files.writeString(target, java);
        System.out.println("Wrote BATCH1 baseline -> " + target.toAbsolutePath()
            + " (" + java.length() + " chars)");
    }
}
