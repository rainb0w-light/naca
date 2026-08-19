/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Provides simple thread pool behavior. */
public class SimpleThreadPool
{
    /** Creates a new simple thread pool instance. */
    public SimpleThreadPool(int nNbThread)
    {
        pool = Executors.newFixedThreadPool(nNbThread);
    }

    /** Executes the enqueue operation. */
    public void enqueue(Runnable runnable)
    {
        pool.execute(runnable);
    }

    /** Executes the request stop operation. */
    public void requestStop()
    {
        pool.shutdown();
    }

    private ExecutorService pool = null;
}
