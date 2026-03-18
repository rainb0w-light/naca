/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 21 avr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.asyncTasks;

import idea.onlinePrgEnv.OnlineSession;
import jlib.misc.Time_ms;
import nacaLib.CESM.CESMStartData;
import nacaLib.accounting.CriteriaEndRunMain;
import nacaLib.appOpening.CalendarOpenState;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.AsyncThreadJmxManager;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.exceptions.AbortSessionException;

/**
 * @author U930CV
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CAsynchronousTask extends CJMapObject implements Runnable
{
	private String csProgramParent = null;
	private String csProgramToRun = null ;
	private int nDelaySeconds = 0 ;
	private Thread thread = null ;
	private boolean bInvalidate = false ;
	private CESMStartData startData = null ;

	public CAsynchronousTask(String csProgramToRun, String csProgramParent, CESMStartData startData, int nDelaySeconds) 
	{
		csProgramToRun = csProgramToRun;
		csProgramParent = csProgramParent;
		nDelaySeconds = nDelaySeconds ;
		startData = startData;
		thread = new Thread(this, csProgramToRun);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		boolean bUseJmx = BaseResourceManager.getUsingJmx();
		
		long lThreadId = thread.getId();
		String csThreadId = String.valueOf(lThreadId);
		String csThreadName = thread.getName();
		
		if(bUseJmx)
		{
			AsyncThreadJmxManager.startAsyncProgram(csThreadId, csThreadName, csProgramToRun, csProgramParent, nDelaySeconds);
		}
		
		Time_ms.wait_ms(nDelaySeconds * 1000);

		while (BaseResourceManager.isInUpdateMode())
		{
			Time_ms.wait_ms(1 * 60 * 1000);
		}
		
		CalendarOpenState openState = BaseResourceManager.getAppOpenState();
		while(openState != CalendarOpenState.AppOpened)
		{
			Time_ms.wait_ms(5 * 60 * 1000);
			openState = BaseResourceManager.getAppOpenState();
		}
		
		if (bInvalidate)
		{
			return ;
		}

		OnlineSession session = new OnlineSession(true) ;
		BaseProgramLoader loader = BaseProgramLoader.GetProgramLoaderInstance() ;
		BaseEnvironment env = loader.GetEnvironment(session, csProgramToRun, csProgramParent) ;

		env.startRunTransaction();
		
		env.enqueueData(startData);
		
		try
		{
			if(bUseJmx)
				AsyncThreadJmxManager.setRunningAsyncProgram(csThreadId, csThreadName);
			
			loader.runTopProgram(env, null);
			env.endRunTransaction(CriteriaEndRunMain.Normal);
			env.resetSession();
		}
		catch (AbortSessionException e)
		{
			env.endRunTransaction(CriteriaEndRunMain.Abort);
			env.resetSession();
		}
		catch (Exception e)
		{
			env.endRunTransaction(CriteriaEndRunMain.Abort);
			env.resetSession();
		}
		if(bUseJmx)
			AsyncThreadJmxManager.endAsyncProgram(csThreadId, csThreadName);
	}
	
	public void Start()
	{
		if (!bInvalidate)
		{
			thread.start();
		}
		else
		{
			thread = null ;
		}
	}
	
	public void Wait()
	{
		if (thread != null)
		{
			try
			{
				thread.join() ;
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void Invalidate()
	{
		bInvalidate = true ;
		thread.interrupt() ;
	}
}
