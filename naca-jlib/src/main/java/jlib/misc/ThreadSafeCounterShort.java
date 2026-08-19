/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/** Provides thread safe counter short behavior. */
public class ThreadSafeCounterShort
{
    /** Creates a new thread safe counter short instance. */
    public ThreadSafeCounterShort()
    {
        count = 0;
    }

    /** Creates a new thread safe counter short instance. */
    public ThreadSafeCounterShort(short s)
    {
        count = s;
    }

    private short count = 0; // count starts at zero

    /** Executes the reset operation. */
    public synchronized int reset()
    {
        count = 0;
        return count;
    }

    /** Executes the inc operation. */
    public synchronized short inc(short s)
    {
        count += s;
        return count;
    }

    /** Executes the get operation. */
    public synchronized short get()
    {
        return count;
    }

    /** Executes the inc operation. */
    public synchronized short inc()
    {
        count++;
        return count;
    }

    /** Executes the dec operation. */
    public synchronized short dec()
    {
        count--;
        return count;
    }
}
