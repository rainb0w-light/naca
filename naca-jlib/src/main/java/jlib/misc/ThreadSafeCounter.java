/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCounter
{
	public ThreadSafeCounter()
	{
		nCount.set(0);
	}
	
	public ThreadSafeCounter(int n)
	{
		nCount.set(n);
	}
	
	public int reset()
	{
		nCount.set(0);
		return 0; 
	}
	
	public int inc(int n)
	{ 
		return nCount.addAndGet(n);
	}
	
	public int get()
	{
		return nCount.get();
	}
	
	public int inc()
	{
		return nCount.incrementAndGet();
	}

	public int dec()
	{
		return nCount.decrementAndGet();
	}
	
	private AtomicInteger nCount = new AtomicInteger(0); // count starts at zero
}
