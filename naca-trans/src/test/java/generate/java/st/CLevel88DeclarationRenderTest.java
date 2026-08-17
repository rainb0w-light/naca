package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CStringExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import jlib.misc.AsciiEbcdicConverter;
import lexer.Cobol.CCobolLexer;
import org.junit.jupiter.api.Test;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import semantic.CEntityClass;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

class CLevel88DeclarationRenderTest
{
    private static final String PROGRAM = """
                 IDENTIFICATION DIVISION.
                 PROGRAM-ID. L88TEST.
                 DATA DIVISION.
                 WORKING-STORAGE SECTION.
                 01 APPL-RESULT PIC S9(9).
                    88 APPL-AOK VALUE 0.
                    88 APPL-EOF VALUE 16.
                 PROCEDURE DIVISION.
                     IF APPL-AOK
                         MOVE 1 TO APPL-RESULT
                     END-IF.
                     STOP RUN.
           """;

    private static final String PLAIN_PROGRAM = """
                 IDENTIFICATION DIVISION.
                 PROGRAM-ID. PLAIN.
                 DATA DIVISION.
                 WORKING-STORAGE SECTION.
                 01 APPL-RESULT PIC S9(9).
                 PROCEDURE DIVISION.
                     STOP RUN.
           """;

    private static CEntityClass parse(String source) throws Exception
    {
        COriginalLisiting listing = new COriginalLisiting();
        CCobolLexer lexer = new CCobolLexer();
        assertTrue(lexer.StartLexer(new ByteArrayInputStream(
            source.getBytes(StandardCharsets.UTF_8)), listing), "COBOL source should lex");
        CCobolParser parser = new CCobolParser();
        assertTrue(parser.StartParsing(lexer.GetTokenList()), "COBOL tokens should parse");
        CProgram program = parser.GetRootElement();
        AsciiEbcdicConverter.create();
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        CObjectCatalog catalog = new CObjectCatalog(
            global, listing, CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
        CStringExporter exporter = new CStringExporter();
        catalog.setExporter(exporter);
        CEntityClass root = program.DoSemanticAnalysis(
            new CJavaEntityFactory(catalog, exporter));
        assertNotNull(root, "semantic root should be created");
        return root;
    }

    @Test
    void rendersLevel88ChildrenBeforeProcedureReferencesAndCompiles()
    {
        assertDoesNotThrow(this::assertLevel88Rendering, "level-88 rendering should compile");
    }

    private void assertLevel88Rendering() throws Exception
    {
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(
            parse(PROGRAM), JavaTemplateRole.ROOT);
        String aokDeclaration = "Cond APPL_AOK = declare.condition().value(0).var() ;";
        String eofDeclaration = "Cond APPL_EOF = declare.condition().value(16).var() ;";
        assertEquals(1, count(rendered, aokDeclaration), "APPL-AOK declaration count");
        assertEquals(1, count(rendered, eofDeclaration), "APPL-EOF declaration count");
        assertTrue(PROGRAM.contains("IF APPL-AOK"), "fixture should reference APPL-AOK");
        int ifIndex = rendered.indexOf("if (is(APPL_AOK))");
        assertTrue(ifIndex >= 0, "APPL-AOK procedure reference should render");
        assertTrue(rendered.indexOf(aokDeclaration) < ifIndex, "AOK declaration precedes reference");
        assertTrue(rendered.indexOf(eofDeclaration) < ifIndex, "EOF declaration precedes reference");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "JDK compiler should be available");
        Path output = Files.createTempDirectory("level88-compile");
        try
        {
            Matcher className = Pattern.compile("public class ([A-Za-z_$][A-Za-z0-9_$]*)")
                .matcher(rendered);
            assertTrue(className.find(), "generated class declaration should exist");
            Path source = output.resolve(className.group(1) + ".java");
            Files.writeString(source, rendered);
            Path root = Path.of(System.getProperty("user.dir")).toAbsolutePath();
            while (!Files.isDirectory(root.resolve("naca-rt")) && root.getParent() != null)
            {
                root = root.getParent();
            }
            String classpath = String.join(
                java.io.File.pathSeparator,
                root.resolve("naca-rt/build/classes/java/main").toString(),
                root.resolve("naca-rt/build/resources/main").toString(),
                root.resolve("naca-jlib/build/classes/java/main").toString(),
                root.resolve("naca-jlib/build/resources/main").toString());
            ByteArrayOutputStream diagnostics = new ByteArrayOutputStream();
            int compileResult = compiler.run(null, diagnostics, diagnostics,
                "-classpath", classpath, "-d", output.toString(), source.toString());
            assertEquals(0, compileResult, "generated Java should compile: "
                + diagnostics.toString(StandardCharsets.UTF_8));
        }
        finally
        {
            try (java.util.stream.Stream<Path> paths = Files.walk(output))
            {
                paths.sorted(java.util.Comparator.reverseOrder())
                    .forEach(path -> path.toFile().delete());
            }
        }
    }

    @Test
    void plainPicDeclarationHasNoNamedConditionOutput()
    {
        assertDoesNotThrow(this::assertPlainPicRendering, "plain PIC rendering should be stable");
    }

    private void assertPlainPicRendering() throws Exception
    {
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(
            parse(PLAIN_PROGRAM), JavaTemplateRole.ROOT);
        assertEquals(0, count(rendered, "Cond "), "plain PIC should have no conditions");
        assertEquals(rendered,
            TemplateLoader.getRecursiveAssembler().renderRoot(
                parse(PLAIN_PROGRAM), JavaTemplateRole.ROOT), "plain PIC output should be stable");
    }

    private static int count(String text, String needle)
    {
        return (int) java.util.stream.IntStream.iterate(
                text.indexOf(needle), position -> position >= 0,
                position -> text.indexOf(needle, position + needle.length()))
            .count();
    }
}
