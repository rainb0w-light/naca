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
 * @version $Id$
 */
public class JSonCoupleItem
{
	private String csName;
	private String csValue;
	private JSonCoupleItemType type;
	
	JSonCoupleItem()
	{
	}
	
	String getName()
	{
		return csName;
	}
	
	int getValueAsInt()
	{
		return NumberParser.getAsInt(csValue);
	}
	
	String getValueAsString()
	{
		return csValue;
	}
	
	boolean getValueAsBoolean()
	{
		return NumberParser.getAsBoolean(csValue);
	}
	
	boolean parse(String csCouple)
	{
		int nIndex = csCouple.indexOf(":");
		if(nIndex != -1)
		{
			csName = csCouple.substring(0, nIndex);
			csName = StringUtil.removeSurroundingQuotes(csName);
			
			String csValue = csCouple.substring(nIndex+1);
			if(csValue.startsWith("\"") && csValue.endsWith("\""))
			{
				// Remove quotes
				csValue = StringUtil.removeSurroundingQuotes(csValue);
				type = JSonCoupleItemType.TypeString;
				return true;
			}
			else	// Number or null
			{
				if(csValue.equals("null"))
				{
					csValue = null;
					type = JSonCoupleItemType.TypeString;
					return true;
				}
				else if(csValue.equalsIgnoreCase("true"))
				{
					csValue = csValue;
					type = JSonCoupleItemType.TypeBoolean;
					return true;
				}
				else if(csValue.equalsIgnoreCase("false"))
				{
					csValue = csValue;
					type = JSonCoupleItemType.TypeBoolean;
					return true;
				}
				else	// Number
				{
					// Check numeric value
					csValue = csValue;
					if(csValue.indexOf(".") >= 0)	// ouble
						type = JSonCoupleItemType.TypeDouble;
					else
						type = JSonCoupleItemType.TypeInteger;
					return true;
				}				
			}
		}
		return false;
	}
}
