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

import java.util.ArrayList;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class ArrayDyn<T> extends ArrayFixDyn<T>
{
    ArrayList<T> arr = null;

//  public T[] getAsArray()
//  {
//      return (T[])arr.toArray();
//  }

    /** Executes the size operation. */
    public int size()
    {
        if (arr != null) {
            return arr.size();
        }
        return 0;
    }

    /** Executes the get operation. */
    public T get(int n)
    {
        if (arr != null) {
            return arr.get(n);
        }
        return null;
    }

    /** Executes the add operation. */
    public void add(T t)
    {
        if (arr == null) {
            arr = new ArrayList<T>();
        }
        arr.add(t);
    }

    /** Executes the transfer into operation. */
    public void transferInto(T targetArr[])
    {
        int nSize = size();
        for(int n=0; n<nSize; n++)
        {
            T t = arr.get(n);
            targetArr[n] = t;
        }
    }

    public boolean isDyn()
    {
        return true;
    }

    /** Sets the size. */
    public void setSize(int n)
    {
    }

    /** Executes the set operation. */
    public void set(int n, T t)
    {
        arr.set(n, t);
    }
}
