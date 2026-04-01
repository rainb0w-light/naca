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
	
	private boolean issuccess;
	private boolean isduplicates;
	
	private SQLLoadStatus(boolean issuccess, boolean isduplicates)
	{
		issuccess = issuccess;
		isduplicates = isduplicates;
	}
	
	public boolean isSuccess()
	{
		return issuccess;
	}
	
	public boolean hadDuplicates()
	{
		return isduplicates;
	}
	
	public static SQLLoadStatus updateWithLocalStatus(SQLLoadStatus globalStatus, SQLLoadStatus status)
	{
		if(!status.issuccess)
			return loadFailure;
		if(globalStatus.isduplicates || status.isduplicates)
			return loadSuccessWithDuplicates;
		return loadSuccess;			
	}
	
	public String toString()
	{
		String cs;
		if(issuccess)
			cs = "Success";
		else
			cs = "Failure";
		
		if(isduplicates)
			cs += " with duplicate keys";
		else
			cs += " without duplicate keys";
		return cs;
	}
}
