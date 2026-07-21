package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.CStringExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineSession;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import jlib.classLoader.CodeManager;
import jlib.log.Log;
import jlib.log.LogCenterConsole;
import jlib.log.LogCenterLoader;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;
import jlib.log.LogParams;
import jlib.log.PatternLayoutConsole;
import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.BasePic9Comp3BufferSupport;
import lexer.CTokenList;
import lexer.Cobol.CCobolLexer;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.batchPrgEnv.BatchProgramLoader;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.tempCache.TempCacheLocator;
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

/**
 * Step 4.5 execution gate (TEST EXIT ONLY — production TranscoderEngine is NOT
 * wired). For each sample the program root is rendered through the recursive
 * assembler with the explicit ROOT role, then the resulting Java is compiled
 * with javac (hard assert) and actually run on the NacaRT runtime, asserting the
 * displayed output. Because {@code ProgramRootRenderParityTest} already proves
 * the assembled root is token-identical to the direct generator from two
 * independent trees, this proves the assembled artifact is not just equal but
 * genuinely compilable and runnable.
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class AssembledRootExecutionTest
{
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

    private static CEntityClass parse(Path cbl, CStringExporter exporter, String programName)
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
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
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

    /** Renders the program root of a sample through the assembler (ROOT role). */
    private static String assembleRoot(String sample) throws Exception
    {
        Path path = locate(sample);
        assertNotNull(path, "sample should exist: " + sample);
        String programName = sample.replaceAll("\\.cbl$", "");
        CEntityClass root = parse(path, new CStringExporter(), programName);
        assertNotNull(root, "semantic root for " + sample);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(root, JavaTemplateRole.ROOT);
    }

    private static String nacaClasspath()
    {
        Path root = Path.of(System.getProperty("user.dir")).getParent();
        return root + "/naca-rt/build/classes/java/main:"
            + root + "/naca-jlib/build/classes/java/main";
    }

    /** javac-compiles one assembled source into {@code classesDir}; returns javac output. */
    private static int javac(String className, String source, Path classesDir) throws Exception
    {
        Path srcDir = classesDir.resolve("src");
        Files.createDirectories(srcDir);
        Path javaFile = srcDir.resolve(className + ".java");
        Files.writeString(javaFile, source);
        ProcessBuilder pb = new ProcessBuilder(
            "javac", "-d", classesDir.toString(), "-classpath", nacaClasspath(),
            "-proc:none", javaFile.toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes());
        int exit = p.waitFor();
        if (exit != 0)
        {
            System.out.println("javac failed for " + className + ":\n" + output);
        }
        return exit;
    }

    /** Runs an assembled+compiled batch program on NacaRT and captures its display output. */
    private static String runBatch(String className, Path classesDir)
    {
        StringBuilder captured = new StringBuilder();
        LogCenterConsole center = new LogCenterConsole(new LogCenterLoader()
        {
            {
                logLevel = LogLevel.Normal;
                logFlow = LogFlowStd.Any;
                csChannel = "NacaRT";
            }
        })
        {
            @Override
            protected void sendOutput(LogParams logParam)
            {
                captured.append(logParam.toString()).append("\n");
            }
        };
        center.setPatternLayout(new PatternLayoutConsole("%Message"));
        Log.registerLogCenter(center);

        CodeManager.setPath(classesDir.toString());
        CodeManager.initLoadPossibilities(true, false);
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();

        BaseProgramLoader loader = new BatchProgramLoader(null, null);
        OnlineSession session = new OnlineSession(false);
        OnlineEnvironment env = (OnlineEnvironment) loader.GetEnvironment(session, null, null);
        env.setNextProgramToLoad(className);
        loader.runTopProgram(env, new ArrayList<BaseCalledPrgPublicArgPositioned>());
        return captured.toString();
    }

    @Test
    @DisplayName("Assembled TESTHELLO root compiles with javac and runs")
    void assembledTesthelloCompilesAndRuns() throws Exception
    {
        String source = assembleRoot("TESTHELLO.cbl");
        assertTrue(source.contains("public class Testhello extends BatchProgram"), source);

        Path classesDir = Path.of(System.getProperty("java.io.tmpdir"),
            "naca-assembled-testhello-" + System.nanoTime());
        Files.createDirectories(classesDir);
        assertEquals(0, javac("Testhello", source, classesDir),
            "assembled TESTHELLO must compile with javac");

        String output = runBatch("Testhello", classesDir);
        assertTrue(output.contains("Hello from COBOL!"),
            "assembled TESTHELLO should display its greeting; got:\n" + output);
        assertTrue(output.contains("Status: SUCCESS"),
            "assembled TESTHELLO should display its status; got:\n" + output);
    }

    @Test
    @DisplayName("Assembled T01 root compiles with javac and runs")
    void assembledT01CompilesAndRuns() throws Exception
    {
        String source = assembleRoot("T01.cbl");
        assertTrue(source.contains("public class T01 extends BatchProgram"), source);

        Path classesDir = Path.of(System.getProperty("java.io.tmpdir"),
            "naca-assembled-t01-" + System.nanoTime());
        Files.createDirectories(classesDir);
        assertEquals(0, javac("T01", source, classesDir),
            "assembled T01 must compile with javac");

        String output = runBatch("T01", classesDir);
        assertNotNull(output);
        assertTrue(output.contains("Test Complete") || output.contains("STOP"),
            "assembled T01 should run to completion; got:\n" + output);
    }
}
