/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilterByPrefix implements FilenameFilter
{
	private String csPrefix = null;
	
	public FileFilterByPrefix(String csPrefix)
	{
		csPrefix = csPrefix.toUpperCase();
	}
	
	public boolean accept(File dir, String csName)
	{
		if(csPrefix != null && csName != null)
		{
			String cs = csName.toUpperCase();
			if(cs.startsWith(csPrefix))
				return true;
		}
		return false;
	}
}
