package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.publicitas.naca.cloudnative.service.OnlineCorpusSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import diagnostic.DiagnosticSink;
import diagnostic.UnsupportedFeatureException;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import semantic.CBaseLanguageEntity;
import semantic.CEntityClass;
import utils.Transcoder;

/**
 * Statement-accurate fail-closed probe for the first CardDemo online slice.
 *
 * <p>The AWS corpus does not ship the compiler-provided DFHAID/DFHBMSCA
 * copybooks. The probe assembles the upstream application copybooks with the
 * small, independently maintained compatibility definitions owned by the
 * cloud-native target. Every EXEC CICS statement must then either lower to a
 * semantic node or emit a structured unsupported-feature diagnostic. A
 * statement that does neither is a silent semantic loss and fails the probe.
 */
@Tag("carddemo-online-baseline")
class CardDemoOnlineTranslationBaselineTest
{
    private static final Pattern EXEC = Pattern.compile("EXEC\\s+(SQL|CICS)\\s+([A-Z]+)");
    private static final Pattern LINE_NUMBER = Pattern.compile("(\\d+)");

    private record Statement(int line, String dialect, String command)
    {
        @Override
        public String toString()
        {
            return line + ": EXEC " + dialect + " " + command;
        }
    }

    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("COSGN00C: infrastructure resolves and every CICS statement is preserved or rejected")
    @SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "PMD.UseConcurrentHashMap"})
    void signonProgramIsFailClosed() throws Exception
    {
        Path cardDemo = locateCardDemo();
        Path cobolDir = cardDemo.resolve("app/cbl");
        Path bmsDir = temporaryDirectory.resolve("bms");
        Path includeDir = temporaryDirectory.resolve("includes");
        Path outputDir = temporaryDirectory.resolve("output");
        Files.createDirectories(includeDir);
        Files.createDirectories(bmsDir);
        Files.createDirectories(outputDir);

        copyWithoutExtension(cardDemo.resolve("app/cpy"), includeDir);
        copyWithoutExtension(cardDemo.resolve("app/cpy-bms"), includeDir);
        copyCompatibilityCopybook("DFHAID", includeDir);
        copyCompatibilityCopybook("DFHBMSCA", includeDir);

        Path source = cobolDir.resolve("COSGN00C.cbl");
        assertTrue(Files.isRegularFile(source), "COSGN00C source must be vendored");
        assertTrue(Files.isRegularFile(cardDemo.resolve("app/bms/COSGN00.bms")),
            "COSGN00 BMS mapset must be vendored for the later JSON adapter");
        assertTrue(Files.isRegularFile(includeDir.resolve("COSGN00")),
            "COSGN00 symbolic copybook must be available to COBOL analysis");

        Transcoder transcoder = OnlineCorpusSupport.build(
            cobolDir.toString(), includeDir.toString(), bmsDir.toString(), outputDir.toString());
        DiagnosticSink sink = DiagnosticSink.open();
        CEntityClass root;
        List<UnsupportedFeatureException> diagnostics;
        try
        {
            root = OnlineCorpusSupport.analyze(transcoder, "COSGN00C");
        }
        finally
        {
            diagnostics = sink.drain();
        }
        List<Statement> statements = sourceStatements(source);
        JsonNode baseline = loadProgramBaseline("COSGN00C");
        assertTrue(baseline.path("sourceExecCount").asInt() == statements.size(),
            "Pinned source EXEC count changed; regenerate the translation baseline");
        validateKnownGaps(baseline, statements);

        if (root == null)
        {
            assertTrue("BLOCKED".equals(baseline.path("status").asText()),
                "A null semantic root is allowed only while an explicit BLOCKED baseline exists");
            assertTrue(!baseline.path("firstBlocker").path("code").asText().isBlank(),
                "Blocked analysis must have a stable machine-readable blocker code");
            return;
        }

        Map<Integer, List<String>> nodesByLine = new TreeMap<>();
        collectNodesByLine(root, nodesByLine);
        Map<Integer, List<String>> diagnosticsByLine = diagnosticsByLine(diagnostics);
        List<String> silentDrops = new ArrayList<>();
        StringBuilder report = new StringBuilder("\n=== COSGN00C fail-closed inventory ===\n");
        for (Statement statement : statements)
        {
            List<String> nodes = nodesByLine.getOrDefault(statement.line(), List.of());
            List<String> rejected = diagnosticsByLine.getOrDefault(statement.line(), List.of());
            List<String> dialectNodes = nodes.stream().filter(CardDemoOnlineTranslationBaselineTest::isDialectNode)
                .toList();
            String verdict;
            if (!dialectNodes.isEmpty())
            {
                verdict = "PRESERVED (" + String.join(",", dialectNodes) + ")";
            }
            else if (!rejected.isEmpty())
            {
                verdict = "REJECTED (" + String.join(",", rejected) + ")";
            }
            else
            {
                verdict = "SILENT_DROP";
                silentDrops.add(statement.toString());
            }
            report.append(String.format("  %-28s %s%n", statement, verdict));
        }
        if (!silentDrops.isEmpty())
        {
            fail("COSGN00C silently dropped EXEC statements: " + silentDrops + report);
        }

        String generated = TemplateLoader.getRecursiveAssembler()
            .renderRoot(root, JavaTemplateRole.ROOT);
        assertTrue(generated.contains("CESM.receiveMap(")
            && generated.contains(".resp(") && generated.contains(".resp2("),
            "RECEIVE MAP RESP/RESP2 must survive generation");
        assertTrue(generated.contains("CESM.sendText("),
            "SEND TEXT must survive generation");
        assertTrue(generated.contains("CESM.assign().sysID("),
            "ASSIGN SYSID must survive generation");
        assertTrue(generated.contains("CESM.readDataSet(") && generated.contains(".execute();"),
            "READ DATASET must survive generation as an executable runtime command");
    }

    private static JsonNode loadProgramBaseline(String program) throws IOException
    {
        try (var source = CardDemoOnlineTranslationBaselineTest.class.getResourceAsStream(
            "/carddemo/translation/ONLINE_TRANSLATION_BASELINE.json"))
        {
            assertNotNull(source, "Missing CardDemo online translation baseline");
            JsonNode programs = new ObjectMapper().readTree(source).path("programs");
            for (JsonNode candidate : programs)
            {
                if (program.equals(candidate.path("program").asText()))
                {
                    return candidate;
                }
            }
        }
        throw new IllegalStateException("No online translation baseline for " + program);
    }

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private static void validateKnownGaps(JsonNode baseline, List<Statement> statements)
    {
        Map<Integer, String> commandsByLine = new LinkedHashMap<>();
        for (Statement statement : statements)
        {
            commandsByLine.put(statement.line(), statement.command());
        }
        for (JsonNode gap : baseline.path("knownGaps"))
        {
            int line = gap.path("line").asInt();
            String command = gap.path("command").asText();
            assertTrue(command.equals(commandsByLine.get(line)),
                "Stale known gap " + gap.path("code").asText() + " at source line " + line);
        }
    }

    private static Path locateCardDemo()
    {
        for (Path candidate : List.of(
            Path.of("naca-rt-tests/src/test/resources/carddemo"),
            Path.of("../naca-rt-tests/src/test/resources/carddemo")))
        {
            Path absolute = candidate.toAbsolutePath().normalize();
            if (Files.isDirectory(absolute.resolve("app/cbl")))
            {
                return absolute;
            }
        }
        throw new IllegalStateException("Vendored CardDemo corpus directory was not found");
    }

    private static void copyWithoutExtension(Path sourceDirectory, Path targetDirectory)
        throws IOException
    {
        try (Stream<Path> files = Files.list(sourceDirectory))
        {
            for (Path source : files.filter(Files::isRegularFile).toList())
            {
                String fileName = source.getFileName().toString();
                int extension = fileName.lastIndexOf('.');
                if (extension <= 0)
                {
                    continue;
                }
                Files.copy(source, targetDirectory.resolve(fileName.substring(0, extension)),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static void copyCompatibilityCopybook(String name, Path targetDirectory)
        throws IOException
    {
        try (var source = CardDemoOnlineTranslationBaselineTest.class.getResourceAsStream(
            "/carddemo/compat/copybooks/" + name))
        {
            assertNotNull(source, "Missing cloud-native compatibility copybook " + name);
            Files.copy(source, targetDirectory.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static List<Statement> sourceStatements(Path source) throws IOException
    {
        List<String> lines = Files.readAllLines(source, StandardCharsets.ISO_8859_1);
        List<Statement> statements = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++)
        {
            Matcher matcher = EXEC.matcher(lines.get(index).toUpperCase(Locale.ROOT));
            if (matcher.find())
            {
                statements.add(new Statement(index + 1, matcher.group(1), matcher.group(2)));
            }
        }
        return statements;
    }

    private static void collectNodesByLine(CBaseLanguageEntity node,
        Map<Integer, List<String>> nodesByLine)
    {
        if (node == null)
        {
            return;
        }
        nodesByLine.computeIfAbsent(node.getLine(), ignored -> new ArrayList<>())
            .add(node.getClass().getSimpleName());
        List<CBaseLanguageEntity> children = node.getSemanticChildren();
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
        String normalized = simpleClassName.toUpperCase(Locale.ROOT);
        return normalized.contains("CICS") || normalized.contains("SQL");
    }

    @SuppressWarnings({"PMD.UseConcurrentHashMap", "PMD.AvoidInstantiatingObjectsInLoops"})
    private static Map<Integer, List<String>> diagnosticsByLine(
        List<UnsupportedFeatureException> diagnostics)
    {
        Map<Integer, List<String>> byLine = new LinkedHashMap<>();
        for (UnsupportedFeatureException diagnostic : diagnostics)
        {
            Matcher matcher = LINE_NUMBER.matcher(
                diagnostic.source() == null ? "" : diagnostic.source());
            int line = matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
            byLine.computeIfAbsent(line, ignored -> new ArrayList<>()).add(diagnostic.featureId());
        }
        return byLine;
    }
}
