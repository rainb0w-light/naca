package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Verifies that a real copybook class is generated through the recursive
 * assembler ({@code javaCopyClass} ROOT) without a direct-generator fallback.
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class CopybookClassAssemblerTest {

    private static Path copybookDir;

    @BeforeAll
    static void setup() {
        for (Path p : new Path[] {
            Path.of("naca-rt-tests/src/test/resources/naca-samples/source/copybooks"),
            Path.of("../naca-rt-tests/src/test/resources/naca-samples/source/copybooks") }) {
            if (Files.isDirectory(p)) {
                copybookDir = p.toAbsolutePath();
                break;
            }
        }
    }

    @Test
    @DisplayName("MSGZONE copybook is generated entirely by the assembler ROOT")
    void copybookAssemblerGeneratesCompleteArtifact() throws IOException {
        assertNotNull(copybookDir, "copybook include dir should exist");

        IncludeGroupSupport.configure(copybookDir.toString(),
            System.getProperty("java.io.tmpdir") + "/naca-cb-asm-" + System.nanoTime());
        String assembled = IncludeGroupSupport.generateCopybookClass("MSGZONE");

        assertNotNull(assembled, "assembler copybook generation should succeed");
        assertTrue(assembled.contains("public class Msgzone extends Copy"), assembled);
        assertTrue(assembled.contains("Var msg_Zone = declare.level(1).var() ;"), assembled);
        assertTrue(assembled.contains("Var msg_No = declare.level(05).picX(4)"), assembled);
        assertTrue(assembled.contains("Var msg_Text = declare.level(05).picX(78)"), assembled);
    }
}
