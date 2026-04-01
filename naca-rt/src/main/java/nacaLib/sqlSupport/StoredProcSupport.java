/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.sqlSupport;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import jlib.sql.DbConnectionBase;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: StoredProcSupport.java,v 1.2 2007/02/16 16:15:48 u930bm Exp $
 */
public class StoredProcSupport
{
	public StoredProcSupport()
	{
	}

	public ArrayList<StoredProcInfo> getStoredProceduresList(DbConnectionBase dbConnection)
	{
		ArrayList<StoredProcInfo> arr = new ArrayList<StoredProcInfo>();
		try
		{
			DatabaseMetaData dmd = dbConnection.getDbConnection().getMetaData();
			ResultSet resultSetprocs = dmd.getProcedures(null, null, "%");
			boolean b = true;
			while(resultSetprocs.next() && b)
			{
				StoredProcInfo info = new StoredProcInfo();
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
		
	public StoredProcParams getStoredProcedureParamsList(DbConnectionBase dbConnection, String csStoredProcName)
	{
		StoredProcParams storedProcParams = new StoredProcParams();
				
		try
		{
			DatabaseMetaData dmd = dbConnection.getDbConnection().getMetaData();
			String user = dbConnection.getEnvironmentPrefix();
			ResultSet resultSetparams = dmd.getProcedureColumns(null, dbConnection.getEnvironmentPrefix(), csStoredProcName, "%");
			boolean b = true;
			while(resultSetparams.next() && b)
			{
				storedProcParams.addAParam(resultSetparams);
			}
		}
		catch (SQLException e)
		{			
			return null;
		}

		return storedProcParams;
	}

}
