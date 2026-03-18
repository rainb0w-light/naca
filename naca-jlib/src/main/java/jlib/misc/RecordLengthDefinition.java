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
 * @version $Id: RecordLengthDefinition.java,v 1.1 2006/08/11 06:38:42 u930di Exp $
 */
public class RecordLengthDefinition
{
	private int nRecordLength = 0;
	//private boolean bRecordLengthForced = false;
	
	public RecordLengthDefinition(int n)
	{
		nRecordLength = n;
	}
	
	public int getRecordLength()
	{
		return nRecordLength;
	}
	
	public String toString()
	{
		return String.valueOf(nRecordLength);
	}
}
