package com.publicitas.naca.tests.acceptance;

import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineSession;
import java.io.PrintStream;
import java.util.ArrayList;
import jlib.classLoader.CodeManager;
import jlib.log.Log;
import jlib.log.LogCenterConsole;
import jlib.log.LogCenterLoader;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;
import jlib.log.LogParams;
import jlib.log.PatternLayoutConsole;
import jlib.misc.BasePic9Comp3BufferSupport;
import jlib.misc.LogicalFileDescriptor;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.batchPrgEnv.BatchProgramLoader;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.tempCache.TempCacheLocator;

/** Runs one freshly compiled acceptance program in an isolated JVM. */
public final class AcceptanceProgramRunner {

    private static final int FILE_MAPPING_ARGUMENT_COUNT = 4;
    private static final int NAMED_DESCRIPTOR_ARGUMENT_COUNT = 5;
    private static final String CARD_FILE_LOGICAL_NAME = "CARDFILE";

    public static void main(String[] args) {
        String className = args[0];
        String classesDir = args[1];

        LogCenterLoader loaderConfig = new StandardLogCenterLoader();
        PrintStream standardOutput = System.out;
        LogCenterConsole center = new LogCenterConsole(loaderConfig) {
            @Override
            protected void sendOutput(LogParams logParam) {
                standardOutput.println(logParam.toString());
            }
        };
        center.setPatternLayout(new PatternLayoutConsole("%Message"));
        Log.registerLogCenter(center);

        CodeManager.setPath(classesDir);
        CodeManager.initLoadPossibilities(true, false);
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();

        BaseProgramLoader loader = new BatchProgramLoader(null, null);
        OnlineSession session = new OnlineSession(false);
        if (args.length == FILE_MAPPING_ARGUMENT_COUNT) {
            session.putLogicalFileDescriptor("FILEIN",
                fixedAsciiFile("FILEIN", args[2]));
            session.putLogicalFileDescriptor("FILEOUT",
                fixedAsciiFile("FILEOUT", args[3]));
        }
        if (args.length >= NAMED_DESCRIPTOR_ARGUMENT_COUNT) {
            LogicalFileDescriptor mapped = descriptor(args[2], args[3], args[4]);
            session.putLogicalFileDescriptor(args[2], mapped);
            if (CARD_FILE_LOGICAL_NAME.equals(args[2])) {
                session.putLogicalFileDescriptor(CARD_FILE_LOGICAL_NAME + "-FILE", mapped);
            }
        }
        OnlineEnvironment environment =
            (OnlineEnvironment) loader.GetEnvironment(session, null, null);
        environment.setNextProgramToLoad(className);
        loader.runTopProgram(
            environment, new ArrayList<BaseCalledPrgPublicArgPositioned>());
    }

    private static LogicalFileDescriptor fixedAsciiFile(String logicalName, String path) {
        return new LogicalFileDescriptor(logicalName, path + ",ascii,fb,69");
    }

    private static LogicalFileDescriptor descriptor(
        String logicalName, String path, String format) {
        return new LogicalFileDescriptor(logicalName, path + "," + format);
    }

    private static final class StandardLogCenterLoader extends LogCenterLoader {
        private StandardLogCenterLoader() {
            logLevel = LogLevel.Normal;
            logFlow = LogFlowStd.Any;
            csChannel = "NacaRT";
        }
    }

    private AcceptanceProgramRunner() {
    }
}
