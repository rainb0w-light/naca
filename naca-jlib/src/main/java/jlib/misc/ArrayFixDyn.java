/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package jlib.misc;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public abstract class ArrayFixDyn<T>
{
    //public abstract T[] getAsArray();
    /** Executes the size operation. */
    public abstract int size();
    /** Executes the get operation. */
    public abstract T get(int n);
    /** Executes the add operation. */
    public abstract void add(T t);
    /** Executes the transfer into operation. */
    public abstract void transferInto(T arr[]);
    /** Returns whether dyn. */
    public abstract boolean isDyn();

    /** Sets the size. */
    public abstract void setSize(int n);
    /** Executes the set operation. */
    public abstract void set(int n, T t);
}
