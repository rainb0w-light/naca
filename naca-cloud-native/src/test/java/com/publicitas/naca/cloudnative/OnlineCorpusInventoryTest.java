package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.publicitas.naca.cloudnative.service.OnlineCorpusSupport;
import diagnostic.DiagnosticSink;
import diagnostic.UnsupportedFeatureException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
 * T0 fail-closed, <b>statement-accurate</b> acceptance baseline for the ONLINE
 * canonical corpus (ONLINE1).
 *
 * <p>Phase 3 rework: instead of comparing aggregate SQL/CICS semantic-node totals
 * by package (which could go green while individual statements silently vanish —
 * a false-green risk), this enumerates <em>every</em> {@code EXEC SQL}/{@code EXEC
 * CICS} source statement by {@code (source line, dialect, command)} and classifies
 * each <em>exactly once</em> as:
 * <ul>
 *   <li><b>PRESERVED</b> — a SQL/CICS semantic node exists at that source line;</li>
 *   <li><b>REJECTED</b> — a structured {@link UnsupportedFeatureException} diagnostic
 *       (feature id + dialect + source line) was recorded for that line during the
 *       analysis pass (acceptable interim state: fail-closed with a named reason);</li>
 *   <li><b>SILENT_DROP</b> — neither a node nor a diagnostic: this is a FAILURE.</li>
 * </ul>
 *
 * <p>Diagnostics are gathered non-aborting via a {@link DiagnosticSink} opened around
 * the whole-program analysis, so one unsupported statement does not stop the rest of
 * the program being inventoried. A missing required include/artifact is also a failure.
 * The test stays RED while any statement is a SILENT_DROP, pinning the exact debt that
 * the embedded SQL/CICS migration (Phase 5) must turn into PRESERVED — diagnostics are
 * an interim bridge, never a permanent substitute for migration. Tagged
 * {@code online-corpus-baseline} and excluded from the default gate (a debt baseline).
 */
@Tag("online-corpus-baseline")
class OnlineCorpusInventoryTest
{
    private static Path cobolDir;
    private static Path includeDir;
    private static Path csdFile;
    private static Path ruleFile;

    /** Real copybook files in the Includes group that must exist on disk. */
    private static final List<String> COPYBOOK_INCLUDES = List.of("VTBMSGA", "TUAZONE");

    /**
     * BMS mapset artifacts that resolve on demand through the BMS Resources group
     * (generated from the real ONLINM1.bms source), asserted in BmsArtifactContractTest
     * and re-checked here so a broken artifact contract fails the inventory.
     */
    private static final List<String> BMS_ARTIFACTS = List.of("ONLINM1", "ONLINM1S");

    private static final Pattern EXEC =
        Pattern.compile("EXEC\\s+(SQL|CICS)\\s+([A-Z]+)");

