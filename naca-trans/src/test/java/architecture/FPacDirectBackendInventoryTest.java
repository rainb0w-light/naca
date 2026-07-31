package architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — direct-backend inventory for the independent FPac pipeline.
 * Enumerates every direct backend under {@code generate/fpacjava} that still
 * extends a semantic entity and asserts the exact checked-in baseline, so FPac
 * direct-backend debt can only decrease as backends are retired onto declarative
 * bindings + the recursive-ST4 assembly contract — never silently grow.
 *
 * <p>FPac's emitters subclass the SAME shared semantic entities as COBOL
 * ({@code semantic.Verbs.*}, {@code semantic.expression.*}, ...), so the core
 * subclass rule applies; {@code CSubStringAttributReference} is added because
 * {@code CFPacJavaSubStringAttributeReference} subclasses that shared
 * data-reference base via a wrapped declaration, and no backend may hide outside
 * the inventory. The {@code \s+} after {@code extends} tolerates that wrap.
 *
 * <p>This test owns exactly {@code generate/fpacjava}; {@link DirectBackendInventoryTest}
 * (COBOL/SQL/CICS, forms excluded) and {@link BmsFormsDirectBackendInventoryTest}
 * own the other two roots, and all three must agree with
 * {@code tools/st4-loop/st4loop/debt.py}. FPac is an independent pipeline,
 * never a COBOL dialect.
 */
class FPacDirectBackendInventoryTest
{
    // Exact current count of FPac direct backend source files. Every retirement
    // must lower this value in the same slice, preventing slack in the ratchet.
    private static final int FPAC_DIRECT_BACKEND_TOTAL_BASELINE = 36;

    // Mirrors st4loop.debt.FPAC_DIRECT_SEMANTIC_SUBCLASS exactly.
    private static final Pattern FPAC_DIRECT_SEMANTIC_SUBCLASS =
        Pattern.compile("extends\\s+(?:CEntity|CBaseActionEntity|CDataEntity|CSubStringAttributReference)");

    @Test
    @DisplayName("FPac backend inventory: checked-in ratchet equals measured total")
    void inventory() throws IOException
    {
        Path fpacRoot = moduleRoot().resolve("src/main/java/generate/fpacjava");
        assertTrue(Files.isDirectory(fpacRoot), "generate/fpacjava must exist");

        List<String> backends = new ArrayList<>();
        try (Stream<Path> files = Files.walk(fpacRoot))
        {
            for (Path file : (Iterable<Path>) files
                .filter(p -> p.toString().endsWith(".java"))::iterator)
            {
                String content = Files.readString(file, StandardCharsets.ISO_8859_1);
                if (!FPAC_DIRECT_SEMANTIC_SUBCLASS.matcher(content).find())
                {
                    continue;
                }
                backends.add(fpacRoot.relativize(file).toString());
            }
        }
        backends.sort(String::compareTo);

        StringBuilder report = new StringBuilder("\n=== FPac backend inventory (retirement map) ===\n");
        for (String backend : backends)
        {
            report.append("  ").append(backend).append('\n');
        }
        report.append(String.format("  %-14s %3d%n", "TOTAL", backends.size()));
        System.out.println(report);

        assertTrue(backends.size() == FPAC_DIRECT_BACKEND_TOTAL_BASELINE,
            "FPac backend ratchet is stale or debt changed: expected exactly "
                + FPAC_DIRECT_BACKEND_TOTAL_BASELINE + " but measured " + backends.size()
                + "; every retirement must tighten the checked-in baseline" + report);
    }

    private static Path moduleRoot()
    {
        Path current = Path.of("").toAbsolutePath().normalize();
        if (Files.isDirectory(current.resolve("src/main/java")))
        {
            return current;
        }
        Path module = current.resolve("naca-trans");
        if (Files.isDirectory(module.resolve("src/main/java")))
        {
            return module;
        }
        throw new IllegalStateException("cannot locate naca-trans module root from " + current);
    }
}
