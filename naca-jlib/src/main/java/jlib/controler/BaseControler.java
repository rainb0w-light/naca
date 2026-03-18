/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.controler;

import java.util.Date;
import java.util.Vector;

public abstract class BaseControler
{
	public BaseControler(int nbSteps)
	{
		arrStatus = new Vector<String>(nbSteps) ;
		arrDtStarts = new Vector<Date>(nbSteps)  ;
		arrDtEnds = new Vector<Date>(nbSteps)  ;
		for (int i=0; i<nbSteps; i++)
		{
			arrStatus.add("NONE") ;
			arrDtEnds.add(null) ;
			arrDtStarts.add(null) ;
		}
	}
	private Vector<String> arrStatus; 
	private Vector<Date> arrDtStarts ;
	private Vector<Date> arrDtEnds ;
	
	private boolean bIsRunning = false ;
	private int nCurrentStep = 0 ;
	
	public String getStatus(int stepId)
	{
		if (stepId >= arrStatus.size())
		{
			return "NONE" ;
		}
		String status = arrStatus.get(stepId) ;
		if (getTaskConfig().isModeGroup() || stepId == nCurrentStep)
		{
			if (status.startsWith("NONE") || status.startsWith("ERROR") || status.startsWith("STARTING"))
			{
				return status ;
			}
			else
			{
				return status + " ; " + getCurrentInternalStatus() ;
			}
		}
		else
		{
			return status ;
		}
	};

	protected abstract String getCurrentInternalStatus() ;
	
	public abstract BaseControlerTaskConfig getTaskConfig() ;

	public void setStatus(int currentSite, String string)
	{
		arrStatus.set(currentSite, string) ;
	}
	public void setStartDate(int currentSite, Date dt)
	{
		arrDtStarts.set(currentSite, dt) ;
	}

	public boolean RunStep(int currentSite)
	{
		bIsRunning = true ;
		nCurrentStep = currentSite ;
		BaseControlerTaskConfig conf = getTaskConfig() ;
		BaseControlerStepConfig step = conf.getStep(nCurrentStep) ;
//		step.setCurrentControler(this) ;
		
		boolean b = DoOneStep(currentSite) ;
		
		bIsRunning = false ;
//		step.setCurrentControler(null) ;
		return b ;
	}
	
	protected abstract boolean DoOneStep(int currentSite) ;

	public abstract void Stop(boolean force) ;

	public Date getDateGroupEnds()
	{
		return dtGroupEnds;
	}
	public void setDateGroupEnds()
	{
		dtGroupEnds = new Date() ;
	}
	private Date dtGroupEnds = null ;

	public Date getDateStepEnds(int currentSite)
	{
		return arrDtEnds.get(currentSite) ;
	}

	public String getStepName(int stepId)
	{
		return getTaskConfig().getStep(stepId).getName() ;
	}
	
	protected boolean isRunning()
	{
		return bIsRunning ;
	}
	protected int getCurrentStep()
	{
		return nCurrentStep ;
	}

}
