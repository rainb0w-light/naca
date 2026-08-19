/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/** Provides integer ref behavior. */
public class IntegerRef
{
    /** Creates a new integer ref instance. */
    public IntegerRef()
    {
    }

    /** Creates a new integer ref instance. */
    public IntegerRef(int n)
    {
        this.n = n;
    }


    /** Executes the get operation. */
    public int get()
    {
        return n;
    }

    /** Executes the set operation. */
    public void set(int n)
    {
        this.n = n;
    }

    /** Executes the inc operation. */
    public void inc(int n)
    {
        n += n;
    }

    /** Executes the inc operation. */
    public void inc()
    {
        n++;
    }

    /** Executes the dec operation. */
    public void dec()
    {
        n--;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return "IntegerRef:" + n;
    }

    private int n = 0;
}