    /** One EXEC statement occurrence in the source. */
    private record Stmt(int line, String dialect, String command)
    {
        @Override
        public String toString()
        {
            return line + ": EXEC " + dialect + " " + command;
        }
    }

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
                ruleFile = base.resolve("trans/NacaTransRules.xml").toAbsolutePath().normalize();
                break;
            }
        }
    }

    /** Extracts every "EXEC <dialect> <command>" with its 1-based source line. */
    private static List<Stmt> sourceExecStatements(Path source) throws Exception
    {
        List<String> lines = Files.readAllLines(source, StandardCharsets.ISO_8859_1);
        List<Stmt> statements = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++)
        {
            Matcher m = EXEC.matcher(lines.get(i).toUpperCase());
            if (m.find())
            {
                statements.add(new Stmt(i + 1, m.group(1), m.group(2)));
            }
        }
        return statements;
    }

    /**
     * Records, per source line, the simple class names of ALL semantic nodes reached
     * in the tree, and separately flags lines that carry a SQL/CICS dialect node. A
     * statement line with a dialect node is PRESERVED by lowering; the full per-line
     * class list is reported so INCLUDE (which inlines an external/form entity, not a
     * dialect node) and any line-attribution nuance are visible for diagnosis.
     */
    private static void collectNodesByLine(CBaseLanguageEntity node,
        Map<Integer, List<String>> nodesByLine)
    {
        if (node == null)
        {
            return;
        }
        nodesByLine.computeIfAbsent(node.getLine(), k -> new ArrayList<>())
            .add(node.getClass().getSimpleName());
        List<CBaseLanguageEntity> children = node.getChildren();
        if (children != null)
        {
            for (CBaseLanguageEntity child : children)
            {
                collectNodesByLine(child, nodesByLine);
            }
        }
    }

    private static boolean isDialectNode(String simpleClassName)
    {
        // Dialect lowering produces semantic.* / generate.java.{SQL,CICS} entities.
        // INCLUDE inlines an external/form entity instead, which the per-line report
        // surfaces separately.
        return simpleClassName.contains("CICS") || simpleClassName.contains("SQL");
    }

    /** Parses the leading source line number out of a diagnostic's source span. */
    private static int diagnosticLine(UnsupportedFeatureException d)
    {
        String src = d.source();
        Matcher m = Pattern.compile("(\\d+)").matcher(src == null ? "" : src);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    @Test
    @DisplayName("ONLINE1: every EXEC statement is PRESERVED or REJECTED with a structured diagnostic; SILENT_DROP fails")
    void online1StatementAccurateInventory() throws Exception
    {
        assertTrue(cobolDir != null, "NacaSamples/cobol must exist");
        Path online1 = cobolDir.resolve("ONLINE1.cbl");
        assertTrue(Files.exists(online1), "ONLINE1.cbl must exist");

        String outputDir = System.getProperty("java.io.tmpdir") + "/naca-online-corpus-"
            + System.nanoTime();
        Transcoder transcoder = OnlineCorpusSupport.build(
            cobolDir.toString(), includeDir.toString(),
            Files.exists(csdFile) ? csdFile.toString() : null,
            Files.exists(ruleFile) ? ruleFile.toString() : null, outputDir);

        List<Stmt> sourceStatements = sourceExecStatements(online1);

        // Analyze the whole program with a diagnostic sink open so recognized-but-
        // unlowered statements are recorded (REJECTED) rather than aborting the pass.
        DiagnosticSink sink = DiagnosticSink.open();
        CEntityClass root;
        List<UnsupportedFeatureException> diagnostics;
        try
        {
            root = OnlineCorpusSupport.analyze(transcoder, "ONLINE1");
        }
        finally
        {
            diagnostics = sink.drain();
        }

        Map<Integer, List<String>> nodesByLine = new TreeMap<>();
        if (root != null)
        {
            collectNodesByLine(root, nodesByLine);
        }
        Map<Integer, List<String>> diagnosticsByLine = new LinkedHashMap<>();
        for (UnsupportedFeatureException d : diagnostics)
        {
            int line = diagnosticLine(d);
            diagnosticsByLine.computeIfAbsent(line, k -> new ArrayList<>()).add(d.featureId());
        }

        // --- Includes / BMS artifact contract (unchanged hard requirement). ---
        List<String> missingIncludes = new ArrayList<>();
        for (String inc : COPYBOOK_INCLUDES)
        {
            if (!Files.exists(includeDir.resolve(inc)))
            {
                missingIncludes.add(inc);
            }
        }
        for (String mapset : BMS_ARTIFACTS)
        {
            if (OnlineCorpusSupport.analyzeMapset(transcoder, mapset) == null)
            {
                missingIncludes.add(mapset + " (BMS artifact did not resolve from ONLINM1.bms)");
            }
        }

        // --- Per-statement classification (each statement classified exactly once). ---
        int preserved = 0;
        int rejected = 0;
        List<String> silentDrops = new ArrayList<>();
        StringBuilder report = new StringBuilder("\n=== ONLINE1 statement-accurate inventory ===\n");
        for (Stmt s : sourceStatements)
        {
            List<String> nodes = nodesByLine.get(s.line());
            List<String> diags = diagnosticsByLine.get(s.line());
            List<String> dialectNodes = new ArrayList<>();
            boolean includeResolved = false;
            if (nodes != null)
            {
                for (String cn : nodes)
                {
                    if (isDialectNode(cn))
                    {
                        dialectNodes.add(cn);
                    }
                    // EXEC SQL INCLUDE / COPY that resolved inlines an external/form
                    // entity (CJavaInline) at the statement line — preserved by
                    // resolution, not by a dialect node.
                    if ("INCLUDE".equals(s.command()) && cn.endsWith("Inline"))
                    {
                        includeResolved = true;
                    }
                }
            }
            String verdict;
            if (!dialectNodes.isEmpty())
            {
                verdict = "PRESERVED (" + String.join(",", dialectNodes) + ")";
                preserved++;
            }
            else if (includeResolved)
            {
                verdict = "PRESERVED (include resolved/inlined)";
                preserved++;
            }
            else if (diags != null && !diags.isEmpty())
            {
                verdict = "REJECTED (" + String.join(",", diags) + ")";
                rejected++;
            }
            else
            {
                verdict = "SILENT_DROP"
                    + (nodes != null ? "  [nodes@line: " + String.join(",", nodes) + "]" : "  [no node @line]");
                silentDrops.add(s.toString());
            }
            report.append(String.format("  %-28s %s%n", s, verdict));
        }
        report.append("totals: PRESERVED=").append(preserved)
            .append("  REJECTED=").append(rejected)
            .append("  SILENT_DROP=").append(silentDrops.size()).append('\n');
        if (!missingIncludes.isEmpty())
        {
            report.append("MISSING required includes/artifacts: ").append(missingIncludes).append('\n');
        }
        System.out.println(report);

        List<String> failures = new ArrayList<>();
        if (!missingIncludes.isEmpty())
        {
            failures.add("missing required includes/artifacts (no faithful parse possible): "
                + missingIncludes);
        }
        if (!silentDrops.isEmpty())
        {
            failures.add(silentDrops.size()
                + " statement(s) SILENTLY DROPPED (no semantic node, no structured diagnostic): "
                + silentDrops);
        }
        if (!failures.isEmpty())
        {
            fail("ONLINE1 statement-accurate inventory found silent drops / missing includes "
                + "(debt to close in the embedded SQL/CICS migration):\n  "
                + String.join("\n  ", failures) + report);
        }
    }
}
