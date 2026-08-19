/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

/** Provides pooled thread behavior. */
public class PooledThread extends BaseThread
{
    protected PoolOfThreads owningPool = null;

    /** Creates a new pooled thread instance. */
    public PooledThread(PoolOfThreads owningPool)
    {
        this.owningPool = owningPool;
    }

    protected boolean canHandleRequest()
    {
        return false;   // return true if the thread object must handle it self the request; false if the request handles itself
    }

    protected void handleRequest(ThreadPoolRequest request)
    {
    }

    /** Runs this operation. */
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
                    // Treat the request; the parameter pRequest describes the request to do
                    if(!request.getTerminaisonRequest())
                    {
                        if (ishandleRequest) {
                            handleRequest(request);
                        } else {
                            request.execute();
                        }
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
    /** Executes the pre run operation. */
    public boolean preRun()
    {
        return true;
    }

    /** Executes the post run operation. */
    public void postRun()
    {
    }
}
