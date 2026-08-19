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

/** Provides persistent dequeue object thread behavior. */
public abstract class PersistentDequeueObjectThread extends BaseThread
{
    private PersistantQueue persistantQueue = null;
    private int loopWaitMillis = 0;
    private BaseQueueItemFactory baseQueueItemFactory = null;

    protected PersistentDequeueObjectThread(PersistantQueue persistantQueue, BaseQueueItemFactory baseQueueItemFactory, int nLoopWaitMs)
    {
        this.baseQueueItemFactory = baseQueueItemFactory;
        this.persistantQueue = persistantQueue;
        this.loopWaitMillis = nLoopWaitMs;
    }

    /** Runs this operation. */
    public void run()
    {
        boolean iscontinue = true;
        while(iscontinue)
        {
            Object object = persistantQueue.getFirst(baseQueueItemFactory);
            if(object == null)
            {
                iscontinue = Threadutil.wait(loopWaitMillis);
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
