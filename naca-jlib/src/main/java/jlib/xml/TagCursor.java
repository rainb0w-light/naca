/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.xml;

/*
 * Created on 26 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TagCursor
{
	public TagCursor()
	{
	}
	
	void setCurrentTag(Tag tag)
	{
		this.tag = tag;
	}
	
	Tag getCurrentTag()
	{
		return tag;
	}
	
	void setInvalid()
	{
		tag = null;
	}
	
	public boolean isValid()
	{
		return tag != null; 
	}
	
	void setNameEnumeration(String csName)
	{
		this.csName = csName;
	}
	
	String getNameEnumeration()
	{
		return csName;
	}
	
	
	Tag tag = null;
	String csName = null;
	
}
