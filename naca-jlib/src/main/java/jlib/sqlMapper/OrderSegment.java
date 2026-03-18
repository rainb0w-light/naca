/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sqlMapper;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: OrderSegment.java,v 1.1 2007/12/04 14:00:23 u930di Exp $
 */
public class OrderSegment
{
	private String csColName = null;
	boolean bAscending = true;
	
	protected OrderSegment(String csColName, boolean bAscending)
	{
		csColName = csColName;
		bAscending = bAscending;
	}
	
	String getAsString()
	{
		if(bAscending)
			return csColName;
		return csColName + " desc"; 
	}
}
