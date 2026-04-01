/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class PoolOfThreads
{
	public PoolOfThreads(BasePooledThreadFactory pooledThreadFactory, int nNbThreads, int nNbMaxRequestAsyncSortPending)
	{
		isterminationRequested = false;
		
		queueRequests = new FixedSizeBlockingQueue<ThreadPoolRequest>(nNbMaxRequestAsyncSortPending);
		pooledThreads = new ArrayList<PooledThread>();
		addThreadSize(nNbThreads, pooledThreadFactory);
	}
	
	public void startAllThreads()
	{
		int nNbThreads = pooledThreads.size();
		for(int n=0; n<nNbThreads; n++)
		{
			PooledThread thread = pooledThreads.get(n);
			thread.start();
		}
	}
	
	public Exception stop()
	{
		join(); // Join to do for all CPooledThread
		return expThrownByPooledThread;
	}

	private void addThreadSize(int nNbThreadsToAdd, BasePooledThreadFactory pooledThreadFactory)
	{
		signalThreadsTerminated = new CountDownLatch(nNbThreadsToAdd);
		
		for(int nThread=0; nThread<nNbThreadsToAdd; nThread++)	// Create all required CPooledThread *
		{
			PooledThread pooledThread = pooledThreadFactory.make(this);
			pooledThreads.add(pooledThread);	// Add the pool in the vector
		}
	}
	
	/*!
	Enqueue
	\retval bool: return true if the enqueue has been correctly done; that is if a 
		 free thread has been instructed to process the request, or is all threads are 
		 occupied to put the request in the queue.
		 It returns false if Terminate() has been called (no more enqueing available)
	\param IN CRequest *pRequest: Request to be treated by one of the threads as soon 
		 as one will become available
	\note Enqueue a request and a type of request; it will be precced asynchronously 
		 by a thread allocated in the pool, as soon as one is available
	*/
	public boolean enqueue(ThreadPoolRequest request)
	{
		if(queueRequests != null)
		{
			queueRequests.enqueue(request); // Put the request in the queue
			return true;
		}
		return false;
	}
	
	/*!
	Dequeue
	\retval CRequest *
	\note Gets (can block) the first request pending
	*/
	public ThreadPoolRequest dequeue()
	{
		ThreadPoolRequest request = queueRequests.dequeue();
		return request;
	}
		
	/*!
	Terminate
	\retval void
	\note Reqests all threads to terminate their Run() method i.e. to shutdown their processing
		 As soon as this method is lauched, no more enqueing is possible; that is
		 Enqueue will return false.
	*/
	public void enqueueFinalRequests()
	{
		terminate();
	}
	
	private void terminate()
	{
		if (!isterminationRequested)
		{
			// Enqueue as much TerminaisonRequest requests as there are threads in the pool
			isterminationRequested = true ;
	
			int nNbThreads = pooledThreads.size();
			for(int nThread=0; nThread<nNbThreads; nThread++)
			{
				ThreadPoolRequest request = new ThreadPoolRequestTerminaison();
				queueRequests.enqueue(request);
			}
		}
	}
	
	/*!
	Join
	\retval void
	\note Wait until all threads in the pool have been destroyed
	*/
	private void join()
	{
		terminate();
		try
		{
			signalThreadsTerminated.await();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	void signalThreadTerminating()
	{
		signalThreadsTerminated.countDown();
	}
	
	void signalPooledThreadThrowException(Exception expThrownByPooledThread)
	{
		this.expThrownByPooledThread = expThrownByPooledThread;
	}
	
	private boolean isterminationRequested = false;
	private ArrayList<PooledThread> pooledThreads = null;
	private FixedSizeBlockingQueue<ThreadPoolRequest> queueRequests = null;
	private CountDownLatch signalThreadsTerminated = null;
	private Exception expThrownByPooledThread = null; // Exception thrown by a thread belonging to the pool
}
