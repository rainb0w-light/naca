/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.controler;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;


import jlib.log.stdEvents.LoadConfigCompleted;
import jlib.log.stdEvents.LoadConfigError;
import jlib.log.stdEvents.LoadConfigStart;
import jlib.xml.Tag;
import jlib.xml.TagCursor;

public abstract class BaseControlerConfig
{
	private String csTaskTagName = "" ;
	private String csStepTagName = "" ;
	
	protected BaseControlerConfig(String csTaskTagName, String csStepTagName)
	{
		csTaskTagName = csTaskTagName ;
		csStepTagName = csStepTagName ;
	}
	
	public abstract int getNbTasks() ;

	public abstract BaseControlerTaskConfig getTaskConfig(String name) ;

//		LoadConfigStart.log(getLogChannel(), fConfigFile.getAbsolutePath(), "Start loading from file...") ;
//		Tag tagRoot = Tag.createFromFile(fConfigFile) ;
//	    if (tagRoot != null)
//	    {
//			Tag tagConf = tagRoot.getChild("Config");
//		    Setup(tagConf) ;
//			
//			TagCursor cur = new TagCursor() ;
//			Tag tagSite = tagRoot.getFirstChild(cur, csTaskTagName);
//			while (cur.isValid() && tagSite != null)
//			{
//				String name = tagSite.getVal("site") ;
//				HeritrixSiteConfig siteconf = getConfForSite(name) ;
//				if (siteconf == null)
//				{
//					siteconf = new HeritrixSiteConfig() ;
//					arrSites.add(siteconf) ;
//				}
//				siteconf.setFromTag(tagSite) ;
//				
//				tagSite = tagRoot.getNextChild(cur) ;
//			}
//		    LoadConfigCompleted.log("Conf loading completed.") ;
//		    
//		    DoCVSUpdate() ;
//		}
//	    else
//	    {
//		    LoadConfigError.log(fConfigFile.getAbsolutePath(), "Conf loading completed.") ;
//	    }
//	}
	
	private File fConfigFile = null ;

	private boolean isautoStart = false ;
	public boolean isAutoStart()
	{
		return isautoStart;
	}
	
	
	public void setXMLFile(String cs)
	{
		File f = new File(cs) ;
		setXMLFile(f) ;
	}
	public abstract String getLogChannel() ;
	
	public void setXMLFile(File file)
	{
		fConfigFile = file ;
	}
	
