/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 28 avr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.programPool;


import java.util.Stack;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jlib.classLoader.CodeManager;
import jlib.jmxMBean.BaseCloseMBean;
import jlib.log.Log;
import jlib.misc.ThreadSafeCounter;
import jlib.misc.Time_ms;
import nacaLib.base.JmxGeneralStat;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.classLoad.CustomClassDynLoaderFactory;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

// Each program has it's own pool of instances
public class ProgramInstancesPool extends BaseCloseMBean
{
	ProgramInstancesPool(ProgramPoolManager programPoolManager, String csProgramName)
	{
		super();
		
		if(JmxGeneralStat.showProgramBeans())
			createMBean("Prog." + csProgramName, csProgramName);
		
		programPoolManager = programPoolManager;
		csProgramName = csProgramName;		
	}
	
	void showBean(boolean bToShow)
	{
		if(bToShow && !isBeanCreated())
			createMBean("Prog." + csProgramName, csProgramName);
		else if(!bToShow && isBeanCreated())
			unregisterMBean();
	}
	
	protected void buildDynamicMBeanInfo()
	{
		addAttribute("Name", getClass(), "Name", String.class);
		addAttribute("Instance", getClass(), "Instances", String.class);
    	addAttribute("Nb Total Execution", getClass(), "NbTotalExecution", int.class);
    	addAttribute("WSsize", getClass(), "Mem_WorkingStorageSize", int.class);
    	addAttribute("Nb Var", getClass(), "Mevariables", String.class);
    	addAttribute("ExecRunTime", getClass(), "Time_ExecRunTime", String.class);
    	addAttribute("ExecHour", getClass(), "Time_ExecHour", String.class);
    	
    	addOperation("Unload program", getClass(), "unloadProgram");	//Boolean.TYPE);
	}
	
	public String getName()
	{
		return csProgramName;
	}
	
	
	public int getMem_WorkingStorageSize()
	{
		SharedProgramInstanceData sharedProgramInstanceData = getSharedProgramInstanceDataCatalog();
		if(sharedProgramInstanceData != null)
			return sharedProgramInstanceData.getBufferSize();
		return 0;		
	}
	
	public int getMem_NbVarDef()
	{
		SharedProgramInstanceData sharedProgramInstanceData = getSharedProgramInstanceDataCatalog();
		if(sharedProgramInstanceData != null)
			return sharedProgramInstanceData.getNbVarDef();
		return 0;
	}
	
	public String getMevariables()
	{
		SharedProgramInstanceData sharedProgramInstanceData = getSharedProgramInstanceDataCatalog();
		if(sharedProgramInstanceData != null)
		{
			int nNbForm = sharedProgramInstanceData.getNbVarDefForm();
			int nNbVar = sharedProgramInstanceData.getNbVarDef();
			int nNbCursor = sharedProgramInstanceData.getNbCursor();
			String cs = "Variables:"+nNbVar+"  Forms:"+nNbForm+"  Cursors:"+nNbCursor;
			return cs;
		}	
		return "";
	}
	
	public String getInstances()
	{
		int nNbCreated = nbInstanceCreated.get();
		int nNbStacked = getNbInstancesStacked();
		int nNbRunning = nNbCreated-nNbStacked;
		return "Running:"+nNbRunning+ "  Created:"+nNbCreated+"  Stacked:"+nNbStacked;
	}
	
	int getNbInstancesCreated()
	{
		return nbInstanceCreated.get();
	}
	
	synchronized public String getTime_ExecRunTime()
	{
		long lMin = Long.MAX_VALUE;
		long lMax = 0;
		long lSum = 0;
		long lAvg = 0;
		int nNbInstances = stack.size();
		for(int n=0; n<nNbInstances; n++)
		{
			BaseProgram program = stack.elementAt(n);
			long l = program.getProgramManager().getTimeRun();
			if(l < lMin)
				lMin = l;
			if(l > lMax)
				lMax = l;
			lSum += l;
		}
		if(nNbInstances!= 0)
			lAvg = lSum / nNbInstances;  	
		
		String csAvg = Time_ms.formatHHMMSS_ms(lAvg);
		String csMin = Time_ms.formatHHMMSS_ms(lMin);
		String csMax = Time_ms.formatHHMMSS_ms(lMax);
		return "Min:"+csMin+"   Average:"+csAvg+"   Max:"+csMax;
	}
	
	synchronized public String getTime_ExecHour()
	{
		long lOldRun = Long.MAX_VALUE;
		long lRecentRun = 0;
		int nNbInstances = stack.size();
		for(int n=0; n<nNbInstances; n++)
		{
			BaseProgram program = stack.elementAt(n);
			long l = program.getProgramManager().getTimeLastRunBegin_ms();
			if(l < lOldRun)
				lOldRun = l;
			if(l > lRecentRun)
				lRecentRun = l;
		}

		String csOldest = Time_ms.formatDMY_HHMMSS_ms(lOldRun);
		String csRecent = Time_ms.formatDMY_HHMMSS_ms(lRecentRun);
		return csOldest + "   TO   "+csRecent;
	}
	
