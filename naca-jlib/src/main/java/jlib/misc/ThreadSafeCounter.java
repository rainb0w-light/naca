/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.util.concurrent.atomic.AtomicInteger;

/** Provides thread safe counter behavior. */
public class ThreadSafeCounter
{
    /** Creates a new thread safe counter instance. */
    public ThreadSafeCounter()
    {
        nCount.set(0);
    }

    /** Creates a new thread safe counter instance. */
    public ThreadSafeCounter(int n)
    {
        nCount.set(n);
    }

    /** Executes the reset operation. */
    public int reset()
    {
        nCount.set(0);
        return 0;
    }

    /** Executes the inc operation. */
    public int inc(int n)
    {
        return nCount.addAndGet(n);
    }

    /** Executes the get operation. */
    public int get()
    {
        return nCount.get();
    }

    /** Executes the inc operation. */
    public int inc()
    {
        return nCount.incrementAndGet();
    }

    /** Executes the dec operation. */
    public int dec()
    {
        return nCount.decrementAndGet();
    }

    private AtomicInteger nCount = new AtomicInteger(0); // count starts at zero
}
