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


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SQLClauseSPInfo.java,v 1.1 2007/10/16 09:47:08 u930di Exp $
 */
public class SQLClauseSPInfo
{
	public String csCatalog = null;
	public String csName = null;
	public String csRemarks = null;
	public String csSchem = null;
	public short type = 0;
	
	SQLClauseSPInfo()
	{
	}
	
	boolean fill(ResultSet rsProc)
	{
		try
		{
			csCatalog = rsProc.getString("PROCEDURE_CAT");
			csName = rsProc.getString("PROCEDURE_NAME");
			csRemarks = rsProc.getString("REMARKS");
			type = rsProc.getShort("PROCEDURE_TYPE");
			csSchem = rsProc.getString("PROCEDURE_SCHEM");
			return true;
		}
		catch(SQLException e)
		{
		}
		return false;
	}
}
