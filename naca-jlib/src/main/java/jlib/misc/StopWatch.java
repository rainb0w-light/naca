/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/*
 * Created on 17 déc. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author PJD
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class StopWatch
{
	public StopWatch()
	{
		lStart = System.currentTimeMillis();
	}
	
	public long getElapsedTime()
	{
		long lStop = System.currentTimeMillis();
		return lStop - lStart;
	}

	public long getElapsedTimeReset()
	{
		long lStop = System.currentTimeMillis();
		long l = lStop - lStart;
		lStart = lStop;
		return l;		
	}
	
	public void Reset()
	{
		lStart = System.currentTimeMillis();
	}
	
	public boolean isTimeElapsed(long lTimeOut)
	{
		long lNow = System.currentTimeMillis();
		long lElapsed = lNow - lStart;
		if(lElapsed >= lTimeOut)
			return true;
		return false;
	}
	
	public long getStartValue()
	{
		return lStart;
	}
	
	private long lStart = 0;
}
