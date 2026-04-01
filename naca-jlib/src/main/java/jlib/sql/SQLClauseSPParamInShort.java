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

import java.sql.SQLException;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SQLClauseSPParamInShort.java,v 1.1 2007/10/16 09:47:08 u930di Exp $
 */
public class SQLClauseSPParamInShort extends SQLClauseSPParamIn
{
	private short val = 0;
	
	public SQLClauseSPParamInShort(short val)
	{
		val = val;
	}
	
	protected void setInValueWithException(int nParamId, DbPreparedCallableStatement stmt)
		throws SQLException
	{
		stmt.setInValueWithException(nParamId, val);
	}
}
