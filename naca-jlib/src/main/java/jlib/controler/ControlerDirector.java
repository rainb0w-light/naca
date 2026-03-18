/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.controler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ControlerDirector
{
	protected class ControlerItemDescription
	{
		public String csControlerName = "" ;
		public int nStepId = -1 ;
	}

	private Vector<ControlerItemDescription> arrDesc = null ;
//	private Vector<BaseControler> arrControler = null ;
//	private Vector<ControlerThread> arrThreads = null ;
	private BaseControlerConfig config = null;
	private Hashtable<String, BaseControler> tabControlers = null ;
	private Hashtable<String, ControlerThread> tabThreads = null ;
	
	public void Init(BaseControlerConfig config)
	{
		arrDesc = new Vector<ControlerItemDescription>() ;
//		arrControler = new Vector<BaseControler>() ;
//		arrThreads = new Vector<ControlerThread>() ;
		tabControlers = new Hashtable<String, BaseControler>() ;
		tabThreads = new Hashtable<String, ControlerThread>(); 

		config = config ;
		config.LoadConfig(this) ;
	}
	
//	public void launchControlers()
//	{
//	   	
//	    //Pour chaque site et pour chaque groupe présent dans le fichier de configuration on crée un thread.
//	    for (int i=0; i<config.getNbTasks(); i++) 
//	    {
//	    	BaseControler ctrl = factory.getControlerForTask(i) ;
//	    	BaseControlerTaskConfig confgrp = config.getTaskConfig(i) ;
//	    	
//	    	
//	    	if (config.isAutoStart())
//	    	{
//	    		ControlerThread th = new ControlerThread(ctrl) ;
//	    		arrThreads.add(th) ;
//	    		th.AutoStart() ; 
//	    	}
//	    	else
//	    	{
//		    	arrThreads.add(null) ;
//	    	}
//	    }
//
//	}

	private void AddControler(BaseControler ctrl, BaseControlerTaskConfig confgrp)	  
	{
		if (confgrp.isModeGroup())
    	{
	    	ControlerItemDescription descSite = new ControlerItemDescription() ;
	    	descSite.csControlerName = confgrp.getName() ;
	    	descSite.nStepId = 0 ; // single item
	    	arrDesc.add(descSite) ;
    	}
    	else
    	{
	    	for (int j=0; j<confgrp.getNbSteps(); j++)
	    	{
		    	BaseControlerStepConfig conf = confgrp.getStep(j) ;
		    	ControlerItemDescription descSite = new ControlerItemDescription() ;
		    	descSite.csControlerName = confgrp.getName() ;
		    	descSite.nStepId = j ; // single item
		    	arrDesc.add(descSite) ;
	    	}
    	}

    	tabControlers.put(confgrp.getName(), ctrl) ;
	}
	
	/**
	 * 
	 */
	public void StopAllControlers()
	{
		Enumeration<ControlerThread> enm = tabThreads.elements() ;
		while (enm.hasMoreElements())
		{
			ControlerThread th = enm.nextElement() ;
			if (th != null)
			{
				th.StopControler(false, true) ;
			}
		}
		tabThreads.clear() ;
	}


	public void startControler(int i, boolean bForceStart)
	{
		if (i<arrDesc.size())
		{
			ControlerItemDescription desc = arrDesc.get(i) ;
			BaseControler ctrl = tabControlers.get(desc.csControlerName) ;
			ControlerThread th = tabThreads.get(desc.csControlerName) ;
			if ((th == null || !th.isAlive()) && ctrl != null)
			{
				th = new ControlerThread(ctrl) ;
				tabThreads.put(desc.csControlerName, th) ;
				if (bForceStart)
				{
					th.StartControler(desc.nStepId) ;
				}
				else
				{
					th.AutoStart(desc.nStepId) ;
				}
			}
		}
	}

	/**
	 * @param i
	 * @param b 
	 */
	public void StopControler(int i, boolean bForce)
	{
		if (i<arrDesc.size())
		{
			ControlerItemDescription desc = arrDesc.get(i) ;
			BaseControler ctrl = tabControlers.get(desc.csControlerName) ;
			ControlerThread th = tabThreads.get(desc.csControlerName) ;
			if (th != null)
			{
				th.StopControler(bForce) ;
			}
			tabThreads.remove(desc.csControlerName) ;
		}
	}

	/**
	 * @param i
	 * @return
	 */
	public String getStatus(int i)
	{
		if (i<arrDesc.size())
		{
			ControlerItemDescription desc = arrDesc.get(i) ;
			BaseControler ctrl = tabControlers.get(desc.csControlerName) ;
			return ctrl.getStatus(desc.nStepId) ;
		}
		else
		{
			return "NONE." ;
		}
	}

	public int getNbControlers()
	{
		return arrDesc.size() ;
	}

	public void ReloadConfig()
	{
		config.LoadConfig(this) ;
	}

	public String getStepName(int i)
	{
		if (i<arrDesc.size())
		{
			ControlerItemDescription desc = arrDesc.get(i) ;
			BaseControler ctrl = tabControlers.get(desc.csControlerName) ;
			String name = ctrl.getStepName(desc.nStepId) ;
			return name ;
		}
		else
		{
			return "(none)" ;
		}
	}

	public BaseControler getControler(int i)
	{
		if (i<arrDesc.size())
		{
			ControlerItemDescription desc = arrDesc.get(i) ;
			BaseControler ctrl = tabControlers.get(desc.csControlerName) ;
			return ctrl ;
		}
		return null ;
	}

	public ControlerThread getThread(int i)
	{
		if (i<arrDesc.size())
		{
			ControlerItemDescription desc = arrDesc.get(i) ;
			return tabThreads.get(desc.csControlerName) ;
		}
		return null ;
	}

	public void AddNewTask(BaseControlerTaskConfig grpConfig)
	{
		BaseControler ctrl = grpConfig.NewControler() ;
		AddControler(ctrl, grpConfig) ;
    	if (config.isAutoStart())
    	{
    		ControlerThread th = new ControlerThread(ctrl) ;
    		tabThreads.put(grpConfig.getName(), th) ;
    		th.AutoStart() ; 
    	}
	}

	public void RemoveTask(BaseControlerTaskConfig conf)
	{
		String name = conf.getName();
		if (tabControlers.containsKey(name))
		{
			for (int i=0; i<arrDesc.size(); )
			{
				ControlerItemDescription desc = arrDesc.get(i) ;
				if (desc.csControlerName.equals(name))
				{
					arrDesc.remove(i) ;
				}
				else
				{
					i++ ;
				}
			}
			ControlerThread th = tabThreads.remove(name) ;
			if (th != null)
			{
				th.StopControler(true) ;
				try {
				th.join() ;
				} catch (InterruptedException e) {} 
			}
			tabControlers.remove(name) ;
		}
		
	}

	public void RemoveStepFromTask(BaseControlerTaskConfig cfg)
	{
		if (!cfg.isModeGroup())
		{
			String name = cfg.getName() ;
			if (tabControlers.containsKey(name))
			{
				int iStep = cfg.getNbSteps() ;
				for (int i=0; i<arrDesc.size(); )
				{
					ControlerItemDescription desc = arrDesc.get(i) ;
					if (desc.csControlerName.equals(name) && desc.nStepId == iStep)
					{
						arrDesc.remove(i) ;
					}
					else
					{
						i++ ;
					}
				}
			}
		}
	}

	public void AddStepToTask(BaseControlerTaskConfig cfg)
	{
		if (!cfg.isModeGroup())
		{
			String name = cfg.getName() ;
			BaseControler ctrl = tabControlers.get(name) ;
			if (ctrl != null)
			{
				int iStep = cfg.getNbSteps()-1 ;
				ControlerItemDescription desc = new ControlerItemDescription() ;
				desc.csControlerName = name ;
				desc.nStepId = iStep ;

				for (int i=0; i<arrDesc.size(); i++)
				{
					ControlerItemDescription d = arrDesc.get(i) ;
					if (d.csControlerName.equals(name) && d.nStepId == iStep-1)
					{
						arrDesc.insertElementAt(desc, i+1) ;
					}
				}

			}
		}
	}
}
