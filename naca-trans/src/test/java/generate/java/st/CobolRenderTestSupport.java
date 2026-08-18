package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CStringExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import jlib.misc.AsciiEbcdicConverter;
import lexer.Cobol.CCobolLexer;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import semantic.CEntityClass;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

final class CobolRenderTestSupport
{
    private static final Pattern CLASS_DECLARATION = Pattern.compile(
        "public class ([A-Za-z_$][A-Za-z0-9_$]*)");

    private CobolRenderTestSupport()
    {
    }

    static CEntityClass parse(String source) throws Exception
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

    static String render(String source) throws Exception
    {
        return TemplateLoader.getRecursiveAssembler().renderRoot(
            parse(source), JavaTemplateRole.ROOT);
    }

    static void compile(String rendered) throws Exception
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
        {
            throw new IllegalStateException("JDK compiler should be available");
        }
        Path output = Files.createTempDirectory("cobol-render-compile");
        try
        {
            Matcher className = CLASS_DECLARATION.matcher(rendered);
            assertTrue(className.find(), "generated class declaration should exist");
            Path source = output.resolve(className.group(1) + ".java");
            Files.writeString(source, rendered);
            Path root = findRepositoryRoot();
            String classpath = String.join(
                File.pathSeparator,
                root.resolve("naca-rt/build/classes/java/main").toString(),
                root.resolve("naca-rt/build/resources/main").toString(),
                root.resolve("naca-jlib/build/classes/java/main").toString(),
                root.resolve("naca-jlib/build/resources/main").toString());
            ByteArrayOutputStream diagnostics = new ByteArrayOutputStream();
            int result = compiler.run(null, diagnostics, diagnostics,
                "-classpath", classpath, "-d", output.toString(), source.toString());
            assertEquals(0, result, "generated Java should compile: "
                + diagnostics.toString(StandardCharsets.UTF_8));
        }
        finally
        {
            deleteRecursively(output);
        }
    }

    static int count(String text, String needle)
    {
        int count = 0;
        int offset = 0;
        while (offset >= 0)
        {
            offset = text.indexOf(needle, offset);
            if (offset < 0)
            {
                break;
            }
            count++;
            offset += needle.length();
        }
        return count;
    }

    private static Path findRepositoryRoot()
    {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (current != null)
        {
            if (Files.isDirectory(current.resolve("naca-rt"))
                && Files.isDirectory(current.resolve("naca-jlib")))
            {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Unable to locate repository root from user.dir");
    }

    private static void deleteRecursively(Path root) throws Exception
    {
        List<Path> entries;
        try (Stream<Path> paths = Files.walk(root))
        {
            entries = paths.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path entry : entries)
        {
            Files.delete(entry);
        }
    }
}
