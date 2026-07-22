package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;

@SpringBootTest
public class T01TranspileTest {

    @Autowired
    private TranspilerService transpilerService;

    private static String t01CobolSource;

    @BeforeAll
    static void loadCobolSource() throws IOException {
        // Try multiple paths
        Path[] possiblePaths = {
            Path.of("NacaSamples/cobol/T01.cbl"),
            Path.of("../NacaSamples/cobol/T01.cbl"),
            Path.of("naca-cloud-native/NacaSamples/cobol/T01.cbl"),
            Path.of("./NacaSamples/cobol/T01.cbl")
        };

        for (Path path : possiblePaths) {
            System.out.println("Looking for T01.cbl at: " + path.toAbsolutePath());
            if (Files.exists(path)) {
                t01CobolSource = Files.readString(path);
                System.out.println("Found T01.cbl at: " + path + ", length: " + t01CobolSource.length());
                return;
            }
        }

        t01CobolSource = null;
        System.out.println("T01.cbl not found at any location");
    }

    @Test
    @DisplayName("Transpile T01.cbl - Data Type Conversion Test")
    void testTranspileT01() {
        assertNotNull(t01CobolSource, "T01.cbl source file should exist");

        TranspileResult result = transpilerService.transpile(t01CobolSource, "T01");

        if (!result.isSuccess()) {
            System.err.println("Transpilation failed with errors:");
            result.getErrors().forEach(System.err::println);
        }

        assertTrue(result.isSuccess(), "Transpilation should succeed: " +
            (result.getErrors().isEmpty() ? "no errors" : result.getErrors()));

        String javaSource = result.getJavaSource();
        assertNotNull(javaSource, "Generated Java source should not be null");
        assertFalse(javaSource.isEmpty(), "Generated Java source should not be empty");

        // Verify the generated Java code structure
        System.out.println("=== Generated Java Source for T01 ===");
        System.out.println(javaSource);
        System.out.println("=== End of Generated Source ===");

        // Check for expected data type declarations
        assertTrue(javaSource.contains("declare") || javaSource.contains("Declare"), "Should use NacaTrans declare pattern");

        // Alphanumeric (PIC X)
        assertTrue(javaSource.contains("picX") || javaSource.contains("PIC_X"), "Should contain picX for alphanumeric");

        // Unsigned numeric (PIC 9)
        assertTrue(javaSource.contains("pic9") || javaSource.contains("PIC_9"), "Should contain pic9 for unsigned numeric");

        // Signed numeric (PIC S9)
        assertTrue(javaSource.contains("picS9") || javaSource.contains("PIC_S9"), "Should contain picS9 for signed numeric");

        // COMP-3 (packed decimal)
        assertTrue(javaSource.contains("comp3") || javaSource.contains("COMP_3"), "Should contain comp3 for packed decimal");

        // COMP (binary)
        assertTrue(javaSource.contains("comp") || javaSource.contains("COMP"), "Should contain comp for binary");

        // Decimal with V (implicit decimal)
        assertTrue(javaSource.contains("V") || javaSource.contains("pic9") && javaSource.contains("V"),
            "Should handle implicit decimal (V)");

        // Verify procedure division
        assertTrue(javaSource.contains("display"), "Should contain display() method");
        assertTrue(javaSource.contains("stopRun"), "Should contain stopRun() method");

        // Verify factory and pipeline used
        System.out.println("TranspileResult fields available");
    }

    @Test
    @DisplayName("Verify T01 variable groups structure")
    void testT01VariableStructure() {
        assertNotNull(t01CobolSource, "T01.cbl source file should exist");

        TranspileResult result = transpilerService.transpile(t01CobolSource, "T01");
        assertTrue(result.isSuccess(), "Transpilation should succeed");

        String javaSource = result.getJavaSource();

        // Check for level declarations
        assertTrue(javaSource.contains("level(1)"), "Should have level 1 group variables");
        assertTrue(javaSource.contains("level(5)") || javaSource.contains("level(05)"),
            "Should have level 5 elementary items");

        // Check for working storage section
        assertTrue(javaSource.contains("workingStorageSection") ||
                  javaSource.contains("DataSection") ||
                  javaSource.contains("declare"),
            "Should have working storage section declaration");
    }

    @Test
    @DisplayName("Verify T01 REDEFINES handling")
    void testT01Redefines() {
        assertNotNull(t01CobolSource, "T01.cbl source file should exist");

        TranspileResult result = transpilerService.transpile(t01CobolSource, "T01");
        assertTrue(result.isSuccess(), "Transpilation should succeed");

        String javaSource = result.getJavaSource();

        // REDEFINES should be handled
        // In NacaTrans, REDEFINES typically creates alternative views of the same storage
        assertTrue(javaSource.contains("redefines") ||
                  javaSource.contains("REDEFINES") ||
                  javaSource.toLowerCase().contains("redefine") ||
                  javaSource.contains("wsRawData") || javaSource.contains("wsAltNum"),
            "Should handle REDEFINES clause");
    }

    @Test
    @DisplayName("Verify T01 initial values")
    void testT01InitialValues() {
        assertNotNull(t01CobolSource, "T01.cbl source file should exist");

        TranspileResult result = transpilerService.transpile(t01CobolSource, "T01");
        assertTrue(result.isSuccess(), "Transpilation should succeed");

        String javaSource = result.getJavaSource();

        // Check for VALUE clauses being translated
        assertTrue(javaSource.contains("value") ||
                  javaSource.contains("Value") ||
                  javaSource.contains("VALUE") ||
                  javaSource.contains("valueZero"),
            "Should handle VALUE initialization");

        // Verify specific initial values are present
        assertTrue(javaSource.contains("12345") || javaSource.contains("\"12345\"") ||
                  javaSource.contains("'12345'"),
            "Should have numeric initial value 12345");
    }
}