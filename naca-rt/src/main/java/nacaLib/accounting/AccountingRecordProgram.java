/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.accounting;

import java.util.Date;


import jlib.misc.StopWatchNano;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: AccountingRecordProgram.java,v 1.4 2006/06/13 11:18:21 cvsadmin Exp $
 */
public class AccountingRecordProgram
{
    /** Creates a new accounting record program instance. */
    public AccountingRecordProgram()
    {
        dateStart = new Date();
        stopWatchNano.reset();
    }

    /** Executes the begin run program operation. */
    public void beginRunProgram(String csProgramName)
    {
        this.csProgramName = csProgramName;
    }

    /** Executes the end run program operation. */
    public void endRunProgram(CriteriaEndRunMain criteria)
    {
        runTimeMillis = (int)StopWatchNano.getMilliSecond(stopWatchNano.getElapsedTime());
        csCriteriaEnd = criteria.getName();
    }

    int getRunTime_ms()
    {
        return runTimeMillis;
    }

    long getRunTimeIO_ns()
    {
        return runTimeIONanos;
    }

    long getTimeDateStart()
    {
        if (dateStart != null) {
            return dateStart.getTime();
        }
        return 0;
    }

    void reportDBIOTime(long lDBIOTimeNs)
    {
        runTimeIONanos += lDBIOTimeNs;
    }

    String getProgramName()
    {
        return csProgramName;
    }

    String getCriteriaEnd()
    {
        return csCriteriaEnd;
    }

    private Date dateStart = null;
    private String csProgramName = "";
    private int runTimeMillis = 0;
    private long runTimeIONanos = 0;

    private String csCriteriaEnd = "";
    private StopWatchNano stopWatchNano = new StopWatchNano();
}
