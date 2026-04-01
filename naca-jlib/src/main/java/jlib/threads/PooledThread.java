/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

public class PooledThread extends BaseThread
{
	protected PoolOfThreads owningPool = null;
	
	public PooledThread(PoolOfThreads owningPool)
	{
		owningPool = owningPool;
	}
	
	protected boolean canHandleRequest()
	{
		return false;	// return true if the thread object must handle it self the request; false if the request handles itself
	}
	
	protected void handleRequest(ThreadPoolRequest request)
	{
	}
	
	public void run()
	{
		try
		{
			boolean ishandleRequest = canHandleRequest();
			boolean iscanRun = preRun();
			while(iscanRun)
			{
				ThreadPoolRequest request = owningPool.dequeue();
				if(request != null)
				{
					if(!request.getTerminaisonRequest())		// Treat the request; the parameter pRequest describes the request to do
					{
						if(ishandleRequest)
							handleRequest(request);
						else
							request.execute();
					}
					else
					{
						owningPool.signalThreadTerminating();
						return;
					}
				}
			}
		}
		catch(Exception e)
		{
			owningPool.signalPooledThreadThrowException(e);
			owningPool.signalThreadTerminating(); // This thread is not avaible any more for the owner pool of thread.
		}
		postRun();
	}

	// These methods can overloaded in derivated classes
	public boolean preRun()
	{
		return true;
	}
	
	public void postRun()
	{
	}
}
