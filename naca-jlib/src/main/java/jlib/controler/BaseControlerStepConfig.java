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

import jlib.xml.Tag;

public abstract class BaseControlerStepConfig
{

	public BaseControlerStepConfig(String csStepName)
	{
		csName = csStepName ;
	}
	
	private String csName = "" ;
	private boolean isactive = false ;
	private int nDelayBeforeStart = 0 ;
	private int nDelayBeforeRestart = 0 ;

	public String getName()
	{
		return csName ;
	}

	public boolean isActive()
	{
		return isactive;
	} 

	protected int getDelayBeforeStart()
	{
		return nDelayBeforeStart;
	}

	protected int getDelayBeforeRestart()
	{
		return nDelayBeforeRestart ;
	}

	void Setup(Tag tagSite)
	{
		nDelayBeforeStart = tagSite.getValAsInt("startdelay") ;
		nDelayBeforeRestart = tagSite.getValAsInt("restartdelay") ;
		String cs = tagSite.getVal("status") ;
		if (cs.equalsIgnoreCase("active"))
		{
			isactive = true ;
		}
		else
		{
			isactive = false ;
		}
		
		intSetup(tagSite) ;
	}

	protected abstract void intSetup(Tag tagSite) ;
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
//	}
}