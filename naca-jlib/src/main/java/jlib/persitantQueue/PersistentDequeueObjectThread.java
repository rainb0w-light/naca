/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.persitantQueue;
import jlib.log.Log;
import jlib.threads.BaseThread;
import jlib.threads.Threadutil;

public abstract class PersistentDequeueObjectThread extends BaseThread
{
	private PersistantQueue persistantQueue = null;
	private int nLoopWait_ms = 0;
	private BaseQueueItemFactory baseQueueItemFactory = null;

	protected PersistentDequeueObjectThread(PersistantQueue persistantQueue, BaseQueueItemFactory baseQueueItemFactory, int nLoopWait_ms)
	{
		this.baseQueueItemFactory = baseQueueItemFactory;
		this.persistantQueue = persistantQueue;
		this.nLoopWait_ms = nLoopWait_ms;
	}

	public void run()
	{
		boolean iscontinue = true;
		while(iscontinue)
		{   
			Object object = persistantQueue.getFirst(baseQueueItemFactory);
			if(object == null)
			{
				iscontinue = Threadutil.wait(nLoopWait_ms);
			}
			else
			{
				try
				{
					iscontinue = handleObject(object);
				}
				catch (Exception e)
				{
					Log.logCritical("Exception catched in handleObjet of PersistentDequeueObjectThread::run(): "+e.toString()); 
				}
			}
		}
	}
	
	protected abstract boolean handleObject(Object object);
}
