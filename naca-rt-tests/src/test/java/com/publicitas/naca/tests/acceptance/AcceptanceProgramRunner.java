package com.publicitas.naca.tests.acceptance;

import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineSession;
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

    public static void main(String[] args) {
        String className = args[0];
        String classesDir = args[1];

        LogCenterConsole center = new LogCenterConsole(new LogCenterLoader() {
            {
                logLevel = LogLevel.Normal;
                logFlow = LogFlowStd.Any;
                csChannel = "NacaRT";
            }
        }) {
            @Override
            protected void sendOutput(LogParams logParam) {
                System.out.println(logParam.toString());
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
        if (args.length == 4) {
            session.putLogicalFileDescriptor("FILEIN",
                fixedAsciiFile("FILEIN", args[2]));
            session.putLogicalFileDescriptor("FILEOUT",
                fixedAsciiFile("FILEOUT", args[3]));
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

    private AcceptanceProgramRunner() {
    }
}
