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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Translation Pipeline Tests")
public class TranslationPipelineTest {

    private static boolean codeManagerInitialized = false;
    private StringBuilder logOutput;

    @BeforeAll
    static void initCodeManager() {
        if (!codeManagerInitialized) {
            String userDir = System.getProperty("user.dir");
            String testClassesPath = userDir + "/build/classes/java/main/";
            CodeManager.setPath(testClassesPath);
            CodeManager.initLoadPossibilities(true, false);
            codeManagerInitialized = true;
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
            java.nio.file.Path projectRoot = java.nio.file.Path.of(System.getProperty("user.dir")).getParent();
            java.nio.file.Path cobolSource = projectRoot.resolve("NacaSamples/cobol/BATCH1.cbl");
            java.nio.file.Path javaSource = projectRoot.resolve("NacaSamples/src/batch/BATCH1.java");
            
            assertThat(cobolSource).exists();
            assertThat(javaSource).exists();
            
            System.out.println("BATCH1 COBOL source: " + cobolSource.toAbsolutePath());
            System.out.println("BATCH1 Java source: " + javaSource.toAbsolutePath());
        }

        @Test
        void testCallMsgFilesExist() {
            java.nio.file.Path projectRoot = java.nio.file.Path.of(System.getProperty("user.dir")).getParent();
            java.nio.file.Path cobolSource = projectRoot.resolve("NacaSamples/cobol/CALLMSG.cbl");
            java.nio.file.Path javaSource = projectRoot.resolve("NacaSamples/src/commons/CALLMSG.java");
            
            assertThat(cobolSource).exists();
            assertThat(javaSource).exists();
        }

        @Test
        void testMsgzoneFilesExist() {
            java.nio.file.Path projectRoot = java.nio.file.Path.of(System.getProperty("user.dir")).getParent();
            java.nio.file.Path cobolCopybook = projectRoot.resolve("NacaSamples/cobol/include/MSGZONE");
            java.nio.file.Path javaCopybook = projectRoot.resolve("NacaSamples/src/commons/include/MSGZONE.java");
            
            assertThat(cobolCopybook).exists();
            assertThat(javaCopybook).exists();
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
}