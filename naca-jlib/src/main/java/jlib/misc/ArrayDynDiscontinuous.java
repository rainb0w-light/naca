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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class ArrayDynDiscontinuous<T> extends ArrayFixDyn<T>
{
	Hashtable<Integer, T> arr = null;
	
	public int size()
	{
		if(arr != null)
		{
			int nMaxKey = -1;
			Set<Integer> keySet = arr.keySet();
			Iterator<Integer> keyIter = keySet.iterator();
			while(keyIter.hasNext())
			{
				Integer n = keyIter.next();
				if(n > nMaxKey)
					nMaxKey = n;
			}
			return nMaxKey+1;
		}
		return 0;
	}
	
	public T get(int n)
	{
		if(arr != null)
			return arr.get(n);
		return null;
	}
	
	public void add(T t)
	{
	}
	
	public void transferInto(T targetArr[])
	{
		if(arr != null)
		{
			Set<Integer> keySet = arr.keySet();
			if(keySet != null)
			{
				Iterator<Integer> keyIter = keySet.iterator();
				while(keyIter.hasNext())
				{
					Integer nKey = keyIter.next();
					T t = arr.get(nKey);
					targetArr[nKey] = t;			
				}
			}
		}
	}
	
	public boolean isDyn()
	{
		return true;
	}

	public void setSize(int n)
	{
	}

	public void set(int n, T t)
	{
		if(arr == null)
			arr = new Hashtable<Integer, T>();
		arr.put(n, t);
	}
}
