/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/**
 * @author PJD
 *
 */

public class StopWatch
{
    /** Creates a new stop watch instance. */
    public StopWatch()
    {
        start = System.currentTimeMillis();
    }

    /** Returns the elapsed time. */
    public long getElapsedTime()
    {
        long stop = System.currentTimeMillis();
        return stop - start;
    }

    /** Returns the elapsed time reset. */
    public long getElapsedTimeReset()
    {
        long stop = System.currentTimeMillis();
        long l = stop - start;
        start = stop;
        return l;
    }

    /** Executes the reset operation. */
    public void Reset()
    {
        start = System.currentTimeMillis();
    }

    /** Returns whether time elapsed. */
    public boolean isTimeElapsed(long lTimeOut)
    {
        long now = System.currentTimeMillis();
        long elapsed = now - start;
        if (elapsed >= lTimeOut) {
            return true;
        }
        return false;
    }

    public long getStartValue()
    {
        return start;
    }

    private long start = 0;
}
