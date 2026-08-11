package quality;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CommentQualityRegressionTest {
    @Test
    void generatedTodoPlaceholdersDoNotReturn() throws IOException {
        List<String> findings = new ArrayList<>();
        Path sourceRoot = Path.of("src/main/java");

        try (var files = Files.walk(sourceRoot)) {
            files.filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
                    for (int index = 0; index < lines.size(); index++) {
                        if (lines.get(index).contains("TODO Auto-generated")) {
                            findings.add(path + ":" + (index + 1));
                        }
                    }
                } catch (IOException exception) {
                    throw new IllegalStateException("Cannot inspect " + path, exception);
                }
            });
        }

        assertTrue(findings.isEmpty(), () -> "Generated TODO placeholders found:\n" + String.join("\n", findings));
    }
}
