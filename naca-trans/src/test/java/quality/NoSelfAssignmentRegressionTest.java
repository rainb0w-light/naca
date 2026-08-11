package quality;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class NoSelfAssignmentRegressionTest {
    private static final Pattern SELF_ASSIGNMENT = Pattern.compile(
            "(?<![.A-Za-z0-9_$])([A-Za-z_$][A-Za-z0-9_$]*)\\s*=\\s*\\1\\s*;");

    @Test
    void noUnqualifiedSelfAssignmentsRemainInTranspilerSources() throws IOException {
        List<String> findings = new ArrayList<>();
        Path sourceRoot = Path.of("src/main/java");

        try (var files = Files.walk(sourceRoot)) {
            files.filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
                    for (int index = 0; index < lines.size(); index++) {
                        String line = lines.get(index);
                        String trimmed = line.stripLeading();
                        if (trimmed.startsWith("//") || trimmed.startsWith("*") || trimmed.startsWith("/*")) {
                            continue;
                        }
                        Matcher matcher = SELF_ASSIGNMENT.matcher(line);
                        if (matcher.find()) {
                            findings.add(path + ":" + (index + 1) + " " + matcher.group());
                        }
                    }
                } catch (IOException exception) {
                    throw new IllegalStateException("Cannot inspect " + path, exception);
                }
            });
        }

        assertTrue(findings.isEmpty(), () -> "Unqualified self-assignments found:\n" + String.join("\n", findings));
    }
}
