package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.publicitas.naca.cloudnative.service.OnlineCorpusSupport;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import semantic.CBaseLanguageEntity;
import semantic.CEntityClass;
import utils.Transcoder;

/**
 * T0 fail-closed acceptance baseline for the ONLINE canonical corpus (ONLINE1).
 *
 * <p>This is an honest, fail-closed inventory — NOT a green-wash. It enumerates
 * every {@code EXEC SQL}/{@code EXEC CICS} source statement (by line + command)
 * and every required {@code INCLUDE}/copybook, then classifies each as:
 * <ul>
 *   <li><b>preserved</b> — a corresponding semantic node reached the tree;</li>
 *   <li><b>rejected/unsupported</b> — a structured diagnostic was produced
 *       (acceptable: fail-closed with a named reason);</li>
 *   <li><b>silent-drop</b> — the statement vanished with no node and no
 *       diagnostic: this is a FAILURE.</li>
 * </ul>
 * A missing required include is also a failure. The test is therefore RED while
 * statements are still silently dropped, tracking the debt that T3-T5 must close
 * (each fixed statement turns its line green). It is tagged
 * {@code online-corpus-baseline} and excluded from the default gate, exactly like
 * {@code finalArchitectureCheck}: a debt baseline, not a green-wash.
 *
 * <p>BMS (ONLINM1.bms) is a separate artifact inventoried by the BMS parser, not
 * proven here by a class name containing "Map".
 */
@Tag("online-corpus-baseline")
class OnlineCorpusInventoryTest
{
    private static Path cobolDir;
    private static Path includeDir;
    private static Path csdFile;

    /** Required includes/copybooks ONLINE1 references; missing => failure. */
    private static final List<String> REQUIRED_INCLUDES =
        List.of("VTBMSGA", "TUAZONE", "SQLCA", "DFHAID", "ONLINM1", "ONLINM1S");

    private static final Pattern EXEC =
        Pattern.compile("EXEC\\s+(SQL|CICS)\\s+([A-Z]+)");

    @BeforeAll
    static void locate()
    {
        for (Path base : new Path[] { Path.of("NacaSamples"), Path.of("../NacaSamples") })
        {
            Path candidate = base.resolve("cobol");
            if (Files.isDirectory(candidate))
            {
                cobolDir = candidate.toAbsolutePath().normalize();
                includeDir = candidate.resolve("include").toAbsolutePath().normalize();
                csdFile = candidate.resolve("CICSCSD.txt").toAbsolutePath().normalize();
                break;
            }
        }
    }

