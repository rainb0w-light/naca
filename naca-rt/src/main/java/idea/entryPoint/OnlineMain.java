/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package idea.entryPoint;


import java.util.ArrayList;
import jlib.log.Log;
import jlib.misc.EnvironmentVar;
import jlib.misc.JVMReturnCodeManager;
import jlib.misc.StringArray;
import jlib.misc.ThreadSafeCounter;
import jlib.misc.Time_ms;
import idea.onlinePrgEnv.OnlineProgramLoader;
import idea.onlinePrgEnv.OnlineResourceManager;
import idea.onlinePrgEnv.OnlineResourceManagerFactory;
import idea.onlinePrgEnv.OnlineSession;
import nacaLib.accounting.CriteriaEndRunMain;
import nacaLib.appOpening.CalendarOpenState;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.exceptions.AbortSessionException;
import nacaLib.exceptions.CGotoException;
import nacaLib.exceptions.CStopRunException;

//import idea.programUtil.CSession;


/** Provides online main behavior. */
public class OnlineMain
{
    /** Executes the main operation. */
    public static void main(String[] args)
    {
        String csPath = null;
        String csPrgClassName = null;
        String csDB = "";
        String csLogCfg = null;
        String csConfigFile = null;
        int nNbLoops = 1;
        int nNbThreads = 1;
        int nWait = 0;

        StringArray path = new StringArray();

        EnvironmentVar.registerCmdLineArgs(args);

        if(args.length >= 2)
        {
            for(int nArg=0; nArg<args.length; nArg++)
            {
                String s = args[nArg];
                if(s.startsWith("-") || s.startsWith("/"))
                {
                    String arg = s.substring(1);
                    String argUpper = arg.toUpperCase();
                    if(argUpper.startsWith("PATH="))
                    {
                        csPath = arg.substring(5);
                        if (!csPath.endsWith("/")) {
                            csPath += "/";
                        }
                        path.add(csPath);
                    }

                    if (argUpper.startsWith("PROGRAM=")) {
                        csPrgClassName = arg.substring(8);
                    }

                    if (argUpper.startsWith("DB=")) {
                        csDB = arg.substring(3);
                    }

                    if(argUpper.startsWith("HELP"))
                    {
                        displayHelp();
                        return ;
                    }

                    if (argUpper.startsWith("LOG=")) {
                        csLogCfg = arg.substring(4);
                    }

                    if(argUpper.startsWith("NBLOOPS="))
                    {
                        String cs = arg.substring(8);
                        nNbLoops = Integer.parseInt(cs);
                    }
                    if(argUpper.startsWith("WAIT="))
                    {
                        String cs = arg.substring(5);
                        nWait = Integer.parseInt(cs);
                    }

                    if(argUpper.startsWith("NBTHREADS="))
                    {
                        String cs = arg.substring(10);
                        nNbThreads = Integer.parseInt(cs);
                    }

                    if(argUpper.startsWith("CONFIGFILE="))
                    {
                        csConfigFile = arg.substring(11);
                    }
                }
            }

            if(!csPrgClassName.equals("") && path.size() > 0)
            {
                if(BaseResourceManager.isInUpdateMode())
                {
                    Log.logCritical("Application is in update mode");
                    return;
                }

                OnlineResourceManager resourceManager = OnlineResourceManagerFactory.GetInstance(csConfigFile, csDB);
                if(BaseResourceManager.getAppOpenState() != CalendarOpenState.AppOpened)
                {
                    Log.logCritical("Application is closed");
                    return;
                }

                if(nNbThreads > 1)
                {
                    ArrayList<ThreadCJMap> threads = new ArrayList<ThreadCJMap>();
                    ThreadSafeCounter counter = new ThreadSafeCounter(nNbThreads);
                    for(int n=0; n<nNbThreads; n++)
                    {
                        CJMapThreadedRun cjmapThreadedRun = new CJMapThreadedRun(resourceManager, nNbLoops, csPrgClassName, path);
                        ThreadCJMap threadCJMap = new ThreadCJMap(counter, cjmapThreadedRun);
                        threads.add(threadCJMap);
                    }


                    //StopWatch sw = new StopWatch();
                    // Starts threads
                    for(int n=0; n<nNbThreads; n++)
                    {
                        ThreadCJMap thread = threads.get(n);
                        thread.start();
                    }

                    // Wait until all threads are over
                    while(counter.get() > 0)
                    {
                        try
                        {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else    // 1 thread only (batch mode)
                {
                    BaseEnvironment env = null;
                    try
                    {
                        BaseSession session = new OnlineSession(false) ;
                        BaseProgramLoader loader = OnlineProgramLoader.GetProgramLoaderInstance() ;

                        env = loader.GetEnvironment(session, null, null) ;
                        env.startRunTransaction();

                        loader.setPaths(path);
                        env.setNextProgramToLoad(csPrgClassName);

                        //StopWatch sw = new StopWatch();
                        for(int n =0; n<nNbLoops; n++)
                        {
                            env.setNextProgramToLoad(csPrgClassName);
                            try
                            {
                                loader.runTopProgram(env, null);
                            }
                            catch(AbortSessionException e)
                            {
                            }
                            if((n % 500) == 0)
                            {
                                int g = 0;
                            }
                        }
                        env.endRunTransaction(CriteriaEndRunMain.Normal);

                        Time_ms.wait_ms(nWait);
                    }
                    catch (CGotoException e)
                    {
                    }
                    catch (CStopRunException e)
                    {
                        env.endRunTransaction(CriteriaEndRunMain.StopRun);
                        String cs = e.getMessage();
                        Log.logImportant(cs);
                    }
                }
            }
        } else {
            displayHelp();
        }
        Log.close();
        JVMReturnCodeManager.exitJVM();
    }

    static void displayHelp()
    {
        System.out.println("JCMap: CtoJ Transcoded Cobol Application runtime and executor");
        System.out.println("Command line is");
        System.out.println("        JCMap ");
        System.out.println("        \t-Path=CaseSensitiveString [-Path=CaseSensitiveString]");
        System.out.println("        \t-Program=CaseSensitiveString ");
        System.out.println("       \t\t[-DB=Oracle|DB2] (defaulted to DB2)");
        System.out.println("        \t[-Help]");
        System.out.println("        \t[-ConfigFile=true|false] (Defaulted to false)");
        System.out.println("        \t[-NbLoops=number] (Defaulted to 1)");
        System.out.println("        \t[-Log=path and file name]");
        System.out.println("");
        System.out.println("Where - A CaseSensitiveString is a case sensitive string");
        System.out.println("      - A path can use Windows directory separators (\\)");
        System.out.println("        or, Unix one (/)");
        System.out.println("        or can be a URL");
        System.out.println("      - The Program to launch must be located one one of the specified Paths");
    }
}
