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
	public AccountingRecordProgram()
	{
		dateStart = new Date();
		stopWatchNano.reset();
	}
	
	public void beginRunProgram(String csProgramName)
	{
		this.csProgramName = csProgramName;
	}
	
	public void endRunProgram(CriteriaEndRunMain criteria)
	{
		nRunTime_ms = (int)StopWatchNano.getMilliSecond(stopWatchNano.getElapsedTime());
		csCriteriaEnd = criteria.getName();
	}
	
	int getRunTime_ms()
	{
		return nRunTime_ms;
	}
	
	long getRunTimeIO_ns()
	{
		return nRunTimeIO_ns;
	}
	
	long getTimeDateStart()
	{
		if(dateStart != null)
			return dateStart.getTime();
		return 0;
	}
	
	void reportDBIOTime(long lDBIOTime_ns)
	{
		nRunTimeIO_ns += lDBIOTime_ns;
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
	private int nRunTime_ms = 0;
	private long nRunTimeIO_ns = 0;

	private String csCriteriaEnd = "";
	private StopWatchNano stopWatchNano = new StopWatchNano();	
}
