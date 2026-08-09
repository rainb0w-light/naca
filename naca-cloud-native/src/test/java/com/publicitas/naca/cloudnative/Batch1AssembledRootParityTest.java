package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import generate.CJavaEntityFactory;
import generate.CStringExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import jlib.misc.AsciiEbcdicConverter;
import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import semantic.CEntityClass;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;
import utils.Transcoder;

/**
 * Step 5c (TEST EXIT ONLY — production TranscoderEngine is NOT wired): BATCH1's
 * whole program root is rendered through the recursive assembler with the
 * explicit ROOT role — working storage, the file section with its FD, the COPY
 * MSGZONE instance declaration, and the procedure division — and compared
 * token-for-token against the frozen production golden. This proves the new
 * root/data/COPY/FD templates compose a full real program identically to the
 * direct generator, the precondition for later swapping the production exit.
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class Batch1AssembledRootParityTest {

    private static String batch1Source;
    private static Path copybookDir;

    @BeforeAll
    static void setup() throws IOException {
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol/BATCH1.cbl"),
            Path.of("../NacaSamples/cobol/BATCH1.cbl") }) {
            if (Files.exists(p)) {
                batch1Source = Files.readString(p);
                break;
            }
        }
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol/include"),
            Path.of("../NacaSamples/cobol/include") }) {
            if (Files.isDirectory(p)) {
                copybookDir = p.toAbsolutePath();
                break;
            }
        }
    }

    private static String golden() throws IOException {
        try (InputStream in =
            Batch1AssembledRootParityTest.class.getResourceAsStream("/golden/BATCH1_baseline.java")) {
            assertNotNull(in, "golden/BATCH1_baseline.java should be on the test classpath");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String tokens(String s) {
        return s.replaceAll("\\s+", "");
    }

    private static String normalize(String s) {
        return s.replaceAll("\\s+", " ").strip();
    }

    /** Parses BATCH1 with the COPY include group and renders the root via the assembler. */
    private static String assembleBatch1Root() throws Exception {
        String outDir = System.getProperty("java.io.tmpdir") + "/naca-batch1-root-" + System.nanoTime();
        IncludeGroupSupport.configure(copybookDir.toString(), outDir);

        COriginalLisiting listing = new COriginalLisiting();
        for (String line : batch1Source.split("\n")) {
            listing.RegisterNewOriginalLine(line);
        }
        CCobolLexer lexer = new CCobolLexer();
        assertTrue(lexer.StartLexer(
            new ByteArrayInputStream(batch1Source.getBytes(StandardCharsets.UTF_8)), listing));
        CTokenList tokens = lexer.GetTokenList();
        CCobolParser parser = new CCobolParser();
        assertTrue(parser.StartParsing(tokens));
        CProgram program = parser.GetRootElement();

        AsciiEbcdicConverter.create();
        Transcoder includeTranscoder = IncludeGroupSupport.getIncludeTranscoder();
        CGlobalCatalog global = new CGlobalCatalog(includeTranscoder, "", "",
            IncludeGroupSupport.INCLUDE_GROUP_NAME);
        CObjectCatalog catalog = new CObjectCatalog(global, listing,
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
        CStringExporter exporter = new CStringExporter();
        catalog.setExporter(exporter);
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, exporter);
        factory.InitCustomCICSEntities();
        CEntityClass root = program.DoSemanticAnalysis(factory);
        assertNotNull(root, "BATCH1 semantic root");
        if (root.GetName() == null || root.GetName().isEmpty()) {
            root.SetName("BATCH1");
        }
        return TemplateLoader.getRecursiveAssembler().renderRoot(root, JavaTemplateRole.ROOT);
    }

    @Test
    @DisplayName("BATCH1 full root rendered by the assembler matches the frozen golden")
    void assembledBatch1RootMatchesGolden() throws Exception {
        assertNotNull(batch1Source, "BATCH1.cbl should exist");
        assertNotNull(copybookDir, "copybook include dir should exist");

        String golden = golden();
        String rendered = assembleBatch1Root();

        assertEquals(tokens(golden), tokens(rendered),
            "BATCH1 assembled root must be token-identical to the frozen golden"
                + "\n--- golden ---\n" + normalize(golden)
                + "\n--- rendered ---\n" + normalize(rendered));
    }
}
