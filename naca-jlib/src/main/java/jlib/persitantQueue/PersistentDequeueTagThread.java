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

/** Provides persistent dequeue tag thread behavior. */
public abstract class PersistentDequeueTagThread extends BaseThread
{
    private PersistantQueue persistantQueue = null;
    private int loopWaitMillis = 0;

    protected PersistentDequeueTagThread(PersistantQueue persistantQueue, int nLoopWaitMs)
    {
        this.persistantQueue = persistantQueue;
        this.loopWaitMillis = nLoopWaitMs;
    }

    /** Runs this operation. */
    public void run()
    {
        boolean iscontinue = true;
        while(iscontinue)
        {
            Tag tagItem = persistantQueue.getFirstAsTag();
            if(tagItem == null)
            {
                iscontinue = Threadutil.wait(loopWaitMillis);
            }
            else
            {
                iscontinue = handleObject(tagItem);
            }
        }
    }

    protected abstract boolean handleObject(Tag tagItem);
}
