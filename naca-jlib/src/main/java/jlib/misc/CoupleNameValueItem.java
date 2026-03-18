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
 * @version $Id: CoupleNameValueItem.java,v 1.1 2007/10/11 10:13:03 u930di Exp $
 */
public class CoupleNameValueItem
{
	private String csName = null;
	private String csValue = null;
	
	public CoupleNameValueItem(String csName, String csValue)
	{
		csName = csName; 
		csValue = csValue;
	}
	
	public String getName()
	{
		return csName;
	}
	
	public int getNameAsInt()
	{
		return NumberParser.getAsInt(csName);
	}
	
	public String getValue()
	{
		return csValue;
	}
}
