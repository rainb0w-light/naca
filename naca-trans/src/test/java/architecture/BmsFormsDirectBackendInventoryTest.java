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
 * Phase 2 (BMS_ARTIFACT) — direct-backend inventory for the independent BMS
 * map-resource pipeline. Enumerates every direct backend under
 * {@code generate/java/forms} that still extends a BMS semantic entity
 * ({@code extends CEntity* / CBaseActionEntity / CDataEntity / CResourceStrings})
 * and asserts the exact checked-in baseline, so BMS direct-backend debt can only
 * decrease as backends are retired onto declarative bindings + the recursive-ST4
 * assembly contract — never silently grow.
 *
 * <p>This is the BMS twin of {@link DirectBackendInventoryTest}: that test
 * deliberately EXCLUDES {@code forms/} from the completed COBOL/SQL/CICS ratchet,
 * and this test owns exactly that subtree. The two instruments and
 * {@code tools/st4-loop/st4loop/debt.py} must agree; {@code CResourceStrings} is
 * included because {@code CJavaResourceStrings} subclasses that BMS semantic base
 * directly and no backend may hide outside the inventory.
 *
 * <p>BMS is a CICS screen-map DSL ({@code .bms} maps), never a COBOL dialect.
 */
class BmsFormsDirectBackendInventoryTest
{
    // Exact current count of BMS direct backend source files. Every retirement
    // must lower this value in the same slice, preventing slack in the ratchet.
    private static final int BMS_DIRECT_BACKEND_TOTAL_BASELINE = 18;

    // Mirrors st4loop.debt.BMS_DIRECT_SEMANTIC_SUBCLASS exactly.
    private static final Pattern BMS_DIRECT_SEMANTIC_SUBCLASS =
        Pattern.compile(" extends (?:CEntity|CBaseActionEntity|CDataEntity|CResourceStrings)");

    @Test
    @DisplayName("BMS forms backend inventory: checked-in ratchet equals measured total")
    void inventory() throws IOException
    {
        Path formsRoot = moduleRoot().resolve("src/main/java/generate/java/forms");
        assertTrue(Files.isDirectory(formsRoot), "generate/java/forms must exist");

        List<String> backends = new ArrayList<>();
        try (Stream<Path> files = Files.walk(formsRoot))
        {
            for (Path file : (Iterable<Path>) files
                .filter(p -> p.toString().endsWith(".java"))::iterator)
            {
                String content = Files.readString(file, StandardCharsets.ISO_8859_1);
                if (!BMS_DIRECT_SEMANTIC_SUBCLASS.matcher(content).find())
                {
                    continue;
                }
                backends.add(formsRoot.relativize(file).toString());
            }
        }
        backends.sort(String::compareTo);

        StringBuilder report = new StringBuilder("\n=== BMS forms backend inventory (retirement map) ===\n");
        for (String backend : backends)
        {
            report.append("  ").append(backend).append('\n');
        }
        report.append(String.format("  %-14s %3d%n", "TOTAL", backends.size()));
        System.out.println(report);

        assertTrue(backends.size() == BMS_DIRECT_BACKEND_TOTAL_BASELINE,
            "BMS backend ratchet is stale or debt changed: expected exactly "
                + BMS_DIRECT_BACKEND_TOTAL_BASELINE + " but measured " + backends.size()
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
