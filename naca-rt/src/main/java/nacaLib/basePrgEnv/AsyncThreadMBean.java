/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import jlib.jmxMBean.BaseCloseMBean;
import jlib.misc.DateUtil;
import jlib.misc.StopWatch;
import nacaLib.base.JmxGeneralStat;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: AsyncThreadMBean.java,v 1.2 2006/12/20 13:59:53 u930bm Exp $
 */
public class AsyncThreadMBean extends BaseCloseMBean
{
	private String csThreadName = null;
	private String csThreadId = null;
	private boolean bWaiting = false;
	private String csLastWaitEvent = ""; 
	private String csProgram = "";
	private String csProgramParent = "";
	private int nDelaySeconds = 0;
	private StopWatch sw = null;

	AsyncThreadMBean(String csThreadId, String csThreadName)
	{
		super();
		
		csThreadName = csThreadName;
		csThreadId = csThreadId;
		sw = new StopWatch();
		if(JmxGeneralStat.showAsyncThreadBeans())
		{
			create();
		}
	}
	
	void setAsyncThreadClosed()
	{
		unregisterMBean();
	}
	
	void showBean(boolean bToShow)
	{
		if(bToShow && !isBeanCreated())
			create();
		else if(!bToShow && isBeanCreated())
			unregisterMBean();
	}
	
	private void create()
	{
		String cs = getAsyncThreadMBeanId(csThreadName, csThreadId);
		createMBean(cs, cs);
	}
	
	void setWait(boolean bWaiting)
	{
		if(bWaiting != bWaiting)	// Changing state
			sw.Reset();
		bWaiting = bWaiting;
		csLastWaitEvent = DateUtil.getDisplayTimeStamp();
	}
	
	void setProgram(String csProgram)
	{
		csProgram = csProgram;
	}
	
	void setProgramParent(String csProgramParent)
	{
		csProgramParent = csProgramParent;
	}
	
	void setDelaySeconds(int nDelaySeconds)
	{
		nDelaySeconds = nDelaySeconds;
	}
	
	private static String getAsyncThreadMBeanId(String csThreadId, String csThreadName)
	{
		return "AsyncThread." + csThreadName + "." + csThreadId;
	}
	
	protected void buildDynamicMBeanInfo()
	{
		addAttribute("ThreadName", getClass(), "A_ThreadName", String.class);
		addAttribute("ThreadId", getClass(), "B_ThreadId", String.class);
		addAttribute("Program", getClass(), "C_Program", String.class);
		addAttribute("ProgramParent", getClass(), "D_ProgramParent", String.class);
		addAttribute("WaitStatus", getClass(), "E_WaitStatus", String.class);
		addAttribute("DelaySecond", getClass(), "F_DelaySeconds", int.class);
	}

	public String getA_ThreadName()
	{
		return csThreadName;
	}
	
	public String getB_ThreadId()
	{
		return csThreadId;
	}
	
	public String getC_Program()
	{
		return csProgram;
	}
	
	public String getD_ProgramParent()
	{
		return csProgramParent;
	}
	
	public String getE_WaitStatus()
	{
		String cs;
		if(bWaiting)
			cs = "Waiting since " + csLastWaitEvent;
		else
			cs = "Running since " + csLastWaitEvent;
		long lElapsedTime_s = sw.getElapsedTime() / 1000;
		cs = cs + " (" + lElapsedTime_s + " s)";
		return cs;
	}
	
	public int getF_DelaySeconds()
	{
		return nDelaySeconds;
	}
}
