/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleThreadPool
{
	public SimpleThreadPool(int nNbThread)
	{
		pool = Executors.newFixedThreadPool(nNbThread);
	}
	
	public void enqueue(Runnable runnable)
	{
		pool.execute(runnable);
	}
	
	public void requestStop()
	{
		pool.shutdown();
	}
	
	private ExecutorService pool = null;
}