    /** Extracts "line: EXEC <dialect> <command>" from the source. */
    private static List<String> sourceExecStatements(Path source) throws Exception
    {
        List<String> lines = Files.readAllLines(source, StandardCharsets.ISO_8859_1);
        List<String> statements = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++)
        {
            Matcher m = EXEC.matcher(lines.get(i).toUpperCase());
            if (m.find())
            {
                statements.add((i + 1) + ": EXEC " + m.group(1) + " " + m.group(2));
            }
        }
        return statements;
    }

    /** Counts semantic nodes by SQL/CICS family (by package), recursively. */
    private static void inventory(CBaseLanguageEntity node, Map<String, Integer> counts)
    {
        if (node == null)
        {
            return;
        }
        String pkg = node.getClass().getPackage() == null
            ? "" : node.getClass().getPackage().getName();
        if (pkg.contains(".SQL"))
        {
            counts.merge("SQL:" + node.getClass().getSimpleName(), 1, Integer::sum);
        }
        else if (pkg.contains(".CICS"))
        {
            counts.merge("CICS:" + node.getClass().getSimpleName(), 1, Integer::sum);
        }
        List<CBaseLanguageEntity> children = node.getChildren();
        if (children != null)
        {
            for (CBaseLanguageEntity child : children)
            {
                inventory(child, counts);
            }
        }
    }

    @Test
    @DisplayName("ONLINE1: every EXEC statement is preserved or rejected-with-diagnostic; silent-drop fails")
    void online1FailClosedInventory() throws Exception
    {
        assertTrue(cobolDir != null, "NacaSamples/cobol must exist");
        Path online1 = cobolDir.resolve("ONLINE1.cbl");
        assertTrue(Files.exists(online1), "ONLINE1.cbl must exist");

        String outputDir = System.getProperty("java.io.tmpdir") + "/naca-online-corpus-"
            + System.nanoTime();
        Transcoder transcoder = OnlineCorpusSupport.build(
            cobolDir.toString(), includeDir.toString(),
            Files.exists(csdFile) ? csdFile.toString() : null, outputDir);

        List<String> sourceStatements = sourceExecStatements(online1);
        CEntityClass root = OnlineCorpusSupport.analyze(transcoder, "ONLINE1");

        Map<String, Integer> semanticCounts = new TreeMap<>();
        if (root != null)
        {
            inventory(root, semanticCounts);
        }
        int sqlNodes = semanticCounts.entrySet().stream()
            .filter(e -> e.getKey().startsWith("SQL:")).mapToInt(Map.Entry::getValue).sum();
        int cicsNodes = semanticCounts.entrySet().stream()
            .filter(e -> e.getKey().startsWith("CICS:")).mapToInt(Map.Entry::getValue).sum();

        long sourceSql = sourceStatements.stream().filter(s -> s.contains("EXEC SQL")).count();
        long sourceCics = sourceStatements.stream().filter(s -> s.contains("EXEC CICS")).count();

        // Missing required includes are a hard failure (the parse cannot be
        // faithful without them; empty copybooks are NOT an acceptable substitute).
        List<String> missingIncludes = new ArrayList<>();
        for (String inc : REQUIRED_INCLUDES)
        {
            if (!Files.exists(includeDir.resolve(inc)))
            {
                missingIncludes.add(inc);
            }
        }

        StringBuilder report = new StringBuilder("\n=== ONLINE1 fail-closed inventory ===\n");
        report.append("source EXEC SQL  : ").append(sourceSql)
            .append("   semantic SQL nodes: ").append(sqlNodes).append('\n');
        report.append("source EXEC CICS : ").append(sourceCics)
            .append("   semantic CICS nodes: ").append(cicsNodes).append('\n');
        for (Map.Entry<String, Integer> e : semanticCounts.entrySet())
        {
            report.append(String.format("  %-40s %d%n", e.getKey(), e.getValue()));
        }
        report.append("source statements:\n");
        for (String s : sourceStatements)
        {
            report.append("  ").append(s).append('\n');
        }
        if (!missingIncludes.isEmpty())
        {
            report.append("MISSING required includes: ").append(missingIncludes).append('\n');
        }
        System.out.println(report);

        // Fail-closed: a silent drop is a source statement with no corresponding
        // semantic node and no structured diagnostic. Until each dialect feature is
        // migrated (T3-T5) with a structured diagnostic, any source statement that
        // does not reach a semantic node is a silent drop => failure.
        List<String> failures = new ArrayList<>();
        if (!missingIncludes.isEmpty())
        {
            failures.add("missing required includes (no faithful parse possible): " + missingIncludes);
        }
        if (sqlNodes < sourceSql)
        {
            failures.add((sourceSql - sqlNodes)
                + " EXEC SQL statement(s) silently dropped (no semantic node, no diagnostic)");
        }
        if (cicsNodes < sourceCics)
        {
            failures.add((sourceCics - cicsNodes)
                + " EXEC CICS statement(s) silently dropped (no semantic node, no diagnostic)");
        }

        if (!failures.isEmpty())
        {
            fail("ONLINE1 fail-closed inventory found silent drops / missing includes "
                + "(debt to close in T3-T5):\n  " + String.join("\n  ", failures) + report);
        }
    }
}
