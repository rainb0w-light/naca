/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

public class ThreadSafeCounterShort
{
	public ThreadSafeCounterShort()
	{
		sCount = 0;
	}
	
	public ThreadSafeCounterShort(short s)
	{
		sCount = s;
	}
	
	private short sCount = 0; // count starts at zero

	public synchronized int reset()
	{
		sCount = 0;
		return sCount; 
	}
	
	public synchronized short inc(short s)
	{ 
		sCount += s;
		return sCount; 
	}
	
	public synchronized short get()
	{
		return sCount;
	}
	
	public synchronized short inc()
	{
		sCount++;
		return sCount;
	}

	public synchronized short dec()
	{
		sCount--;
		return sCount;
	}
}
