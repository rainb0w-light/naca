package com.publicitas.naca.cloudnative;

import idea.onlinePrgEnv.OnlineEnvironment;
import idea.onlinePrgEnv.OnlineSession;
import jlib.classLoader.CodeManager;
import jlib.log.Log;
import jlib.log.LogCenterConsole;
import jlib.log.LogCenterLoader;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;
import jlib.log.LogParams;
import jlib.log.PatternLayoutConsole;
import jlib.misc.BasePic9Comp3BufferSupport;
import java.util.ArrayList;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.batchPrgEnv.BatchProgramLoader;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.tempCache.TempCacheLocator;

/**
 * Standalone launcher that runs one assembled batch program on the NacaRT
 * runtime in its OWN JVM, so the run is isolated from the shared static
 * {@code CodeManager}/{@code Log} state that other tests pollute (and from
 * stale {@code NacaSamples/src/*.class} artifacts that a case-insensitive
 * filesystem would otherwise resolve ahead of the freshly compiled class).
 *
 * <p>args[0] = program class name, args[1] = directory holding its .class file.
 * Display output is sent to stdout for the calling test to capture.
 */
public final class AssembledProgramRunner
{
    public static void main(String[] args)
    {
        String className = args[0];
        String classesDir = args[1];

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
        OnlineEnvironment env = (OnlineEnvironment) loader.GetEnvironment(session, null, null);
        env.setNextProgramToLoad(className);
        loader.runTopProgram(env, new ArrayList<BaseCalledPrgPublicArgPositioned>());
    }

    private AssembledProgramRunner()
    {
    }
}
