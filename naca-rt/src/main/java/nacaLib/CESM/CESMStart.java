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
import nacaLib.base.*;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.Var;

public class CESMStart extends CJMapObject
{
    public CESMStart(String cs, BaseEnvironment env)
    {
        csTransID = cs ;
        environment = env;
    }
    private BaseEnvironment environment = null ;
    public String csTransID = "" ;
    public String csTermID = "" ;
    private CESMStartData data = null;

    public CESMStart termID(String string)
    {
        csTermID = string ;
        return this ;
    }
    public CESMStart termID(Var termID)
    {
        return termID(termID.getString()) ;
    }
    public CESMStart sysID(String sysID)
    {
        // Remote-system routing is not implemented by this local runtime.
        return this ;
    }
    public CESMStart sysID(Var sysID)
    {
        return sysID(sysID.getString()) ;
    }
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

    public CESMStart dataFrom(Var var, Var varLength)
    {
        data = new CESMStartData(var, varLength);
        return this ;
    }

    public CESMStart dataFrom(Var var)
    {
        data = new CESMStartData(var, null);
        return this ;
    }

    /**
     * @param trans_Time
     * @return
     */
    public CESMStart time(Var trans_Time)
    {
        // trans_Time uses format HHMMSS
        int nNbSecondsSinceMidnightFromNow_s = DateUtil.getNbSecondSinceMidnight();
        int nNextTime_s = DateUtil.getNbSecondsFromHour(trans_Time.getInt());
        if (nNbSecondsSinceMidnightFromNow_s < nNextTime_s) { // We are before next time
            nIntervalTimeSeconds = nNextTime_s - nNbSecondsSinceMidnightFromNow_s;
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
