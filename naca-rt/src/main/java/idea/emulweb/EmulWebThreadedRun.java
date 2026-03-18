/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.emulweb;

import jlib.log.Log;
import jlib.misc.StopWatch;
import idea.onlinePrgEnv.OnlineResourceManager;
import idea.onlinePrgEnv.OnlineSession;

public class EmulWebThreadedRun
{
	EmulWebThreadedRun(EmulWebRunner emulWebRunner, OnlineResourceManager resourceManager, int nbLoops, boolean bCheckScenario, boolean bOutputExport)
	{
		emulWebRunner = emulWebRunner;
		resourceManager = resourceManager;
		nbLoops = nbLoops;
		bCheckScenario = bCheckScenario;
		bOutputExport = bOutputExport;
	}
	
	void run()
	{	
		OnlineSession session = new OnlineSession(false) ;
		session.setCheckScenario(bCheckScenario);
		for (int i=0; i<nbLoops; i++)
		{
			StopWatch sw = new StopWatch();
			EmulWebRunner.PlayScenario(session, resourceManager, bOutputExport) ;
			Log.logCritical("Scneario loop executed in " + sw.getElapsedTimeReset() + " ms");
			waitUntilNextLoopEnabled(i);
			session.reset();
		}
		Log.logCritical("EmulWebRun finished");
	}
	
	private void waitUntilNextLoopEnabled(int i)
	{	
//		if(!bEnableRemainingLoops)
//		{
//			Log.logCritical("EmulWeb Loop " + i + " Done; waiting to be enabled by jmx ...");	
//			while(!bNextLoopEnabled)
//			{
//				try
//				{
//					Thread.sleep(1000L);
//				} 
//				catch (InterruptedException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			bNextLoopEnabled= false;
//		}
	}
	
	int nbLoops = 0;
	boolean bCheckScenario = false;
	boolean bOutputExport = false;
	OnlineResourceManager resourceManager = null;
	EmulWebRunner emulWebRunner = null;
}
