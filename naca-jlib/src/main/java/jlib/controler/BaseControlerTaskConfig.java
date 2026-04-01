/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.controler;

import java.util.Hashtable;

import jlib.xml.Tag;


public abstract class BaseControlerTaskConfig
{
	protected BaseControlerTaskConfig(String name)
	{
		csName = name ;
	}
	private String csName = "" ;
	protected enum EGroupMode 
	{
		MODE_GROUP, MODE_SITE ;
	}
	private EGroupMode eMode = EGroupMode.MODE_SITE ;

	public abstract int getNbSteps() ;

	public abstract BaseControlerStepConfig getStep(int j) ;

	public boolean isModeGroup()
	{
		return eMode == EGroupMode.MODE_GROUP ;
	}

	void Setup(Tag tagTask)
	{
		nDelayBeforeStart = tagTask.getValAsInt("startdelay") ;
		nDelayBeforeRestart = tagTask.getValAsInt("restartdelay") ;
		String cs = tagTask.getVal("mode") ;
		if (cs.equals("group"))
		{
			eMode = EGroupMode.MODE_GROUP ;
			isactive = false ; // default value
		}
		else if (cs.equals("site"))
		{
			eMode = EGroupMode.MODE_SITE ;
			isactive = true ; // default value
		}
		cs = tagTask.getVal("status") ;
		if (cs.equalsIgnoreCase("active"))
		{
			isactive = true ;
		}
		else if (cs.equalsIgnoreCase("inactive"))
		{
			isactive = false ;
		}
		
		
		intSetup(tagTask) ;
	}
	protected abstract void intSetup(Tag tagTask);

	private int nDelayBeforeStart = 0 ;
	private int nDelayBeforeRestart = 0 ;
	private boolean isactive = false ;

	public String getName()
	{
		return csName ;
	}

	protected int getDelayBeforeStart()
	{
		return nDelayBeforeStart ;
	}

	protected int getDelayBeforeRestart()
	{
		return nDelayBeforeRestart ;
	}

	public abstract String getLogChannel() ;

	protected abstract BaseControlerStepConfig NewStepConfig(String stepName, int stepIndex) ;

	protected abstract Hashtable<String, BaseControlerStepConfig> getTabConfig() ;

	protected abstract void RemoveStepConfig(BaseControlerStepConfig conf) ;

	public abstract BaseControler NewControler() ;

//	public abstract int FindStepConfig(BaseControlerStepConfig conf) ;
	
	
//	void setCurrentControler(BaseControler ctrl)
//	{
//		currentControler = ctrl ;
//	}
//	private BaseControler currentControler = null  ;
//	void OnDeleteConfig()
//	{
//		if (currentControler != null)
//		{
//			currentControler.Stop(true) ;
//		}
//		for (int i=0; i<getNbSteps(); i++)
//		{
//			BaseControlerStepConfig step = getStep(i) ;
//			step.OnDeleteConfig() ;
//		}
//	}
}