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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import jlib.misc.AsciiEbcdicConverter;
import lexer.Cobol.CCobolLexer;
import org.junit.jupiter.api.Test;
import parser.Cobol.CCobolParser;
import parser.Cobol.elements.CProgram;
import semantic.CEntityClass;
import semantic.CBaseLanguageEntity;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

class CWorkingGroupDeclarationRenderTest
{
    private static final String PROGRAM = """
                 IDENTIFICATION DIVISION.
                 PROGRAM-ID. GROUPTEST.
                 DATA DIVISION.
                 WORKING-STORAGE SECTION.
                 01 CARDFILE-STATUS.
                    05 CARDFILE-STAT1 PIC X.
                    05 CARDFILE-STAT2 PIC X.
                 01 UNUSED-GROUP.
                    05 UNUSED-CHILD PIC X.
                 PROCEDURE DIVISION.
                     MOVE "00" TO CARDFILE-STATUS.
                     STOP RUN.
           """;

    private static CEntityClass parse(String source) throws Exception
    {
        COriginalLisiting listing = new COriginalLisiting();
        CCobolLexer lexer = new CCobolLexer();
        assertTrue(lexer.StartLexer(new ByteArrayInputStream(
            source.getBytes(StandardCharsets.UTF_8)), listing));
        CCobolParser parser = new CCobolParser();
        assertTrue(parser.StartParsing(lexer.GetTokenList()));
        CProgram program = parser.GetRootElement();
        AsciiEbcdicConverter.create();
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        CObjectCatalog catalog = new CObjectCatalog(
            global, listing, CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
        CStringExporter exporter = new CStringExporter();
        catalog.setExporter(exporter);
        CEntityClass root = program.DoSemanticAnalysis(
            new CJavaEntityFactory(catalog, exporter));
        assertNotNull(root);
        return root;
    }

    @Test
    void referencedGroupKeepsCompleteStorageLayoutAndCompiles() throws Exception
    {
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(
            parse(PROGRAM), JavaTemplateRole.ROOT);
        String group = "Var CARDFILE_STATUS = declare.level(1).var() ;";
        String first = "Var CARDFILE_STAT1 = declare.level(05).picX(1).var() ;";
        String second = "Var CARDFILE_STAT2 = declare.level(05).picX(1).var() ;";
        assertEquals(1, count(rendered, group), rendered);
        assertEquals(1, count(rendered, first), rendered);
        assertEquals(1, count(rendered, second), rendered);
        assertTrue(rendered.indexOf(group) < rendered.indexOf(first));
        assertTrue(rendered.indexOf(first) < rendered.indexOf(second));
        compile(rendered);
    }

    @Test
    void ignoredIndependentGroupIsExcludedByParentSelection() throws Exception
    {
        CEntityClass root = parse(PROGRAM);
        CBaseLanguageEntity unused = find(root, "UNUSED-GROUP");
        assertNotNull(unused);
        unused.SetIgnoreStructure();
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(
            root, JavaTemplateRole.ROOT);
        assertEquals(0, count(rendered, "UNUSED_GROUP"), rendered);
        assertEquals(0, count(rendered, "UNUSED_CHILD"), rendered);
    }

    private static CBaseLanguageEntity find(CBaseLanguageEntity entity, String name)
    {
        if (name.equals(entity.GetName()))
        {
            return entity;
        }
        for (CBaseLanguageEntity child : entity.getChildren())
        {
            CBaseLanguageEntity found = find(child, name);
            if (found != null)
            {
                return found;
            }
        }
        return null;
    }

    private static void compile(String rendered) throws Exception
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler);
        Path output = Files.createTempDirectory("working-group-compile");
        try
        {
            Matcher className = Pattern.compile(
                "public class ([A-Za-z_$][A-Za-z0-9_$]*)").matcher(rendered);
            assertTrue(className.find(), rendered);
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
            int result = compiler.run(null, diagnostics, diagnostics,
                "-classpath", classpath, "-d", output.toString(), source.toString());
            assertEquals(0, result, diagnostics.toString(StandardCharsets.UTF_8));
        }
        finally
        {
            try (Stream<Path> paths = Files.walk(output))
            {
                paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> path.toFile().delete());
            }
        }
    }

    private static int count(String text, String needle)
    {
        int count = 0;
        int offset = 0;
        while ((offset = text.indexOf(needle, offset)) >= 0)
        {
            count++;
            offset += needle.length();
        }
        return count;
    }
}
