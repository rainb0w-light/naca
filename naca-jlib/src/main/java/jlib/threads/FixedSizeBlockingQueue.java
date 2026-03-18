/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.threads;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class FixedSizeBlockingQueue<T>
{
	public FixedSizeBlockingQueue(int nNbEntries)
	{
		nNbEntries = nNbEntries;
		arr = new Object[nNbEntries];
		semFilledEntries = new Semaphore(0, true);
		semNotFilledEntries = new Semaphore(nNbEntries, true);
	}
	
	public void enqueue(T t)
	{
		try
		{
			semNotFilledEntries.acquire();
		}
		catch (InterruptedException e)
		{
			return;
		}

		synchronized(arr)
		{
			arr[nIndexSet] = t;
			nIndexSet++;
			if(nIndexSet >= nNbEntries)
				nIndexSet  = 0;
		}
		semFilledEntries.release();
	}
	
	public T dequeue()
	{		
		try
		{
			semFilledEntries.acquire();
		}
		catch (InterruptedException e)
		{
			return null;
		}

		synchronized(arr)
		{
			T t = (T)arr[nIndexGet];
			nIndexGet++;
			if(nIndexGet >= nNbEntries)
				nIndexGet = 0;
			semNotFilledEntries.release();
			return t;
		}		
	}
	
	private int nNbEntries = 0;
	private Object arr[] = null;
	private int nIndexSet = 0;
	private int nIndexGet = 0;
	private Semaphore semFilledEntries = null;	// Semaphore counting the number of filled entries
	private Semaphore semNotFilledEntries = null;	// Semaphore counting the number of entries that can still be filled (that are not filled yet)
}