	public int getNbTotalExecution()
	{
		return nbTotalExecution.get();
	}
	
	synchronized public int getNbInstancesStacked()
	{
		return stack.size();
	}
	
	SharedProgramInstanceData getSharedProgramInstanceDataCatalog()
	{
		SharedProgramInstanceData sharedProgramInstanceData = SharedProgramInstanceDataCatalog.getSharedProgramInstanceData(csProgramName);
		return sharedProgramInstanceData;
	}
	
	public BaseProgram getOrCreateUnusedInstance()
	{
		unloadProgramRWLock.readLock().lock();	// Get exclusive lock
		
		if(stack.size() > 0)
		{
			BaseProgram program = stack.pop();
			if(program != null)
			{
				BaseProgramManager programManager = program.getProgramManager();
				if(programManager != null)
				{
					programManager.setOldInstance();
					programManager.setLastTimeRunBegin();
					Log.logVerbose("Retrieved program instance:" + csProgramName + " From pool");
					return program;
				}
			}
		}

		Log.logVerbose("No available instance in program's pool: "+csProgramName+"; create a new one");
		BaseProgram program = createNewInstance();
		if(program == null)	// Could not load the class: Release lock
			unloadProgramRWLock.readLock().unlock();	// Get exclusive lock
		return program;
	}
	
	public BaseProgram preloadSecondInstance()
	{
		unloadProgramRWLock.readLock().lock();	// Get exclusive lock
		BaseProgram program = createNewInstance();
		if(program == null)	// Could not load the class: Release lock
			unloadProgramRWLock.readLock().unlock();	// Get exclusive lock
		return program;
	}
	
	private synchronized BaseProgram createNewInstance()
	{
		Object obj = CodeManager.getInstance(csProgramName, CustomClassDynLoaderFactory.getInstance());
		if(obj != null)
		{
			BaseProgram program = (BaseProgram) obj;
			nbInstanceCreated.inc();
			//program.getProgramManager().declareInstance(csProgramName);
			//Program programInstance = new ProgramInstance(csProgramName, program, true);
			return program;
		}
		return null;
	}
	
	public void unloadProgram()
	{
		Log.logImportant("unloadProgram; Begin unload program "+csProgramName);
		unloadProgramRWLock.writeLock().lock();	// Get exclusive lock

		// No program instance is running
		doUnloadProgram();

		unloadProgramRWLock.writeLock().unlock();	// Release exclusive lock; unlocking optinal thread waiting to obtain read lock in getUnusedInstance()
		Log.logImportant("unloadProgram; End unload program "+csProgramName);
	}
	
	private void doUnloadProgram()
	{	
		// At that step, we are the only thread owning a program instance
		// Dequeue all unused instances in the stack in order to delete them
		while(stack.size() > 0)
		{
			BaseProgram program = stack.pop();			
			program.getProgramManager().unloadClassCode();		// Destroy the program's class			
			nbInstanceCreated.dec();
		}
		stack = null;
		stack = new Stack<BaseProgram>();
		
		int nNbCreated = nbInstanceCreated.get();
		if(nNbCreated == 0)	// No more instance in the stack
		{			
			programPoolManager.removeProgramInstancesPool(csProgramName);	// Remove ourself form our's container
			
			SharedProgramInstanceDataCatalog.removeSharedProgramInstanceData(csProgramName);
			unregisterMBean();
			
			// unload code
			CodeManager.removeAllInstances(csProgramName);
		}
		else
		{
			Log.logCritical("unloadProgram: all instances should have ben deleted");
			// Should never happen
		}
	}
	
	public void returnProgram(BaseProgram program)
	{
		if(program != null)
		{
			program.getProgramManager().prepareBeforeReturningToPool();
			nbTotalExecution.inc();
			stack.push(program);
			Log.logVerbose("returnProgram: returned program to pool "+csProgramName);
		}
		unloadProgramRWLock.readLock().unlock();	// Release read lock: the current thread do not own anymore the program instance
	}
		
	private Stack<BaseProgram> stack = new Stack<BaseProgram>();
	private ThreadSafeCounter nbInstanceCreated = new ThreadSafeCounter();
	private String csProgramName = null;
	private ThreadSafeCounter nbTotalExecution = new ThreadSafeCounter();
	private ProgramPoolManager programPoolManager = null;
	
	
	private ReentrantReadWriteLock unloadProgramRWLock = new ReentrantReadWriteLock();
}