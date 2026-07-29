package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.CJavaEntityFactory;
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
 * Root semantic parity (TEST EXIT ONLY — not wired into TranscoderEngine):
 * parse a real sample independently with both production factories, flatten
 * both semantic trees only through the recursive assembler with the explicit
 * ROOT role, and assert the two produce the same token stream. As direct
 * subclasses are retired, {@code CJavaEntityFactory} deliberately returns pure
 * semantic nodes whose {@code DoExport()} is empty, so the old direct exporter
 * can no longer be a complete-program oracle. Focused migration tests retain
 * the legacy output shapes; this test proves both factories now feed the same
 * assembler contract.
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
     * Parses a sample using either the legacy direct factory or the ST4 semantic
     * factory. Keeping the trees independent makes this a true backend parity
     * test even after transitional CJava*ST controllers are retired.
     */
    private static CEntityClass parse(
        Path cbl, CStringExporter exporter, String programName, boolean st4)
        throws Exception
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
        CJavaEntityFactory factory = st4
            ? new CJavaEntityFactoryST(catalog, exporter)
            : new CJavaEntityFactory(catalog, exporter);
        factory.InitCustomCICSEntities();
        CEntityClass root = program.DoSemanticAnalysis(factory);
        // Mirror TranspilerService: some program headers parse an empty PROGRAM-ID,
        // so production falls back to the supplied program name for the class name.
        if (root != null && (root.GetName() == null || root.GetName().isEmpty()))
        {
            root.SetName(programName);
        }
        return root;
    }

    @Test
    void bothFactoriesProduceEquivalentAssembledProgramRoots() throws Exception
    {
        for (String sample : SAMPLES)
        {
            Path path = locate(sample);
            assertNotNull(path, "sample should exist: " + sample);
            String programName = sample.replaceAll("\\.cbl$", "");

            // Tree A: production compatibility factory, flattened only by ST4.
            CStringExporter directExporter = new CStringExporter();
            CEntityClass directRoot = parse(path, directExporter, programName, false);
            assertNotNull(directRoot, "semantic root for " + sample);
            assertTrue(directRoot instanceof CJavaClass,
                "production root should be a CJavaClass");
            String direct = TemplateLoader.getRecursiveAssembler()
                .renderRoot(directRoot, JavaTemplateRole.ROOT);

            // Tree B: independent ST4 factory parse, flattened by the same
            // assembler. No export side effect touches either semantic tree.
            CStringExporter assemblerExporter = new CStringExporter();
            CEntityClass assemblerRoot = parse(path, assemblerExporter, programName, true);
            assertNotNull(assemblerRoot, "semantic root for " + sample);
            String rendered = TemplateLoader.getRecursiveAssembler()
                .renderRoot(assemblerRoot, JavaTemplateRole.ROOT);

            assertEquals(tokens(direct), tokens(rendered),
                "program-root parity for " + sample
                    + "\n--- direct ---\n" + normalize(direct)
                    + "\n--- rendered ---\n" + normalize(rendered));
        }
    }
}
