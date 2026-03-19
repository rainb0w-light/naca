/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.program;

import java.util.Collection;
import java.util.Hashtable;

//import nacaLib.base.JmxGeneralStat;
import nacaLib.base.JmxGeneralStat;
import nacaLib.basePrgEnv.BaseResourceManager;

import jlib.jmxMBean.BaseCloseMBean;
import jlib.log.Log;

public class ProgramCopyOwner extends BaseCloseMBean
{
	ProgramCopyOwner(String csCopyName)
	{
		super();

		if(JmxGeneralStat.showCopyBeans())
			createMBean("Copy."+csCopyName, csCopyName);
		this.csCopyName = csCopyName;
		hashPrograms = new Hashtable<String, String>();
	}
	
	void showBean(boolean bToShow)
	{
		if(bToShow && !isBeanCreated())
			createMBean("Copy."+csCopyName, csCopyName);
		else if(!bToShow && isBeanCreated())
			unregisterMBean();
	}
	
	void add(String csProgramName)
	{
		hashPrograms.put(csProgramName, csProgramName);
	}	
	
	boolean removeProgramOwner(String csProgramName)
	{
		if(hashPrograms != null)
		{
			String cs = hashPrograms.get(csProgramName);
			if(cs != null)
			{
				hashPrograms.remove(csProgramName);
				if(hashPrograms.size() == 0)
				{
					unregisterMBean();
					return true;
				}
			}
		}
		return false;
	}
		
	protected void buildDynamicMBeanInfo()
	{
		addAttribute("Name", getClass(), "Name", String.class);
		addAttribute("NbProgramOwner", getClass(), "NbProgramOwner", int.class);
    	
    	addOperation("Unload Copy", getClass(), "unloadCopy");	//Boolean.TYPE);
	}
	
	public String getName()
	{
		return csCopyName;
	}
	
	public int getNbProgramOwner()
	{
		if(hashPrograms == null)
			return 0;
		return hashPrograms.size();
	}
	
	public void unloadCopy()
	{
		Log.logNormal("unloadCopy; Begin to unload all programs using copy "+csCopyName);
		if(hashPrograms != null)
		{
			Collection<String> colProgramNames = hashPrograms.values();
			Object arrPrograms[] = colProgramNames.toArray();
			int nNbPrograms = arrPrograms.length;
			for(int n=nNbPrograms-1; n>=0; n--)
			{
				String csProgramName = (String)arrPrograms[n]; 
				BaseResourceManager.unloadProgram(csProgramName);
			}
//			if(m_hashPrograms.size() == 0)
//				Log.logNormal("unloadCopy; Correctly unload " + nNbPrograms + " programs using copy "+csCopyName);
//			else
//				Log.logImportant("unloadCopy; ERROR: unload " + nNbPrograms + " programs using copy "+csCopyName + " but " + m_hashPrograms.size() + " remains uncorrectly loaded");
		}
		else
			Log.logImportant("unloadCopy; ERROR: No program to unload");
	}

	private String csCopyName = null;
	private Hashtable<String, String> hashPrograms = null;
}
