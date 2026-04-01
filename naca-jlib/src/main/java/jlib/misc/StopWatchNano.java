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
		start = System.nanoTime();
	}
	
	public long getElapsedTime()
	{
		long stop = System.nanoTime();
		return stop - start;
	}

	public long getElapsedTimeReset()
	{
		long stop = System.nanoTime();
		long l = stop - start;
		start = stop;
		return l;		
	}
	
	public void reset()
	{
		start = System.nanoTime();
	}
	
	public static long getMicroSecond(long l)
	{
		return l / 1000;
	}
	
	public static long getMilliSecond(long l)
	{
		return l / 1000000;
	}
	
	
	private long start = 0;
}
