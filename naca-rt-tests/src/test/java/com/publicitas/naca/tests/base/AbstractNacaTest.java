/*
 * NacaRTTests - Test Suite for NacaRT
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package com.publicitas.naca.tests.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
import nacaLib.callPrg.CalledEnvironment;
import nacaLib.callPrg.CalledProgramLoader;
import nacaLib.callPrg.CalledResourceManager;
import nacaLib.callPrg.CalledSession;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.tempCache.TempCacheLocator;
import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineProgramLoader;
import idea.onlinePrgEnv.OnlineResourceManager;
import idea.onlinePrgEnv.OnlineSession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public abstract class AbstractNacaTest {

    private static boolean codeManagerInitialized = false;
    protected StringBuilder logOutput;

    @BeforeAll
    static void initCodeManager() {
        if (!codeManagerInitialized) {
            String classPath = System.getProperty("java.class.path");
            String testClassesPath = findTestClassesPath();
            
            if (testClassesPath != null) {
                CodeManager.setPath(testClassesPath);
            }
            CodeManager.initLoadPossibilities(true, false);
            codeManagerInitialized = true;
        }
    }
    
    private static String findTestClassesPath() {
        String userDir = System.getProperty("user.dir");
        if (userDir != null && userDir.contains("naca-rt-tests")) {
            return userDir + "/build/classes/java/main/";
        }
        return userDir + "/build/classes/java/main/";
    }

    @BeforeEach
    protected void setUp() {
        logOutput = new StringBuilder();
        setupLogCenter();
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();
        nacaLib.testSupport.TestAssertionCollector.startCollecting();
    }

    protected void setupLogCenter() {
        LogCenterConsole logCenter = new LogCenterConsole(
                new LogCenterLoader() {
                    {
                        logLevel = LogLevel.Normal;
                        logFlow = LogFlowStd.Any;
                        csChannel = "NacaRT";
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

    protected String runProgram(String programName) {
        return runProgram(programName, false);
    }

    protected String runProgram(String programName, boolean isBatch) {
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
        // Capitalize first letter for class name convention
        String className = simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);
        
        // Try ExtraTests package first (for ExtraTests test cases)
        String extraTestsClass = "nacaTests.ExtraTests." + className;
        try {
            Class.forName(extraTestsClass);
            return extraTestsClass;
        } catch (ClassNotFoundException e) {
            // Fall through to CobolLikeSupport
        }
        
        // Try CobolLikeSupport package
        String cobolLikeClass = "nacaTests.CobolLikeSupport." + className;
        try {
            Class.forName(cobolLikeClass);
            return cobolLikeClass;
        } catch (ClassNotFoundException e) {
            // Fall through to return default
        }
        
        // Return default path for error message purposes
        return cobolLikeClass;
    }


    protected String readExpectedOutput(String resourceName) {
        return readResource(resourceName + ".out");
    }

    protected String readResource(String resourceName) {
        StringBuilder content = new StringBuilder();
        try (InputStream in = getClass().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new RuntimeException("Resource not found: " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourceName, e);
        }
        return content.toString();
    }

    protected String normalizeOutput(String output) {
        if (output == null) {
            return "";
        }
        // Remove "Loading program instance:" lines for consistent comparison
        StringBuilder normalized = new StringBuilder();
        for (String line : output.split("\n")) {
            if (!line.startsWith("Loading program instance:")) {
                normalized.append(line).append("\n");
            }
        }
        return normalized.toString().trim() + "\n";
    }
    
    protected void assertNoFailures() {
        nacaLib.testSupport.TestAssertionCollector collector = nacaLib.testSupport.TestAssertionCollector.getInstance();
        if (collector.hasFailures()) {
            org.junit.jupiter.api.Assertions.fail(
                "Test program assertions failed:\n" + collector.getFailureSummary()
            );
        }
    }
    
    protected int getFailureCount() {
        return nacaLib.testSupport.TestAssertionCollector.getInstance().getFailureCount();
    }
}