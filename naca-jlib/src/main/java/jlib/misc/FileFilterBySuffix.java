/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilterBySuffix implements FilenameFilter
{
	private String csSuffix = null;
	
	public FileFilterBySuffix(String csSuffix)
	{
		csSuffix = csSuffix.toUpperCase();
	}
	
	public boolean accept(File dir, String csName)
	{
		if(csSuffix != null && csName != null)
		{
			String cs = csName.toUpperCase();
			if(cs.endsWith(csSuffix))
				return true;
		}
		return false;
	}
}
