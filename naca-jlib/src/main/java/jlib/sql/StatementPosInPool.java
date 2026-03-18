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
 * @version $Id: StatementPosInPool.java,v 1.4 2006/05/01 12:36:20 cvsadmin Exp $
 */
public class StatementPosInPool
{
	StatementPosInPool(DbConnectionBase connection, String csStatementId)
	{
		connection = connection; 
		csStatementId = csStatementId;
	}
	
	boolean forceRemoveStatement()
	{
		if(connection != null)
		{
			boolean b = connection.forceRemoveStatement(csStatementId);
			connection = null;
			return b;
		}
		return false;
	}	
	
	private DbConnectionBase connection = null;
	private String csStatementId = null;
}
