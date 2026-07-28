package architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Phase A1 — direct-backend reachability inventory. Enumerates every direct
 * {@code CJava*} backend class that still extends a semantic entity
 * ({@code extends CEntity* / CBaseActionEntity / CDataEntity}) and groups them
 * by area/dialect. This is the retirement map for the final cleanup (goal #8):
 * it shows, by area, how many direct generators remain to be migrated to the
 * declarative manifest + ST4 assembler before they can be deleted.
 *
 * <p>The total is asserted against the same baseline as the architecture ratchet
 * ({@code DIRECT_SEMANTIC_SUBCLASS_BASELINE = 209}) so it can only decrease as
 * direct backends are retired — never silently grow. The per-area counts are
 * printed as the inventory; they are the prioritized work list:
 * verbs/expressions are largely assembler-bound already (retire first); CICS /
 * SQL / forms(BMS) need vertical migration first.
 */
class DirectBackendInventoryTest
{
    // Current count of direct backend source files (high-water mark; may only
    // decrease as direct backends are retired — same assertAtMost semantics as
    // the architecture ratchet).
    private static final int DIRECT_BACKEND_TOTAL_BASELINE = 147;

    private static final Pattern DIRECT_SEMANTIC_SUBCLASS =
        Pattern.compile(" extends (?:CEntity|CBaseActionEntity|CDataEntity)");

    @Test
    @DisplayName("direct backend inventory: enumerated and grouped by area, total only decreases")
    void inventory() throws IOException
    {
        Path directRoot = moduleRoot().resolve("src/main/java/generate/java");
        assertTrue(Files.isDirectory(directRoot), "generate/java must exist");

        Map<String, Integer> byArea = new TreeMap<>();
        int total = 0;
        try (Stream<Path> files = Files.walk(directRoot))
        {
            for (Path file : (Iterable<Path>) files
                .filter(p -> p.toString().endsWith(".java"))::iterator)
            {
                String content = Files.readString(file, StandardCharsets.ISO_8859_1);
                if (!DIRECT_SEMANTIC_SUBCLASS.matcher(content).find())
                {
                    continue;
                }
                total++;
                byArea.merge(areaOf(directRoot, file), 1, Integer::sum);
            }
        }

        // The inventory, printed for planning (retirement priority by area).
        StringBuilder report = new StringBuilder("\n=== direct backend inventory (retirement map) ===\n");
        for (Map.Entry<String, Integer> entry : byArea.entrySet())
        {
            report.append(String.format("  %-14s %3d%n", entry.getKey(), entry.getValue()));
        }
        report.append(String.format("  %-14s %3d%n", "TOTAL", total));
        System.out.println(report);

        assertTrue(total <= DIRECT_BACKEND_TOTAL_BASELINE,
            "direct backend count grew from baseline " + DIRECT_BACKEND_TOTAL_BASELINE
                + " to " + total + "; new direct backends are not allowed" + report);
    }

    /** The area/dialect of a backend file: its package segment under generate/java. */
    private static String areaOf(Path directRoot, Path file)
    {
        Path relative = directRoot.relativize(file);
        if (relative.getNameCount() <= 1)
        {
            return "data/class";
        }
        return relative.getName(0).toString();
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
