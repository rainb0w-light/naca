package com.publicitas.naca.cloudnative.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
import nacaLib.tempCache.TempCacheLocator;
import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineProgramLoader;
import idea.onlinePrgEnv.OnlineSession;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;

@Service
public class RunnerService {

    private static boolean codeManagerInitialized = false;
    private final StringBuilder logOutput;
    private LogCenterConsole currentLogCenter;

    public RunnerService() {
        this.logOutput = new StringBuilder();
        initializeCodeManager();
    }

    private void initializeCodeManager() {
        if (!codeManagerInitialized) {
            String userDir = System.getProperty("user.dir");
            if (userDir != null) {
                // Add NacaSamples src directory to classpath
                String nacaSamplesPath = userDir + "/NacaSamples/src/";
                CodeManager.setPath(nacaSamplesPath);
                System.out.println("CodeManager path set to: " + nacaSamplesPath);
            }
            // Enable class loading from file system (not JAR)
            CodeManager.initLoadPossibilities(true, false);
            System.out.println("CodeManager initialized with bCanLoadClass=true");
            codeManagerInitialized = true;
        }
    }

    /**
     * Run a transpiled COBOL program using NacaRT.
     *
     * @param programName The program name to run
     * @param programType "batch" or "online"
     * @return RunResult containing success status, output or errors
     */
    public RunResult runProgram(String programName, String programType) {
        try {
            // Validate input
            if (programName == null || programName.trim().isEmpty()) {
                return RunResult.failure("Program name is required");
            }

            // Setup log capture
            setupLogCenter();

            // Initialize required runtime components
            BasePic9Comp3BufferSupport.init();
            TempCacheLocator.setTempCache();

            // Resolve the full class name
            String fullClassName = resolveFullClassName(programName);

            // Load class with dynamic class loader that includes NacaSamples/src/
            Class<?> programClass = loadClassFromNacaSamples(fullClassName);
            if (programClass == null) {
                return RunResult.failure("Program class not found: " + programName +
                    ". Please transpile and compile the COBOL program first.");
            }

            // Create program loader based on type
            BaseProgramLoader programLoader;
            if ("batch".equalsIgnoreCase(programType)) {
                programLoader = new nacaLib.batchPrgEnv.BatchProgramLoader(null, null);
            } else {
                programLoader = OnlineProgramLoader.GetProgramLoaderInstance();
                if (programLoader == null) {
                    programLoader = new OnlineProgramLoader(null, null);
                }
            }

            // Create session and environment
            OnlineSession session = new OnlineSession(false);
            OnlineEnvironment env = (OnlineEnvironment) programLoader.GetEnvironment(session, null, null);

            // Set program to load and run
            ArrayList<BaseCalledPrgPublicArgPositioned> params = new ArrayList<>();
            env.setNextProgramToLoad(fullClassName);
            programLoader.runTopProgram(env, params);

            String output = logOutput.toString();
            return RunResult.success(output);

        } catch (Exception e) {
            e.printStackTrace();
            return RunResult.failure("Execution error: " + e.getMessage());
        }
    }

    /**
     * Load a class from NacaSamples/src/ directory using a custom class loader.
     */
    private Class<?> loadClassFromNacaSamples(String className) {
        try {
            // First try with current class loader
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                // Try with NacaSamples/src/ directory
            }

            String userDir = System.getProperty("user.dir");
            java.io.File nacaSamplesDir = new java.io.File(userDir + "/NacaSamples/src/");

            if (!nacaSamplesDir.exists()) {
                return null;
            }

            // Create URL class loader for NacaSamples/src/
            java.net.URL url = nacaSamplesDir.toURI().toURL();
            java.net.URLClassLoader urlClassLoader = new java.net.URLClassLoader(
                new java.net.URL[] { url },
                Thread.currentThread().getContextClassLoader()
            );

            Class<?> cls = urlClassLoader.loadClass(className);

            // Register with CodeManager so it can be found later
            CodeManager.setPath(userDir + "/NacaSamples/src/");
            CodeManager.initLoadPossibilities(true, false);

            return cls;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupLogCenter() {
        logOutput.setLength(0); // Clear previous output

        currentLogCenter = new LogCenterConsole(
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
        currentLogCenter.setPatternLayout(new PatternLayoutConsole("%Message"));
        Log.registerLogCenter(currentLogCenter);
    }

    private String resolveFullClassName(String simpleName) {
        if (simpleName == null || simpleName.isEmpty()) {
            return "Program";
        }

        // Capitalize first letter for class name convention
        String className = simpleName.toUpperCase();

        // Try common packages used by NacaTrans
        String[] packages = {
            "",  // default package
            "batch.",
            "online.",
            "commons.",
            "commons.include."
        };

        for (String pkg : packages) {
            String fullClassName = pkg.isEmpty() ? className : pkg + className;
            try {
                Class.forName(fullClassName);
                return fullClassName;
            } catch (ClassNotFoundException e) {
                // Try next package
            }
        }

        // Return the simple name uppercase as default
        return className;
    }

    /**
     * Result class for program execution operations.
     */
    public static class RunResult {
        private final boolean success;
        private final String output;
        private final List<String> errors;

        private RunResult(boolean success, String output, List<String> errors) {
            this.success = success;
            this.output = output;
            this.errors = errors;
        }

        public static RunResult success(String output) {
            return new RunResult(true, output, new ArrayList<>());
        }

        public static RunResult failure(String error) {
            List<String> errors = new ArrayList<>();
            errors.add(error);
            return new RunResult(false, null, errors);
        }

        public boolean isSuccess() { return success; }
        public String getOutput() { return output; }
        public List<String> getErrors() { return errors; }
    }
}
