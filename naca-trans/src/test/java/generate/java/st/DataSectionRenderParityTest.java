package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.CStringExporter;
import generate.java.CJavaDataSection;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
 * Step-5 de-risking: prove a whole, real DATA DIVISION section renders through
 * the recursive ST4 assembler byte-for-byte (modulo whitespace) like the direct
 * {@code CJavaDataSection.DoExport} path. This is stronger than the binding
 * audit ({@link DataSectionSubtypeAuditTest}), which only checks a binding
 * exists: here the full section subtree is flattened and compared.
 *
 * <p>Tagged {@code data-section-audit}; runs via {@code :naca-trans:dataSectionAudit}.
 */
@Tag("data-section-audit")
class DataSectionRenderParityTest
{
    private static final String[] SAMPLES = { "TESTHELLO.cbl", "T01.cbl" };

    private static String normalize(String s)
    {
        return s.replaceAll("\\s+", " ").strip();
    }

    /**
     * Whitespace-insensitive token comparison. The recursive ST4 assembler emits
     * compact output, whereas the legacy direct generator spaces tokens via its
     * WriteWord output protocol; the two are token-for-token identical but differ
     * in whitespace only (which javac ignores). Per-declaration exact formatting
     * is already locked by DataAttributeDeclarationTemplateTest /
     * DataSectionDeclarationTemplateTest, so here we verify the whole section
     * composes the same token stream.
     */
    private static String tokens(String s)
    {
        return s.replaceAll("\\s+", "");
    }

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
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
        factory.InitCustomCICSEntities();
        return program.DoSemanticAnalysis(factory);
    }

    private static void collectSections(CBaseLanguageEntity node, List<CEntityDataSection> out)
    {
        if (node instanceof CEntityDataSection section)
        {
            out.add(section);
        }
        List<CBaseLanguageEntity> children = node.getChildren();
        if (children != null)
        {
            for (CBaseLanguageEntity child : children)
            {
                collectSections(child, out);
            }
        }
    }

    private static boolean isKnownSectionKind(CEntityDataSection section)
    {
        return section.isWorkingStorageSection()
            || section.isLinkageSection()
            || section.isFileSection()
            || section.isVariableSection();
    }

    @Test
    void realDataSectionsRenderLikeTheDirectGenerator() throws Exception
    {
        int checked = 0;
        for (String sample : SAMPLES)
        {
            Path path = locate(sample);
            assertNotNull(path, "sample should exist: " + sample);
            CEntityClass root = parse(path);
            assertNotNull(root, "semantic root for " + sample);

            List<CEntityDataSection> sections = new ArrayList<>();
            collectSections(root, sections);
            for (CEntityDataSection section : sections)
            {
                if (!isKnownSectionKind(section))
                {
                    continue; // e.g. SQL cursor sections are out of scope
                }
                assertTrue(section instanceof CJavaDataSection,
                    "production data section should be a CJavaDataSection");

                // Point the whole subtree at a fresh mock so the direct export is
                // captured in isolation. Run the direct export first: it assigns
                // FILLER default names exactly as production does, so the
                // assembler then observes the same names.
                MockJavaExporter mock = new MockJavaExporter();
                section.setLanguageExporter(mock);
                section.StartExport();
                String direct = mock.getCapturedOutput();

                String rendered = TemplateLoader.getRecursiveAssembler()
                    .renderRoot(section, JavaTemplateRole.DECLARATION);

                assertEquals(tokens(direct), tokens(rendered),
                    "data-section parity for " + sample + " / " + section.GetName()
                        + "\n--- direct ---\n" + normalize(direct)
                        + "\n--- rendered ---\n" + normalize(rendered));
                checked++;
            }
        }
        assertTrue(checked > 0, "expected at least one known data section across samples");
    }
}
