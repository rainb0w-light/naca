/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/** Provides stop watch nano behavior. */
public class StopWatchNano
{
    /** Creates a new stop watch nano instance. */
    public StopWatchNano()
    {
        start = System.nanoTime();
    }

    /** Returns the elapsed time. */
    public long getElapsedTime()
    {
        long stop = System.nanoTime();
        return stop - start;
    }

    /** Returns the elapsed time reset. */
    public long getElapsedTimeReset()
    {
        long stop = System.nanoTime();
        long l = stop - start;
        start = stop;
        return l;
    }

    /** Executes the reset operation. */
    public void reset()
    {
        start = System.nanoTime();
    }

    /** Returns the micro second. */
    public static long getMicroSecond(long l)
    {
        return l / 1000;
    }

    /** Returns the milli second. */
    public static long getMilliSecond(long l)
    {
        return l / 1000000;
    }


    private long start = 0;
}
