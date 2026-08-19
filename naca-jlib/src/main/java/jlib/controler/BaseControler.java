/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.controler;

import java.util.Date;
import java.util.Vector;

/** Provides base controler behavior. */
public abstract class BaseControler
{
    /** Creates a new base controler instance. */
    public BaseControler(int nbSteps)
    {
        status = new Vector<String>(nbSteps) ;
        datestarts = new Vector<Date>(nbSteps)  ;
        dateends = new Vector<Date>(nbSteps)  ;
        for (int i=0; i<nbSteps; i++)
        {
            status.add("NONE") ;
            dateends.add(null) ;
            datestarts.add(null) ;
        }
    }
    private Vector<String> status;
    private Vector<Date> datestarts;
    private Vector<Date> dateends;

    private boolean isisRunning = false ;
    private int nCurrentStep = 0 ;

    /** Returns the status. */
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

    /** Returns the task config. */
    public abstract BaseControlerTaskConfig getTaskConfig() ;

    /** Sets the status. */
    public void setStatus(int currentSite, String string)
    {
        status.set(currentSite, string) ;
    }
    /** Sets the start date. */
    public void setStartDate(int currentSite, Date dt)
    {
        datestarts.set(currentSite, dt) ;
    }

    /** Executes the run step operation. */
    public boolean RunStep(int currentSite)
    {
        isisRunning = true ;
        nCurrentStep = currentSite ;
        BaseControlerTaskConfig conf = getTaskConfig() ;
        BaseControlerStepConfig step = conf.getStep(nCurrentStep) ;
//      step.setCurrentControler(this) ;

        boolean b = DoOneStep(currentSite) ;

        isisRunning = false ;
//      step.setCurrentControler(null) ;
        return b ;
    }

    protected abstract boolean DoOneStep(int currentSite) ;

    /** Executes the stop operation. */
    public abstract void Stop(boolean force) ;

    public Date getDateGroupEnds()
    {
        return dategroupEnds;
    }
    /** Sets the date group ends. */
    public void setDateGroupEnds()
    {
        dategroupEnds = new Date() ;
    }
    private Date dategroupEnds = null ;

    /** Returns the date step ends. */
    public Date getDateStepEnds(int currentSite)
    {
        return dateends.get(currentSite) ;
    }

    /** Returns the step name. */
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
