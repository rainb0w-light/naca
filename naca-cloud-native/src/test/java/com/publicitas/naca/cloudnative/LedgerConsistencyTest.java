package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Phase 4 — consistency gate for the machine-readable migration ledger
 * ({@code docs/migration-ledger.json}, schema {@code docs/migration-ledger.schema.json}).
 *
 * <p>The ledger is the single source of truth that survives context compression and
 * tracks every production-reachable COBOL / embedded-SQL / embedded-CICS semantic type
 * on its way to the unified recursive-ST4 assembler. This test keeps it honest:
 * <ul>
 *   <li>it parses and carries the required structure (schema conformance);</li>
 *   <li>entry ids are unique and well-formed;</li>
 *   <li>every {@code status} is a known stage that can only advance forward along
 *       {@code meta.statusOrder};</li>
 *   <li>every {@code scope} is valid, and BMS entries are {@code BMS_ARTIFACT} — never a
 *       COBOL dialect scope (BMS is a separate screen-map DSL, out of the COBOL goal);</li>
 *   <li>clean fully-qualified {@code parserNode}/{@code semanticNode} references resolve
 *       to real classes (no dangling/renamed types);</li>
 *   <li>the ratchet block is present with non-negative debt counts.</li>
 * </ul>
 * It runs in the default gate and must stay green; the ledger grows per migration slice.
 */
class LedgerConsistencyTest
{
    private static final Set<String> VALID_SCOPES =
        Set.of("COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS", "BMS_ARTIFACT");
    private static final Set<String> TERMINAL_STATUSES = Set.of("direct-retired", "done");
    private static final Pattern DIRECT_BACKEND_CLASS = Pattern.compile(
        "\\bpublic\\s+(?:abstract\\s+)?class\\s+(\\w+)\\s+extends\\s+"
            + "(?:CEntity\\w*|CBaseActionEntity|CDataEntity)\\b");
    private static final Pattern PACKAGE =
        Pattern.compile("(?m)^package\\s+([\\w.]+);");

    private static Path ledgerPath;
    private static JsonNode root;
    private static List<String> statusOrder;

    @BeforeAll
    static void load() throws Exception
    {
        for (Path p : new Path[] {
            Path.of("docs/migration-ledger.json"),
            Path.of("../docs/migration-ledger.json") })
        {
            if (Files.exists(p))
            {
                ledgerPath = p;
                break;
            }
        }
        assertNotNull(ledgerPath, "docs/migration-ledger.json must exist");
        root = new ObjectMapper().readTree(ledgerPath.toFile());
        assertNotNull(root.get("meta"), "ledger must have a meta block");
        assertNotNull(root.get("entries"), "ledger must have entries");
        statusOrder = new ArrayList<>();
        root.get("meta").get("statusOrder").forEach(n -> statusOrder.add(n.asText()));
    }

    @Test
    @DisplayName("ledger has the required top-level structure and ratchet")
    void structureAndRatchet()
    {
        JsonNode meta = root.get("meta");
        assertEquals(1, meta.get("schemaVersion").asInt(), "schemaVersion must be 1");
        assertTrue(statusOrder.size() >= 2, "statusOrder must define the forward-only stages");
        assertTrue(meta.get("scopeOrder").isArray());

        JsonNode ratchet = meta.get("ratchet");
        assertNotNull(ratchet, "meta.ratchet is required");
        JsonNode fac = ratchet.get("finalArchitectureCheck");
        assertNotNull(fac, "ratchet.finalArchitectureCheck is required");
        assertTrue(fac.get("tests").asInt() >= 0);
        assertTrue(fac.get("failures").asInt() >= 0);
        assertTrue(ratchet.get("rules").isArray() && ratchet.get("rules").size() >= 1,
            "ratchet.rules must state the monotonic debt rules");
    }

    @Test
    @DisplayName("entry ids are unique and well-formed; status/scope are valid")
    void entriesAreWellFormed()
    {
        Set<String> seenIds = new HashSet<>();
        List<String> problems = new ArrayList<>();
        for (JsonNode e : root.get("entries"))
        {
            String id = text(e, "id");
            if (id == null || !id.matches("^[A-Z0-9][A-Z0-9_-]*$"))
            {
                problems.add("bad id: " + id);
                continue;
            }
            if (!seenIds.add(id))
            {
                problems.add("duplicate id: " + id);
            }
            String scope = text(e, "scope");
            if (!VALID_SCOPES.contains(scope))
            {
                problems.add(id + ": invalid scope " + scope);
            }
            String status = text(e, "status");
            if (!statusOrder.contains(status))
            {
                problems.add(id + ": unknown status " + status + " (not in statusOrder)");
            }
            if (e.get("productionReachable") == null || !e.get("productionReachable").isBoolean())
            {
                problems.add(id + ": productionReachable must be a boolean");
            }
        }
        assertTrue(problems.isEmpty(), "ledger entry problems:\n  " + String.join("\n  ", problems));
    }

    @Test
    @DisplayName("BMS entries are scoped BMS_ARTIFACT, never a COBOL dialect")
    void bmsIsNotACobolDialect()
    {
        List<String> problems = new ArrayList<>();
        for (JsonNode e : root.get("entries"))
        {
            String id = text(e, "id");
            String scope = text(e, "scope");
            boolean looksLikeBms = id != null && id.startsWith("BMS-");
            if (looksLikeBms && !"BMS_ARTIFACT".equals(scope))
            {
                problems.add(id + " must be scope BMS_ARTIFACT, got " + scope);
            }
            if ("BMS_ARTIFACT".equals(scope) && !looksLikeBms)
            {
                problems.add(id + " has BMS_ARTIFACT scope but its id should start with BMS-");
            }
        }
        assertTrue(problems.isEmpty(), "BMS scope problems:\n  " + String.join("\n  ", problems));
    }

