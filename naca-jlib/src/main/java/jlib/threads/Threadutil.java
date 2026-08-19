/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

/** Provides threadutil behavior. */
public class Threadutil
{
    /** Executes the wait operation. */
    public static boolean wait(int nNbms)
    {
        try
        {
            Thread.sleep(nNbms);
            return true;
        }
        catch (InterruptedException e)
        {
        }
        return false;
    }

    /** Returns the current thread id. */
    public static long getCurrentThreadId()
    {
        Thread thread = Thread.currentThread();
        long l = thread.getId();
        return l;
    }
}
