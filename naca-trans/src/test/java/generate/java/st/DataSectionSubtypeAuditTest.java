package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.CStringExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import generate.templates.recursive.MissingTemplateRendererException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import jlib.misc.AsciiEbcdicConverter;
import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import semantic.CBaseLanguageEntity;
import semantic.CEntityClass;
import semantic.CEntityDataSection;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Step-4 audit (see docs/ST4_DATA_SECTION_MIGRATION_PLAN.md): parse the real
 * standalone samples, walk their DATA DIVISION, and inventory every concrete
 * semantic entity type that appears inside a data section. For each type we
 * probe the role-aware assembler in DECLARATION role: a type with no
 * declaration binding fails closed with {@link MissingTemplateRendererException}
 * (no silent fallback). The printed report lists exactly which bindings must
 * exist before the data section can be wired into the production root writer.
 *
 * <p>This is an informational audit, tagged {@code data-section-audit}; it is
 * excluded from the default gate and asserts only that parsing succeeds and the
 * inventory is non-empty.
 */
@Tag("data-section-audit")
class DataSectionSubtypeAuditTest
{
    private static final String[] SAMPLES = {
        "T01.cbl", "TESTHELLO.cbl", "VERBS.cbl", "INSPECT1.cbl", "TEST-A-STANDALONE.cbl"
    };

    private static Path locate(String name)
    {
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol", name),
            Path.of("../NacaSamples/cobol", name) })
        {
            if (Files.exists(p))
            {
                return p;
            }
        }
        return null;
    }

    private static CEntityClass parse(Path cbl) throws Exception
    {
        String source = Files.readString(cbl);
        COriginalLisiting listing = new COriginalLisiting();
        CCobolLexer lexer = new CCobolLexer();
        assertTrue(lexer.StartLexer(
            new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), listing),
            "lexer should start for " + cbl);
        CTokenList tokens = lexer.GetTokenList();
        CCobolParser parser = new CCobolParser();
        assertTrue(parser.StartParsing(tokens), "parser should start for " + cbl);
        CProgram program = parser.GetRootElement();
        AsciiEbcdicConverter.create();
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        CObjectCatalog catalog = new CObjectCatalog(global, listing,
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
        CStringExporter exporter = new CStringExporter();
        catalog.setExporter(exporter);
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
        factory.InitCustomCICSEntities();
        return program.DoSemanticAnalysis(factory);
    }

    /** Recursively collects every entity inside the data sections. */
    private static void collectDataEntities(CBaseLanguageEntity node, Set<CBaseLanguageEntity> out)
    {
        if (node instanceof CEntityDataSection || insideDataSection(node))
        {
            out.add(node);
        }
        List<CBaseLanguageEntity> children = node.getChildren();
        if (children != null)
        {
            for (CBaseLanguageEntity child : children)
            {
                collectDataEntities(child, out);
            }
        }
    }

    private static boolean insideDataSection(CBaseLanguageEntity node)
    {
        for (CBaseLanguageEntity p = node.GetParent(); p != null; p = p.GetParent())
        {
            if (p instanceof CEntityDataSection)
            {
                return true;
            }
        }
        return false;
    }

    @Test
    void inventoriesDataSectionEntityTypesAndTheirDeclarationBindings() throws Exception
    {
        // type name -> "COVERED" or "MISSING"
        TreeMap<String, String> coverage = new TreeMap<>();
        Set<String> parsedSamples = new LinkedHashSet<>();

        for (String sample : SAMPLES)
        {
            Path path = locate(sample);
            assertNotNull(path, "sample should exist: " + sample);
            CEntityClass root = parse(path);
            assertNotNull(root, "semantic root for " + sample);
            parsedSamples.add(sample);

            Set<CBaseLanguageEntity> entities = new LinkedHashSet<>();
            collectDataEntities(root, entities);
            for (CBaseLanguageEntity entity : entities)
            {
                String typeName = entity.getClass().getName();
                if (coverage.containsKey(typeName))
                {
                    continue;
                }
                coverage.put(typeName, probe(entity));
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("\n=== DATA SECTION SUBTYPE AUDIT (").append(parsedSamples).append(") ===\n");
        for (var entry : coverage.entrySet())
        {
            report.append(String.format("  [%s] %s%n", entry.getValue(), entry.getKey()));
        }
        long missing = coverage.values().stream().filter("MISSING"::equals).count();
        report.append("  total types: ").append(coverage.size())
            .append(", missing declaration binding: ").append(missing).append('\n');
        System.out.println(report);

        assertTrue(!coverage.isEmpty(), "audit should find data-section entities");
    }

    private static String probe(CBaseLanguageEntity entity)
    {
        try
        {
            TemplateLoader.getRecursiveAssembler().renderNode(entity, JavaTemplateRole.DECLARATION);
            return "COVERED";
        }
        catch (MissingTemplateRendererException e)
        {
            return "MISSING";
        }
    }
}
