package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Step 7c parity: the copybook class generated through the recursive assembler
 * ({@code javaCopyClass} ROOT) is token-identical to the legacy direct re-export
 * ({@link IncludeGroupSupport#generateCopybookClassDirect}). Whitespace differs
 * (the assembler is compact, the direct uses the WriteLine protocol) but the
 * token stream — including the lower-case camelCase field identifiers the
 * transpiled programs reference — is the same.
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class CopybookClassParityTest {

    private static Path copybookDir;

    @BeforeAll
    static void setup() {
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol/include"),
            Path.of("../NacaSamples/cobol/include") }) {
            if (Files.isDirectory(p)) {
                copybookDir = p.toAbsolutePath();
                break;
            }
        }
    }

    private static String tokens(String s) {
        return s.replaceAll("\\s+", "");
    }

    @Test
    @DisplayName("MSGZONE copybook: assembler ROOT output is token-identical to the direct re-export")
    void copybookAssemblerMatchesDirect() throws IOException {
        assertNotNull(copybookDir, "copybook include dir should exist");

        // Independent parses so the direct re-export's exporter setup cannot leak
        // into the assembler render.
        IncludeGroupSupport.configure(copybookDir.toString(),
            System.getProperty("java.io.tmpdir") + "/naca-cb-direct-" + System.nanoTime());
        String direct = IncludeGroupSupport.generateCopybookClassDirect("MSGZONE");

        IncludeGroupSupport.configure(copybookDir.toString(),
            System.getProperty("java.io.tmpdir") + "/naca-cb-asm-" + System.nanoTime());
        String assembled = IncludeGroupSupport.generateCopybookClass("MSGZONE");

        assertNotNull(direct, "direct copybook generation should succeed");
        assertNotNull(assembled, "assembler copybook generation should succeed");
        assertEquals(tokens(direct), tokens(assembled),
            "assembler copybook must be token-identical to the direct re-export"
                + "\n--- direct ---\n" + direct
                + "\n--- assembled ---\n" + assembled);
    }
}
