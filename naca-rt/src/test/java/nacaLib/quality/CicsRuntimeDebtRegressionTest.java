package nacaLib.quality;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/** Prevents silent CICS placeholder implementations from returning. */
class CicsRuntimeDebtRegressionTest
{
    @Test
    void baseCesmManagerContainsNoTodoOrPlaceholderComment() throws Exception
    {
        Path source = locateSourceRoot()
            .resolve("nacaLib/basePrgEnv/BaseCESMManager.java");
        String text = Files.readString(source, StandardCharsets.ISO_8859_1)
            .toLowerCase(Locale.ROOT);

        assertFalse(text.contains("todo"), "BaseCESMManager must not contain TODO debt");
        assertFalse(text.contains("fake method"),
            "BaseCESMManager must not silently claim placeholder behavior");
        assertFalse(text.contains("not implemented"),
            "unsupported CICS behavior must fail closed explicitly");
    }

    private static Path locateSourceRoot()
    {
        for (Path candidate : new Path[] {
            Path.of("src/main/java"), Path.of("naca-rt/src/main/java")})
        {
            if (Files.isDirectory(candidate))
            {
                return candidate.toAbsolutePath().normalize();
            }
        }
        throw new IllegalStateException("naca-rt production source root not found");
    }
}
