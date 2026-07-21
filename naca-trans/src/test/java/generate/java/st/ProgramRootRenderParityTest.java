package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.CStringExporter;
import generate.java.CJavaClass;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import jlib.misc.AsciiEbcdicConverter;
import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import semantic.CEntityClass;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Step-4 root render parity (TEST EXIT ONLY — not wired into TranscoderEngine):
 * parse a real sample into a semantic tree, flatten it once through the direct
 * {@code CJavaClass.DoExport} path and once through the recursive assembler with
 * the explicit ROOT role, and assert the two produce the same token stream
 * (whitespace-insensitive; javac ignores whitespace). The direct export runs
 * first so it assigns FILLER default names exactly as production does, and the
 * assembler then observes the same names.
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class ProgramRootRenderParityTest
{
    private static final String[] SAMPLES = { "TESTHELLO.cbl", "T01.cbl" };

    private static String normalize(String s)
    {
        return s.replaceAll("\\s+", " ").strip();
    }

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

    /**
     * Parses a sample using the ST4 factory. Every entity shares {@code exporter}
     * (set on the catalog and factory), so a single {@code root.StartExport()}
     * captures the whole direct compilation unit into {@code exporter}.
     */
    private static CEntityClass parse(Path cbl, CStringExporter exporter) throws Exception
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
        catalog.setExporter(exporter);
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
        factory.InitCustomCICSEntities();
        return program.DoSemanticAnalysis(factory);
    }

    @Test
    void programRootRendersLikeTheDirectGenerator() throws Exception
    {
        for (String sample : SAMPLES)
        {
            Path path = locate(sample);
            assertNotNull(path, "sample should exist: " + sample);

            CStringExporter exporter = new CStringExporter();
            CEntityClass root = parse(path, exporter);
            assertNotNull(root, "semantic root for " + sample);
            assertTrue(root instanceof CJavaClass,
                "production root should be a CJavaClass");

            // Direct first: assigns FILLER names as production does.
            root.StartExport();
            String direct = exporter.getCapturedString();

            String rendered = TemplateLoader.getRecursiveAssembler()
                .renderRoot(root, JavaTemplateRole.ROOT);

            assertEquals(tokens(direct), tokens(rendered),
                "program-root parity for " + sample
                    + "\n--- direct ---\n" + normalize(direct)
                    + "\n--- rendered ---\n" + normalize(rendered));
        }
    }
}
