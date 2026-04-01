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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SQLClauseSPSupport.java,v 1.4 2008/04/01 11:45:06 u930bm Exp $
 */
public class SQLClauseSPSupport
{
	public SQLClauseSPSupport()
	{
	}

	public ArrayList<SQLClauseSPInfo> getStoredProceduresList(DbConnectionBase dbConnection)
	{
		ArrayList<SQLClauseSPInfo> arr = new ArrayList<SQLClauseSPInfo>();
		try
		{
			DatabaseMetaData dmd = dbConnection.getDbConnection().getMetaData();
			ResultSet resultSetprocs = dmd.getProcedures(null, null, "%");
			boolean b = true;
			while(resultSetprocs.next() && b)
			{
				SQLClauseSPInfo info = new SQLClauseSPInfo();
				if(info.fill(resultSetprocs))
					arr.add(info);
			}
		}
		catch (SQLException e)
		{
			return null;
		}
		return arr;	
	}

	public SQLClauseSPParamsDesc getStoredProcedureParamsList(DbConnectionBase dbConnection, String csStoredProcName)
	{
		SQLClauseSPParamsDesc spinnerparamsDesc = new SQLClauseSPParamsDesc();
				
		try
		{
			DatabaseMetaData dmd = dbConnection.getDbConnection().getMetaData();
			if (csStoredProcName.indexOf(".") != -1) // suppress the user if exists in the procedure
				csStoredProcName = csStoredProcName.substring(csStoredProcName.indexOf(".") + 1); 
			ResultSet resultSetparams = dmd.getProcedureColumns(null, dbConnection.getEnvironmentPrefix(), csStoredProcName.replace("_", "\\_"), "%");
			boolean b = true;
			while(resultSetparams.next() && b)
			{
				spinnerparamsDesc.addAParam(resultSetparams);
			}
		}
		catch (SQLException e)
		{			
			return null;
		}

		return spinnerparamsDesc;
	}

}
