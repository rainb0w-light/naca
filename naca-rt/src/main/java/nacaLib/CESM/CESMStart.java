/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930CV
 *
 */
package nacaLib.CESM;

import jlib.misc.DateUtil;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.Var;

/** Provides cesmstart behavior. */
public class CESMStart extends CJMapObject
{
    /** Creates a new cesmstart instance. */
    public CESMStart(String cs, BaseEnvironment env)
    {
        csTransID = cs ;
        environment = env;
    }
    private BaseEnvironment environment = null ;
    public String csTransID = "" ;
    public String csTermID = "" ;
    private CESMStartData data = null;

    /** Executes the term id operation. */
    public CESMStart termID(String string)
    {
        csTermID = string ;
        return this ;
    }
    /** Executes the term id operation. */
    public CESMStart termID(Var termID)
    {
        return termID(termID.getString()) ;
    }
    /** Executes the sys id operation. */
    public CESMStart sysID(String sysID)
    {
        // Remote-system routing is not implemented by this local runtime.
        return this ;
    }
    /** Executes the sys id operation. */
    public CESMStart sysID(Var sysID)
    {
        return sysID(sysID.getString()) ;
    }
    /** Executes the do start operation. */
    public void doStart()
    {
        if (!csTermID.equals(""))
        {
            assertIfFalse(environment.getTerminalID().equals(csTermID)) ;
            environment.enqueueProgram(csTransID, data) ;
        }
        else
        {
            TempCache t = TempCacheLocator.getTLSTempCache();
            String csCurrentProgram = t.getProgramManager().getProgramName();
            environment.StartAsynchronousProgram(csTransID, csCurrentProgram, data, nIntervalTimeSeconds);
        }
    }

    /** Executes the data from operation. */
    public CESMStart dataFrom(Var var, Var varLength)
    {
        data = new CESMStartData(var, varLength);
        return this ;
    }

    /** Executes the data from operation. */
    public CESMStart dataFrom(Var var)
    {
        data = new CESMStartData(var, null);
        return this ;
    }

    /**
     * @param transTime
     * @return
     */
    public CESMStart time(Var transTime)
    {
        // trans_Time uses format HHMMSS
        int nNbSecondsSinceMidnightFromNowS = DateUtil.getNbSecondSinceMidnight();
        int nNextTimeS = DateUtil.getNbSecondsFromHour(transTime.getInt());
        if (nNbSecondsSinceMidnightFromNowS < nNextTimeS) { // We are before next time
            nIntervalTimeSeconds = nNextTimeS - nNbSecondsSinceMidnightFromNowS;
        } else {
            nIntervalTimeSeconds = 0;
        }
        return this;
    }
    /**
     * @param interval
     * @return
     */
    public CESMStart interval(Var interval)
    {
        // interval uses format HHMMSS
        nIntervalTimeSeconds = DateUtil.getNbSecondsFromHour(interval.getInt());
        return this;
    }
    protected int nIntervalTimeSeconds = 0 ;
}
