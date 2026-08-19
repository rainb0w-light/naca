package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;

/**
 * Verifies the fully-migrated GO TO verb: the semantic {@code CEntityGoto} is
 * constructed directly by the factory (no backend subclass) and rendered via the
 * recursive assembler. Uses VERBS.cbl (which contains GO TO statements) and
 * checks every {@code goTo(target)} matches a declared paragraph method.
 */
public class GotoTranspileTest {

    private static String verbsSource;

    @BeforeAll
    static void setup() throws IOException {
        Path[] sources = {
            Path.of("naca-rt-tests/src/test/resources/naca-samples/source/cobol/VERBS.cbl"),
            Path.of("../naca-rt-tests/src/test/resources/naca-samples/source/cobol/VERBS.cbl")
        };
        for (Path p : sources) {
            if (Files.exists(p)) {
                verbsSource = Files.readString(p);
                break;
            }
        }
    }

    @Test
    @DisplayName("GO TO renders goTo targets matching the paragraph method names")
    void gotoTargetsMatchParagraphs() {
        assertNotNull(verbsSource, "VERBS.cbl should exist");
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(verbsSource, "VERBS");
        assertTrue(result.isSuccess(), "transpile should succeed: " + result.getErrors());
        String java = result.getJavaSource();
        assertNotNull(java);

        // Collect declared paragraph method names: public void NAME()
        Set<String> methods = new HashSet<>();
        Matcher mm = Pattern.compile("public void ([A-Za-z_0-9]+)\\(\\)").matcher(java);
        while (mm.find()) {
            methods.add(mm.group(1));
        }
        assertFalse(methods.isEmpty(), "should declare paragraph methods");

        // Every goTo(target) must reference a declared method.
        Matcher gm = Pattern.compile("goTo\\(([A-Za-z_0-9]+)\\)").matcher(java);
        int gotoCount = 0;
        while (gm.find()) {
            gotoCount++;
            String target = gm.group(1);
            assertTrue(methods.contains(target),
                "goTo target `" + target + "` should match a declared paragraph method " + methods);
        }
        assertTrue(gotoCount > 0, "VERBS.cbl should produce at least one goTo(...) call");
    }
}
