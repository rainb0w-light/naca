package jlib.quality;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Prevents parameter-shadowing assignments from returning to maintained JLib code. */
class NoSelfAssignmentRegressionTest {

    private static final Pattern SELF_ASSIGNMENT = Pattern.compile(
        "^\\s*([A-Za-z_$][\\w$]*)\\s*=\\s*\\1\\s*;.*$");

    @Test
    void productionLibraryHasNoUnqualifiedSelfAssignments() throws Exception {
        Path sourceRoot = locateSourceRoot();
        List<String> findings = new ArrayList<>();
        try (var paths = Files.walk(sourceRoot)) {
            for (Path path : paths.filter(file -> file.toString().endsWith(".java")).toList()) {
                List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
                for (int index = 0; index < lines.size(); index++) {
                    if (SELF_ASSIGNMENT.matcher(lines.get(index)).matches()) {
                        findings.add(sourceRoot.relativize(path) + ":" + (index + 1));
                    }
                }
            }
        }
        assertEquals(List.of(), findings,
            "use an explicit receiver or the actual renamed target field");
    }

    private static Path locateSourceRoot() {
        for (Path candidate : List.of(
            Path.of("src/main/java"), Path.of("naca-jlib/src/main/java"))) {
            if (Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }
        throw new IllegalStateException("naca-jlib production source root not found");
    }
}
