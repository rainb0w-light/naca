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
    void programRootRendersLikeTheDirectGenerator() throws Exception
    {
        for (String sample : SAMPLES)
        {
            Path path = locate(sample);
            assertNotNull(path, "sample should exist: " + sample);
            String programName = sample.replaceAll("\\.cbl$", "");

            // Tree A: flattened ONLY through the direct generator.
            CStringExporter directExporter = new CStringExporter();
            CEntityClass directRoot = parse(path, directExporter, programName, false);
            assertNotNull(directRoot, "semantic root for " + sample);
            assertTrue(directRoot instanceof CJavaClass,
                "production root should be a CJavaClass");
            directRoot.StartExport();
            String direct = directExporter.getCapturedString();

            // Tree B: an INDEPENDENT parse, flattened ONLY through the assembler
            // with the ROOT role. No direct export ever touches this tree, so the
            // comparison proves the root template does not rely on any direct
            // export side effect (e.g. the old export-time FILLER naming) having
            // "washed" the model first. FILLER names are assigned at construction,
            // so both independent trees converge on the same names.
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