	synchronized void LoadConfig(ControlerDirector director)
	{
		LoadConfigStart.log(getLogChannel(), fConfigFile.getAbsolutePath(), "Start loading from file...") ;
		Tag tagRoot = Tag.createFromFile(fConfigFile) ;
	    if (tagRoot != null)
	    {
	    	// init global parameters
			Tag tagConf = tagRoot.getChild("Config");
		    Setup(tagConf) ;
		    
		    // get a table with all existing tasks
		    Hashtable<String, BaseControlerTaskConfig> tabTasks = getTabConfig() ;
		    
		    // iteration on all task Tags
			TagCursor curgrp = new TagCursor() ;
			Tag tagGroup = tagRoot.getFirstChild(curgrp, csTaskTagName);
			while (curgrp.isValid() && tagGroup != null)
			{
				String name = tagGroup.getVal("name") ;
				BaseControlerTaskConfig grpConfig = tabTasks.get(name) ;
				boolean isnewTask = false ;
				if (grpConfig == null)
				{ // if task doesn't exist yet
					grpConfig = NewTaskConfig(name) ;
					isnewTask = true ;
				}
				// task own configuration
				grpConfig.Setup(tagGroup) ;
				
				// get a table with all steps of that task
				Hashtable<String, BaseControlerStepConfig> tabSteps = grpConfig.getTabConfig() ;
				
				// iteration on all step Tags
				TagCursor cur = new TagCursor() ;
				Tag tagSite = tagGroup.getFirstChild(cur, csStepTagName);
				int nStepIndex = 0 ;
				while (cur.isValid() && tagSite != null)
				{
					String stepName = tagSite.getVal("name") ;
					BaseControlerStepConfig stepConf = tabSteps.get(stepName) ;
					boolean isnewStep = false ;
					if (stepConf == null)
					{ // step doesn't exist yet
						isnewStep = true ;
						stepConf = grpConfig.NewStepConfig(stepName, nStepIndex) ;
					}
					// step own configuration
					stepConf.Setup(tagSite) ;
					
					
					if (isnewStep && !isnewTask)
					{ // the step is new, but not the task : tel the director
						director.AddStepToTask(grpConfig/*, nStepIndex, stepConf*/) ;
					}
					else if (!isnewStep)
					{ // remove step of known steps
						tabSteps.remove(stepName) ;
					}
					nStepIndex ++ ;
					tagSite = tagRoot.getNextChild(cur) ;
				}
				
				if (!tabSteps.isEmpty())
				{ // known steps have not been found in XML : remove them
					Enumeration<BaseControlerStepConfig> enm = tabSteps.elements() ;
					while  (enm.hasMoreElements()) 
					{
						BaseControlerStepConfig conf = enm.nextElement() ;
						//conf.OnDeleteConfig() ;
//						int nIndex = grpConfig.FindStepConfig(conf);
						grpConfig.RemoveStepConfig(conf) ;
						director.RemoveStepFromTask(grpConfig) ;
					}
				}

				if (isnewTask)
				{ // the task is new : create controler
					director.AddNewTask(grpConfig) ;
				}
				else
				{ // remove task from known tasks
					tabTasks.remove(name) ;
				}
				tagGroup = tagRoot.getNextChild(curgrp) ;
			}
		    
			// iteration on single steps remaining in XML
			TagCursor cur = new TagCursor() ;
			Tag tagSite = tagRoot.getFirstChild(cur, csStepTagName);
			while (cur.isValid() && tagSite != null)
			{
				String name = tagSite.getVal("name") ;
				BaseControlerTaskConfig grpConfig = tabTasks.get(name) ;
				boolean isnewTask = false ;
				if (grpConfig == null)
				{ // the task is new
					isnewTask = true ;
					grpConfig = NewTaskConfig(name) ;
				}
				else if(grpConfig.getNbSteps() > 1)
				{ // the task exists but with more than one step : delete and remake
//					grpConfig.OnDeleteConfig() ;
					director.RemoveTask(grpConfig) ;
					RemoveTaskConfig(grpConfig) ;
					grpConfig = NewTaskConfig(name) ;
					isnewTask = true ;
				}
				// task own configuration
				grpConfig.Setup(tagSite) ;
		
				BaseControlerStepConfig stepConf = null ;
				if (grpConfig.getNbSteps()>0)
				{ // single step already exists
					stepConf = grpConfig.getStep(0) ;
				}
				else
				{ // no step exists
					stepConf = grpConfig.NewStepConfig(name, 0) ;
				}
				// step own configuration
				stepConf.Setup(tagSite) ;
				
				if (isnewTask)
				{ // task is new : create a controler
					director.AddNewTask(grpConfig) ;
				}
				else
				{ // reove task from known tasks
					tabTasks.remove(name) ;
				}
				tagSite = tagRoot.getNextChild(cur) ;
			}

			if (!tabTasks.isEmpty())
			{
				Enumeration<BaseControlerTaskConfig> enm = tabTasks.elements() ;
				while  (enm.hasMoreElements()) 
				{
					BaseControlerTaskConfig conf = enm.nextElement() ;
//					conf.OnDeleteConfig() ;
					director.RemoveTask(conf) ;
					RemoveTaskConfig(conf) ;
				}
			}
			LoadConfigCompleted.log(getLogChannel(), "Conf loading completed.") ;
		}
	    else
	    {
		    LoadConfigError.log(getLogChannel(), fConfigFile.getAbsolutePath(), "Conf loading completed.") ;
	    }
	}

	protected abstract void RemoveTaskConfig(BaseControlerTaskConfig grpConfig) ;

	protected abstract Hashtable<String, BaseControlerTaskConfig> getTabConfig() ;

	protected abstract BaseControlerTaskConfig NewTaskConfig(String name) ;

	private void Setup(Tag tagConf)
	{
		isautoStart = tagConf.getValAsBoolean("AutoStart") ;
		
		intSetup(tagConf) ;
	}
	protected abstract void intSetup(Tag tagConf) ;

	
}
