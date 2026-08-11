package parser.FPac;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import jlib.engine.NotificationEngine;
import lexer.CTokenList;
import lexer.FPac.CFPacLexer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import parser.FPac.elements.CFPacScript;
import parser.FPac.elements.CFPacMove;
import semantic.CEntityClass;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;
import utils.NacaTransAssertException;
import utils.FPacTranscoder.DefaultFileManager;

/** End-to-end contract for the shipped FPac source fixture and fail-closed factory boundary. */
class FPacPipelineAcceptanceTest
{
    private static final String FIXTURE = "fpac/SMOKE.fpac";

    @TempDir
    Path temporaryDirectory;

    private static CJavaFPacEntityFactory factory(COriginalLisiting listing)
    {
        CGlobalCatalog globalCatalog = new CGlobalCatalog(null, "", "", "");
        CObjectCatalog catalog = new CObjectCatalog(globalCatalog, listing,
            CTransApplicationGroup.EProgramType.TYPE_BATCH, new NotificationEngine());
        catalog.RegisterNotifHandler(new DefaultFileManager());
        return new CJavaFPacEntityFactory(catalog, null);
    }

    private static String loadFixture() throws Exception
    {
        try (var input = FPacPipelineAcceptanceTest.class.getClassLoader()
            .getResourceAsStream(FIXTURE))
        {
            assertNotNull(input, "shipped FPac fixture must exist");
            return new String(input.readAllBytes(), StandardCharsets.ISO_8859_1);
        }
    }

    private static CEntityClass analyze(String source) throws Exception
    {
        COriginalLisiting listing = new COriginalLisiting();
        CFPacLexer lexer = new CFPacLexer();
        assertTrue(lexer.StartLexer(new ByteArrayInputStream(
            source.getBytes(StandardCharsets.ISO_8859_1)), listing));
        CTokenList tokens = lexer.GetTokenList();
        assertTrue(tokens.GetNbTokens() > 0);

        CFPacParser parser = new CFPacParser();
        assertTrue(parser.StartParsing(tokens), "canonical FPac fixture must parse");
        CFPacScript script = parser.GetRootElement();
        script.setName("FPACSMOKE");
        return script.DoSemanticAnalysis(factory(listing));
    }

    @Test
    void canonicalProgramParsesRendersAndCompiles() throws Exception
    {
        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(analyze(loadFixture()), JavaTemplateRole.FPAC_ROOT);

        assertTrue(rendered.contains("public class FPACSMOKE extends FPacProgram"));
        assertTrue(rendered.contains("wto.display(\"HELLO FPAC\") ;"));
        assertTrue(rendered.contains("protected int first()"));
        assertTrue(rendered.contains("protected int normal()"));
        assertTrue(rendered.contains("protected int last()"));

        Path source = temporaryDirectory.resolve("FPACSMOKE.java");
        Files.writeString(source, rendered, StandardCharsets.ISO_8859_1);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "acceptance requires a JDK compiler");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, null,
            StandardCharsets.ISO_8859_1))
        {
            var units = files.getJavaFileObjects(source.toFile());
            boolean compiled = compiler.getTask(null, files, diagnostics,
                List.of("-proc:none", "-classpath", System.getProperty("java.class.path"),
                    "-d", temporaryDirectory.toString()), null, units).call();
            assertTrue(compiled, () -> "generated FPac Java must compile:\n"
                + diagnostics.getDiagnostics());
        }
    }

    @Test
    void declarationsWithoutRuntimeSemanticsFailClosed() throws Exception
    {
        NacaTransAssertException error = assertThrows(NacaTransAssertException.class,
            () -> analyze("PARM = NOLOG\nEND\n"));
        assertTrue(error.csMessage.contains("PARM declarations"));
    }

    @Test
    void factoryReportsGrammarExcludedOperationsByName()
    {
        NacaTransAssertException error = assertThrows(NacaTransAssertException.class,
            () -> factory(null).NewEntityCICSGetMain(1));
        assertTrue(error.csMessage.contains("FPac grammar"));
        assertTrue(error.csMessage.contains("NewEntityCICSGetMain"));
        assertFalse(error.csMessage.contains("not implemented"));
    }

    @Test
    void parsedButUnsupportedCommandsFailBeforeRendering()
    {
        CFPacMove move = new CFPacMove(12, new Vector<>());
        move.rejectUnsupportedCommand("CB");

        NacaTransAssertException error = assertThrows(NacaTransAssertException.class,
            () -> move.DoSemanticAnalysis(null, factory(null)));
        assertTrue(error.csMessage.contains("CB command"));
        assertTrue(error.csMessage.contains("runtime semantics"));
    }
}
