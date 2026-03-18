/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

public class StopWatchNano
{
	public StopWatchNano()
	{
		lStart = System.nanoTime();
	}
	
	public long getElapsedTime()
	{
		long lStop = System.nanoTime();
		return lStop - lStart;
	}

	public long getElapsedTimeReset()
	{
		long lStop = System.nanoTime();
		long l = lStop - lStart;
		lStart = lStop;
		return l;		
	}
	
	public void reset()
	{
		lStart = System.nanoTime();
	}
	
	public static long getMicroSecond(long l)
	{
		return l / 1000;
	}
	
	public static long getMilliSecond(long l)
	{
		return l / 1000000;
	}
	
	
	private long lStart = 0;
}
