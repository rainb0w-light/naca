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
		status = new Vector<String>(nbSteps) ;
		dtStarts = new Vector<Date>(nbSteps)  ;
		dtEnds = new Vector<Date>(nbSteps)  ;
		for (int i=0; i<nbSteps; i++)
		{
			status.add("NONE") ;
			dtEnds.add(null) ;
			dtStarts.add(null) ;
		}
	}
	private Vector<String> status;
	private Vector<Date> dtStarts;
	private Vector<Date> dtEnds;
	
	private boolean isisRunning = false ;
	private int nCurrentStep = 0 ;
	
	public String getStatus(int stepId)
	{
		if (stepId >= status.size())
		{
			return "NONE" ;
		}
		String status = this.status.get(stepId) ;
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
		status.set(currentSite, string) ;
	}
	public void setStartDate(int currentSite, Date dt)
	{
		dtStarts.set(currentSite, dt) ;
	}

	public boolean RunStep(int currentSite)
	{
		isisRunning = true ;
		nCurrentStep = currentSite ;
		BaseControlerTaskConfig conf = getTaskConfig() ;
		BaseControlerStepConfig step = conf.getStep(nCurrentStep) ;
//		step.setCurrentControler(this) ;
		
		boolean b = DoOneStep(currentSite) ;
		
		isisRunning = false ;
//		step.setCurrentControler(null) ;
		return b ;
	}
	
	protected abstract boolean DoOneStep(int currentSite) ;

	public abstract void Stop(boolean force) ;

	public Date getDateGroupEnds()
	{
		return dategroupEnds;
	}
	public void setDateGroupEnds()
	{
		dategroupEnds = new Date() ;
	}
	private Date dategroupEnds = null ;

	public Date getDateStepEnds(int currentSite)
	{
		return dtEnds.get(currentSite) ;
	}

	public String getStepName(int stepId)
	{
		return getTaskConfig().getStep(stepId).getName() ;
	}
	
	protected boolean isRunning()
	{
		return isisRunning;
	}
	protected int getCurrentStep()
	{
		return nCurrentStep ;
	}

}