    @Test
    @DisplayName("clean fully-qualified parser/semantic node references resolve to real classes")
    void nodeReferencesResolve()
    {
        List<String> problems = new ArrayList<>();
        for (JsonNode e : root.get("entries"))
        {
            String id = text(e, "id");
            for (String field : new String[] { "parserNode", "semanticNode" })
            {
                String ref = text(e, field);
                if (ref != null && isCleanFqn(ref))
                {
                    try
                    {
                        Class.forName(ref);
                    }
                    catch (ClassNotFoundException ex)
                    {
                        problems.add(id + "." + field + " does not resolve: " + ref);
                    }
                }
            }
        }
        assertTrue(problems.isEmpty(),
            "dangling node references (renamed/removed types):\n  " + String.join("\n  ", problems));
    }

    @Test
    @DisplayName("every live direct backend has exactly one executable ledger item")
    void directBackendInventoryIsFullyCovered() throws Exception
    {
        Path repoRoot = ledgerPath.getParent().getParent();
        Path directRoot = repoRoot.resolve("naca-trans/src/main/java/generate/java");
        assertTrue(Files.isDirectory(directRoot), "direct backend source root must exist");

        Set<String> liveBackends = new HashSet<>();
        Map<String, String> livePaths = new HashMap<>();
        try (Stream<Path> files = Files.walk(directRoot))
        {
            for (Path file : (Iterable<Path>) files
                .filter(path -> path.toString().endsWith(".java"))::iterator)
            {
                String source = Files.readString(file, java.nio.charset.StandardCharsets.ISO_8859_1);
                Matcher backend = DIRECT_BACKEND_CLASS.matcher(source);
                if (!backend.find())
                {
                    continue;
                }
                Matcher javaPackage = PACKAGE.matcher(source);
                assertTrue(javaPackage.find(), "missing package declaration: " + file);
                String fqn = javaPackage.group(1) + "." + backend.group(1);
                assertTrue(liveBackends.add(fqn), "duplicate direct backend FQN: " + fqn);
                livePaths.put(fqn, repoRoot.relativize(file).toString().replace('\\', '/'));
            }
        }

        Map<String, List<JsonNode>> claims = new HashMap<>();
        List<String> problems = new ArrayList<>();
        for (JsonNode entry : root.get("entries"))
        {
            if (!"DIRECT_BACKEND_RETIREMENT".equals(text(entry, "kind")))
            {
                continue;
            }
            String id = text(entry, "id");
            String backend = text(entry, "directBackend");
            String sourcePath = text(entry, "sourcePath");
            if (backend == null || !backend.matches("^generate\\.java\\.[A-Za-z0-9_.]+$"))
            {
                problems.add(id + ": directBackend must be an exact generate.java FQN");
                continue;
            }
            claims.computeIfAbsent(backend, ignored -> new ArrayList<>()).add(entry);
            if (sourcePath == null || sourcePath.isBlank())
            {
                problems.add(id + ": sourcePath is required");
            }
            for (String field : new String[] {
                "priority", "dependencies", "verification", "expectedDebtDelta",
                "attempts", "blocked" })
            {
                if (entry.get(field) == null)
                {
                    problems.add(id + ": scheduler field " + field + " is required");
                }
            }
            JsonNode delta = entry.path("expectedDebtDelta").get("directBackends");
            if (delta == null || delta.asInt() != -1)
            {
                problems.add(id + ": expectedDebtDelta.directBackends must be -1");
            }
            if (TERMINAL_STATUSES.contains(text(entry, "status"))
                && liveBackends.contains(backend))
            {
                problems.add(id + ": claims direct-retired but source still exists: " + sourcePath);
            }
        }

        for (String backend : liveBackends)
        {
            List<JsonNode> owners = claims.getOrDefault(backend, List.of());
            if (owners.size() != 1)
            {
                problems.add(backend + ": expected exactly one ledger owner, got " + owners.size());
                continue;
            }
            String recordedPath = text(owners.get(0), "sourcePath");
            if (!livePaths.get(backend).equals(recordedPath))
            {
                problems.add(backend + ": sourcePath mismatch: " + recordedPath
                    + " != " + livePaths.get(backend));
            }
        }

        int ratchet = root.path("meta").path("ratchet")
            .path("finalArchitectureCheck").path("directBackends").asInt(-1);
        if (ratchet < liveBackends.size() || ratchet - liveBackends.size() > 1)
        {
            problems.add("ratchet directBackends must equal live inventory outside a single "
                + "in-flight retirement: ratchet=" + ratchet + ", live=" + liveBackends.size());
        }
        assertTrue(problems.isEmpty(),
            "direct-backend ledger coverage problems:\n  " + String.join("\n  ", problems));
    }

    private static String text(JsonNode node, String field)
    {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    /** True for a plain Java FQN (no spaces, parens, wildcards or commas). */
    private static boolean isCleanFqn(String s)
    {
        return s.matches("^[a-z][a-zA-Z0-9_]*(\\.[a-zA-Z0-9_]+)+$");
    }
}
