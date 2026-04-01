/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.persitantQueue;
import jlib.threads.BaseThread;
import jlib.threads.Threadutil;
import jlib.xml.Tag;

public abstract class PersistentDequeueTagThread extends BaseThread
{
	private PersistantQueue persistantQueue = null;
	private int nLoopWait_ms = 0;

	protected PersistentDequeueTagThread(PersistantQueue persistantQueue, int nLoopWait_ms)
	{
		this.persistantQueue = persistantQueue;
		this.nLoopWait_ms = nLoopWait_ms;
	}

	public void run()
	{
		boolean iscontinue = true;
		while(iscontinue)
		{   
			Tag tagItem = persistantQueue.getFirstAsTag();
			if(tagItem == null)
			{
				iscontinue = Threadutil.wait(nLoopWait_ms);
			}
			else
			{
				iscontinue = handleObject(tagItem);
			}
		}
	}
	
	protected abstract boolean handleObject(Tag tagItem);
}
