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
public class ArrayFix<T> extends ArrayFixDyn<T>
{
    T arr[] = null;

    /** Creates a new array fix instance. */
    public ArrayFix(T arr[])
    {
        this.arr = arr;
    }

    /** Executes the size operation. */
    public int size()
    {
        if (arr != null) {
            return arr.length;
        }
        return 0;
    }

    /** Executes the get operation. */
    public T get(int n)
    {
        //if(arr != null)
            return arr[n];
        //return null;
    }

    /** Executes the add operation. */
    public void add(T t)
    {
        //assertIfFalse(false);
    }

//  public T[] getAsArray()
//  {
//      return arr;
//  }


    /** Executes the transfer into operation. */
    public void transferInto(T[] arr)
    {
        this.arr = arr;
    }

    public boolean isDyn()
    {
        return false;
    }

    /** Sets the size. */
    public void setSize(int n)
    {
    }

    /** Executes the set operation. */
    public void set(int n, T t)
    {
        arr[n] = t;
    }
}
