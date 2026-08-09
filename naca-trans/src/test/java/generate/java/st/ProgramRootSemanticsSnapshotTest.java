package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CStringExporter;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jlib.misc.AsciiEbcdicConverter;
import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import org.junit.jupiter.api.Test;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import semantic.CBaseLanguageEntity;
import semantic.CEntityClass;
import semantic.CEntityComment;
import semantic.CEntityDataSection;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureDivision;
import semantic.ProgramCapability;
import semantic.ProgramKind;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Step-3 semantic snapshot: reading the target-neutral program-root model
 * (programKind, capabilities, declarationChildren, executableChildren) is
 * read-only and idempotent. Reading the children does not mutate the object
 * graph, returns a stable order on every call, does not touch the catalog, and
 * triggers no export/render (the exporter stays empty).
 */
class ProgramRootSemanticsSnapshotTest
{
    private static final String[] SAMPLES = { "TESTHELLO.cbl", "T01.cbl" };

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

    private static final class Parsed
    {
        final CEntityClass root;
        final CStringExporter exporter;

        Parsed(CEntityClass root, CStringExporter exporter)
        {
            this.root = root;
            this.exporter = exporter;
        }
    }

    private static Parsed parse(Path cbl) throws Exception
    {
        String source = Files.readString(cbl);
        COriginalLisiting listing = new COriginalLisiting();
        CCobolLexer lexer = new CCobolLexer();
        assertTrue(lexer.StartLexer(
            new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), listing));
        CTokenList tokens = lexer.GetTokenList();
        CCobolParser parser = new CCobolParser();
        assertTrue(parser.StartParsing(tokens));
        CProgram program = parser.GetRootElement();
        AsciiEbcdicConverter.create();
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        CObjectCatalog catalog = new CObjectCatalog(global, listing,
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
        CStringExporter exporter = new CStringExporter();
        catalog.setExporter(exporter);
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, exporter);
        factory.InitCustomCICSEntities();
        CEntityClass root = program.DoSemanticAnalysis(factory);
        assertNotNull(root, "semantic root for " + cbl);
        return new Parsed(root, exporter);
    }

    private static List<String> names(List<CBaseLanguageEntity> entities)
    {
        return entities.stream().map(CBaseLanguageEntity::GetName).collect(Collectors.toList());
    }

    @Test
    void programRootModelIsReadOnlyStableAndTriggersNoExport() throws Exception
    {
        for (String sample : SAMPLES)
        {
            Path path = locate(sample);
            assertNotNull(path, "sample should exist: " + sample);
            Parsed parsed = parse(path);
            CEntityClass root = parsed.root;

            // Semantic analysis builds the tree but does not export; the exporter
            // must still be empty before we read anything.
            assertEquals("", parsed.exporter.getCapturedString(),
                "no export before reading the model (" + sample + ")");

            // Object-graph snapshot before reading the model.
            String nameBefore = root.GetName();
            List<String> activeBefore = names(root.getActiveChildren());

            // Read the model twice.
            ProgramKind kind1 = root.getProgramKind();
            List<ProgramCapability> caps1 = new ArrayList<>(root.getCapabilities());
            List<String> decl1 = names(root.getDeclarationChildren());
            List<String> exec1 = names(root.getExecutableChildren());

            ProgramKind kind2 = root.getProgramKind();
            List<ProgramCapability> caps2 = new ArrayList<>(root.getCapabilities());
            List<String> decl2 = names(root.getDeclarationChildren());
            List<String> exec2 = names(root.getExecutableChildren());

            // Idempotent: repeated reads give identical results (stable order).
            assertEquals(kind1, kind2, "programKind stable (" + sample + ")");
            assertEquals(caps1, caps2, "capabilities stable (" + sample + ")");
            assertEquals(decl1, decl2, "declarationChildren stable (" + sample + ")");
            assertEquals(exec1, exec2, "executableChildren stable (" + sample + ")");

            // Classification is by semantic type.
            for (CBaseLanguageEntity d : root.getDeclarationChildren())
            {
                assertTrue(d instanceof CEntityDataSection,
                    "declaration child must be a data section: " + d.getClass());
            }
            for (CBaseLanguageEntity e : root.getExecutableChildren())
            {
                assertTrue(e instanceof CEntityProcedureDivision || e instanceof CEntityProcedure,
                    "executable child must be a procedure entity: " + e.getClass());
            }

            // Root-child taxonomy for these samples: every active child is a data
            // section (declaration), a procedure entity (executable), or a comment.
            // Comments are a separate category, bound later (step 4/5); the two
            // collections exposed here cover every non-comment active child.
            int comments = 0;
            for (CBaseLanguageEntity child : root.getActiveChildren())
            {
                assertTrue(child instanceof CEntityDataSection
                        || child instanceof CEntityProcedureDivision
                        || child instanceof CEntityProcedure
                        || child instanceof CEntityComment,
                    "unexpected root child type: " + child.getClass());
                if (child instanceof CEntityComment)
                {
                    comments++;
                }
            }
            assertEquals(activeBefore.size() - comments,
                root.getDeclarationChildren().size() + root.getExecutableChildren().size(),
                "declaration + executable cover all non-comment active children (" + sample + ")");
            assertTrue(!root.getDeclarationChildren().isEmpty(),
                "expected a data section (" + sample + ")");
            assertTrue(!root.getExecutableChildren().isEmpty(),
                "expected a procedure division (" + sample + ")");

            // Object graph unchanged after reading.
            assertEquals(nameBefore, root.GetName(), "class name unchanged (" + sample + ")");
            assertEquals(activeBefore, names(root.getActiveChildren()),
                "active children unchanged (" + sample + ")");

            // Reading the model triggered no export/render.
            assertEquals("", parsed.exporter.getCapturedString(),
                "no export after reading the model (" + sample + ")");
        }
    }
}
