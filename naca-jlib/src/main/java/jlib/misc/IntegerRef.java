/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

public class IntegerRef
{
	public IntegerRef()
	{
	}
	
	public IntegerRef(int n)
	{
		n = n;
	}

	
	public int get()
	{
		return n;
	}
	
	public void set(int n)
	{
		n = n;
	}

	public void inc(int n)
	{
		n += n;
	}
	
	public void inc()
	{
		n++;
	}
	
	public void dec()
	{
		n--;
	}
	
	public String toString()
	{
		return "IntegerRef:" + n;
	}
	
	private int n = 0;
}
