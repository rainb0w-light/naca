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
		count = 0;
	}
	
	public ThreadSafeCounterShort(short s)
	{
		count = s;
	}
	
	private short count = 0; // count starts at zero

	public synchronized int reset()
	{
		count = 0;
		return count;
	}
	
	public synchronized short inc(short s)
	{ 
		count += s;
		return count;
	}
	
	public synchronized short get()
	{
		return count;
	}
	
	public synchronized short inc()
	{
		count++;
		return count;
	}

	public synchronized short dec()
	{
		count--;
		return count;
	}
}
