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

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LogInfoMember
{	
	LogInfoMember(String csName, String csValue)
	{
		csName = csName;
		csValue = csValue;
	}
	
	LogInfoMember(String csName, int nValue)
	{
		csName = csName;
		value = Integer.valueOf(nValue);
	}
	
	String getAsString()
	{
		if(csValue != null)
			return csName + "=" + csValue;
		if(value != null)
			return csName + "=" + value.toString();
		return csName + "=?";
	}
	
	String getName()
	{
		return csName; 
	}
	
	String getValue()
	{
		if(csValue != null)
			return csValue;
		else if(value != null)
			return value.toString();
		return "";
	}

	
	String csName = null;
	String csValue = null;
	Integer value = null;
}
