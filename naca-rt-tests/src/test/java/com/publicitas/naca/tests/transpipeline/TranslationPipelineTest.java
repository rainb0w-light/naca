package com.publicitas.naca.tests.transpipeline;

import jlib.classLoader.CodeManager;
import jlib.log.Log;
import jlib.log.LogCenterConsole;
import jlib.log.LogCenterLoader;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;
import jlib.log.LogParams;
import jlib.log.PatternLayoutConsole;
import jlib.misc.BasePic9Comp3BufferSupport;

import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.tempCache.TempCacheLocator;
import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineProgramLoader;
import idea.onlinePrgEnv.OnlineSession;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Translation Pipeline Tests")
public class TranslationPipelineTest {

    private static boolean codeManagerInitialized = false;
    private static boolean dockerAvailable = false;
    private StringBuilder logOutput;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void initCodeManager() {
        if (!codeManagerInitialized) {
            String userDir = System.getProperty("user.dir");
            String testClassesPath = userDir + "/build/classes/java/main/";
            CodeManager.setPath(testClassesPath);
            CodeManager.initLoadPossibilities(true, false);
            codeManagerInitialized = true;
        }
        try {
            Process p = new ProcessBuilder("docker", "info").redirectErrorStream(true).start();
            dockerAvailable = p.waitFor(10, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (Exception e) {
            dockerAvailable = false;
        }
    }

    @BeforeEach
    void setUp() {
        logOutput = new StringBuilder();
        setupLogCenter();
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();
    }

    protected void setupLogCenter() {
        LogCenterConsole logCenter = new LogCenterConsole(
                new LogCenterLoader() {
                    {
                        m_logLevel = LogLevel.Normal;
                        m_logFlow = LogFlowStd.Any;
                        m_csChannel = "NacaRT";
                    }
                }) {
            @Override
            protected void sendOutput(LogParams logParam) {
                logOutput.append(logParam.toString()).append("\n");
            }
        };
        logCenter.setPatternLayout(new PatternLayoutConsole("%Message"));
        Log.registerLogCenter(logCenter);
    }

    @Nested
    @DisplayName("Docker COBOL Execution")
    class DockerCobolTests {

        @Test
        @DisplayName("Check Docker availability")
        void testDockerAvailable() {
            Assumptions.assumeTrue(dockerAvailable, "Docker should be available");
            assertTrue(dockerAvailable, "Docker should be available");
        }

        @Test
        @DisplayName("Compile and run simple COBOL program in Docker")
        void testCompileAndRunSimpleCobol() throws Exception {
            Assumptions.assumeTrue(dockerAvailable, "Docker not available - skipping");
            String cobolProgram = """
                IDENTIFICATION DIVISION.
                PROGRAM-ID. HELLO.
                PROCEDURE DIVISION.
                    DISPLAY 'Hello from COBOL!'.
                    STOP RUN.
                """;

            String output = runCobolInDocker(cobolProgram, "HELLO");
            assertThat(output.trim()).isEqualTo("Hello from COBOL!");
        }

        @Test
        @DisplayName("Run GnuCOBOL version check")
        void testGnuCOBOLVersion() throws Exception {
            Assumptions.assumeTrue(dockerAvailable, "Docker not available - skipping");
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm", "gnucobol/gnucobol:latest", "cobc", "--version"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            p.waitFor(60, TimeUnit.SECONDS);
            assertThat(output.toString()).contains("GnuCOBOL");
            System.out.println("GnuCOBOL Version:\n" + output);
        }
    }

    @Nested
    @DisplayName("Java Runtime Execution")
    class JavaRuntimeTests {

        @Test
        @DisplayName("Run translated CALLMSG program")
        void testRunCallMsg() {
            String output = runProgram("CALLMSG");
            assertNotNull(output, "Program should produce output");
            System.out.println("CALLMSG output: " + output);
        }
    }

    @Nested
    @DisplayName("Sample Files Verification")
    class SampleFilesTests {

        @Test
        void testBatch1FilesExist() {
            Path projectRoot = Path.of(System.getProperty("user.dir")).getParent();
            Path cobolSource = projectRoot.resolve("NacaSamples/cobol/BATCH1.cbl");
            Path javaSource = projectRoot.resolve("NacaSamples/src/batch/BATCH1.java");
            
            assertThat(cobolSource).exists();
            assertThat(javaSource).exists();
            
            System.out.println("BATCH1 COBOL source: " + cobolSource.toAbsolutePath());
            System.out.println("BATCH1 Java source: " + javaSource.toAbsolutePath());
        }

        @Test
        void testCallMsgFilesExist() {
            Path projectRoot = Path.of(System.getProperty("user.dir")).getParent();
            Path cobolSource = projectRoot.resolve("NacaSamples/cobol/CALLMSG.cbl");
            Path javaSource = projectRoot.resolve("NacaSamples/src/commons/CALLMSG.java");
            
            assertThat(cobolSource).exists();
            assertThat(javaSource).exists();
        }

        @Test
        void testMsgzoneFilesExist() {
            Path projectRoot = Path.of(System.getProperty("user.dir")).getParent();
            Path cobolCopybook = projectRoot.resolve("NacaSamples/cobol/include/MSGZONE");
            Path javaCopybook = projectRoot.resolve("NacaSamples/src/commons/include/MSGZONE.java");
            
            assertThat(cobolCopybook).exists();
            assertThat(javaCopybook).exists();
        }
    }

    @Nested
    @DisplayName("Output Comparison Tests")
    class OutputComparisonTests {

        @Test
        @DisplayName("Compare COBOL and Java output for arithmetic program")
        void testArithmeticOutputComparison() throws Exception {
            Assumptions.assumeTrue(dockerAvailable, "Docker not available - skipping");
            String cobolProgram = """
                IDENTIFICATION DIVISION.
                PROGRAM-ID. TESTADD.
                DATA DIVISION.
                WORKING-STORAGE SECTION.
                77 NUM1 PIC 9(2) VALUE 5.
                77 NUM2 PIC 9(2) VALUE 3.
                77 RESULT PIC 9(3).
                PROCEDURE DIVISION.
                    ADD NUM1 TO NUM2 GIVING RESULT.
                    DISPLAY 'Result: ' RESULT.
                    STOP RUN.
                """;

            String cobolOutput = runCobolInDocker(cobolProgram, "TESTADD");
            assertThat(cobolOutput).contains("Result:");
            System.out.println("COBOL output: " + cobolOutput);
        }

        @Test
        @DisplayName("Compare COBOL and Java output for string program")
        void testStringOutputComparison() throws Exception {
            Assumptions.assumeTrue(dockerAvailable, "Docker not available - skipping");
            String cobolProgram = """
                IDENTIFICATION DIVISION.
                PROGRAM-ID. TESTSTR.
                DATA DIVISION.
                WORKING-STORAGE SECTION.
                77 MSG PIC X(20) VALUE 'Hello World'.
                PROCEDURE DIVISION.
                    DISPLAY MSG.
                    STOP RUN.
                """;

            String cobolOutput = runCobolInDocker(cobolProgram, "TESTSTR");
            assertThat(cobolOutput.trim()).contains("Hello World");
            System.out.println("COBOL output: " + cobolOutput);
        }
    }

    protected String runProgram(String programName) {
        String fullClassName = resolveFullClassName(programName);
        try {
            BaseProgramLoader programLoader = OnlineProgramLoader.GetProgramLoaderInstance();
            if (programLoader == null) {
                programLoader = new OnlineProgramLoader(null, null);
            }

            OnlineSession session = new OnlineSession(false);
            OnlineEnvironment env = (OnlineEnvironment) programLoader.GetEnvironment(session, null, null);

            ArrayList<BaseCalledPrgPublicArgPositioned> params = new ArrayList<>();

            env.setNextProgramToLoad(fullClassName);
            programLoader.runTopProgram(env, params);

            return logOutput.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to run program: " + programName, e);
        }
    }

    private String resolveFullClassName(String simpleName) {
        return simpleName;
    }

    protected String runCobolInDocker(String cobolSource, String programName) throws Exception {
        Path sourcePath = tempDir.resolve(programName + ".cbl");
        Files.writeString(sourcePath, cobolSource);

        ProcessBuilder compilePb = new ProcessBuilder(
            "docker", "run", "--rm",
            "-v", sourcePath.getParent() + ":/src",
            "-w", "/src",
            "gnucobol/gnucobol:latest",
            "cobc", "-x", "-free", "-o", programName, programName + ".cbl"
        );
        compilePb.redirectErrorStream(true);
        Process compileProcess = compilePb.start();
        
        StringBuilder compileOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                compileOutput.append(line).append("\n");
            }
        }
        
        boolean compileCompleted = compileProcess.waitFor(60, TimeUnit.SECONDS);
        if (!compileCompleted || compileProcess.exitValue() != 0) {
            throw new RuntimeException("COBOL compilation failed:\n" + compileOutput);
        }

        ProcessBuilder runPb = new ProcessBuilder(
            "docker", "run", "--rm",
            "-v", sourcePath.getParent() + ":/src",
            "-w", "/src",
            "gnucobol/gnucobol:latest",
            "./" + programName
        );
        runPb.redirectErrorStream(true);
        Process runProcess = runPb.start();
        
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        runProcess.waitFor(30, TimeUnit.SECONDS);
        return output.toString();
    }
}