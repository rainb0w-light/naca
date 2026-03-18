/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 24 juin 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jlib.log;

import java.util.ArrayList;

import jlib.xml.Tag;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CallStackExclusion
{
	CallStackExclusion()
	{
	}
	
	void fillExcluded(Tag tagSettings)
	{
		arrExclude = new ArrayList<String>();
		Tag tagCallLocation = tagSettings.getEnumChild("CallLocation");
		while(tagCallLocation != null)
		{
			String csExcludeName = tagCallLocation.getVal("Exclude");
			arrExclude.add(csExcludeName);
			tagCallLocation = tagSettings.getEnumChild("CallLocation");
		}				
	}
	
	boolean doNotContains(String csClassName)
	{
		if(arrExclude != null)
		{
			int nNbExclusion = arrExclude.size();
			for(int n=0; n<nNbExclusion; n++)
			{
				String csExclude = arrExclude.get(n);
				if(csClassName.startsWith(csExclude))
					return false;
			}
		}
		return true;
	}
	
	private ArrayList<String> arrExclude = null;
}
