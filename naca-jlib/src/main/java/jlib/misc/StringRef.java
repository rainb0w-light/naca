/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

public class StringRef
{
	public StringRef()
	{
	}
	
	public StringRef(String cs)
	{
		cs = cs;
	}

	
	public String get()
	{
		return cs;
	}
	
	public void set(String cs)
	{
		cs = cs;
	}
	
	public String toString()
	{
		if(cs != null)
			return "StringRef: \""+cs + "\"";
		return "StringRef: \"<null>\"";
	}
	
	private String cs = null;
}
