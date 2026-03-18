/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sql;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class SQLLoadStatus
{
	public static final SQLLoadStatus loadSuccess = new SQLLoadStatus(true, false);
	public static final SQLLoadStatus loadFailure = new SQLLoadStatus(false, false);
	public static final SQLLoadStatus loadSuccessWithDuplicates = new SQLLoadStatus(true, true);
	
	private boolean bSuccess;
	private boolean bDuplicates;
	
	private SQLLoadStatus(boolean bSuccess, boolean bDuplicates)
	{
		bSuccess = bSuccess;
		bDuplicates = bDuplicates;
	}
	
	public boolean isSuccess()
	{
		return bSuccess;
	}
	
	public boolean hadDuplicates()
	{
		return bDuplicates;
	}
	
	public static SQLLoadStatus updateWithLocalStatus(SQLLoadStatus globalStatus, SQLLoadStatus status)
	{
		if(!status.bSuccess)
			return loadFailure;
		if(globalStatus.bDuplicates || status.bDuplicates)
			return loadSuccessWithDuplicates;
		return loadSuccess;			
	}
	
	public String toString()
	{
		String cs;
		if(bSuccess)
			cs = "Success";
		else
			cs = "Failure";
		
		if(bDuplicates)
			cs += " with duplicate keys";
		else
			cs += " without duplicate keys";
		return cs;
	}
}
