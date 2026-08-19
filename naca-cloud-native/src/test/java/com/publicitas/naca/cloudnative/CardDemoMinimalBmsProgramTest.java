package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.OnlineCorpusSupport;
import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import com.publicitas.naca.cloudnative.carddemo.generation.GeneratedBmsSymbolicAliasAdapter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import semantic.CEntityClass;
import semantic.forms.CEntityResourceFormContainer;
import utils.Transcoder;

/** Build-time proof that the unmodified translator can generate a minimal online program. */
@Tag("carddemo-minimal-bms")
class CardDemoMinimalBmsProgramTest
{
    private static final Pattern PUBLIC_CLASS = Pattern.compile("public class ([A-Za-z0-9_]+)");
    private static final String SOURCE_DIRECTORY = "src";
    private static final String CLASSES_DIRECTORY = "classes";

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void translatesCompilesAndRunsBmsJsonProofProgramInTheCloudNativeTarget() throws Exception
    {
        Path module = Path.of(System.getProperty("user.dir"));
        Path fixture = module.resolve("src/test/resources/carddemo/minimal");
        Path includes = module.getParent()
            .resolve("naca-rt-tests/src/test/resources/naca-samples/source/copybooks");
        Path target = module.resolve("build/generated-carddemo/minimal");
        Path sourceDirectory = target.resolve(SOURCE_DIRECTORY);
        Path classesDirectory = target.resolve(CLASSES_DIRECTORY);
        Files.createDirectories(sourceDirectory);
        Files.createDirectories(classesDirectory);

        Transcoder transcoder = OnlineCorpusSupport.build(
            fixture.toString(), includes.toString(), fixture.toString(), target.toString());
        CEntityResourceFormContainer mapset =
            OnlineCorpusSupport.analyzeMapset(transcoder, "BMSJSM1");
        assertNotNull(mapset,
            "The proof BMS mapset must lower from its real source");
        CEntityResourceFormContainer symbolicMapset =
            OnlineCorpusSupport.analyzeMapset(transcoder, "BMSJSM1S");
        assertNotNull(symbolicMapset,
            "The proof symbolic BMS mapset must lower from its real source");
        Files.writeString(sourceDirectory.resolve("BMSJSM1.java"), TemplateLoader.getRecursiveAssembler()
            .renderRoot(mapset, JavaTemplateRole.ROOT));
        String symbolicSource = TemplateLoader.getRecursiveAssembler()
            .renderRoot(symbolicMapset, JavaTemplateRole.ROOT);
        Files.writeString(sourceDirectory.resolve("BMSJSM1S.java"),
            new GeneratedBmsSymbolicAliasAdapter().addSendFormAliases(symbolicSource));

        CEntityClass program = OnlineCorpusSupport.analyze(transcoder, "BMSJSON1");
        assertNotNull(program, "The proof COBOL program must produce a semantic root");
        String source = TemplateLoader.getRecursiveAssembler()
            .renderRoot(program, JavaTemplateRole.ROOT);
        assertTrue(source.contains("receiveMap") && source.contains(".into(bmsjsm1.bmsjs0f)"),
            "RECEIVE MAP and its target must survive translation");
        assertTrue(source.contains("isEqual(bmsjsm1.request, \"PING\")")
            && source.contains("move(\"PONG\", bmsjsm1s.sresult)"),
            "The COBOL IF/MOVE business branch must survive translation");
        assertTrue(source.contains("sendMap") && source.contains(".freeKB()"),
            "SEND MAP terminal semantics must survive translation");
        Matcher className = PUBLIC_CLASS.matcher(source);
        assertTrue(className.find(), "Generated source must declare one public program class");
        Files.writeString(sourceDirectory.resolve(className.group(1) + ".java"), source);

        IncludeGroupSupport.configure(includes.toString(), target.toString());
        String dfhaid = IncludeGroupSupport.generateCopybookClass("DFHAID");
        assertNotNull(dfhaid, "DFHAID must generate with the proof program");
        Files.writeString(sourceDirectory.resolve("Dfhaid.java"), dfhaid);

        ProcessBuilder compiler = new ProcessBuilder(
            "javac", "-d", classesDirectory.toString(),
            "-classpath", System.getProperty("java.class.path"), "-proc:none",
            sourceDirectory.resolve("BMSJSM1.java").toString(),
            sourceDirectory.resolve("BMSJSM1S.java").toString(),
            sourceDirectory.resolve("Dfhaid.java").toString(),
            sourceDirectory.resolve(className.group(1) + ".java").toString());
        compiler.redirectErrorStream(true);
        Process compilation = compiler.start();
        String errors = new String(compilation.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(compilation.waitFor() == 0, errors);

        ProcessBuilder runtime = new ProcessBuilder(
            "java", "-cp", System.getProperty("java.class.path") + File.pathSeparator
                + classesDirectory,
            CardDemoBmsProgramRunner.class.getName(), className.group(1),
            classesDirectory.toString());
        runtime.redirectErrorStream(true);
        Process execution = runtime.start();
        String runtimeOutput = new String(
            execution.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(execution.waitFor() == 0, runtimeOutput);
        assertTrue(runtimeOutput.contains("RESULT_JSON=")
            && runtimeOutput.contains("\"result\":{\"value\":\"PONG"), runtimeOutput);
    }
}
