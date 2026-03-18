/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.dbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.bdb.BtreePooledThreadWriterFactory;
import nacaLib.sqlSupport.SQLConnectionManager;
import jlib.log.Log;
import jlib.misc.StringUtil;
import jlib.sql.DbConnectionBase;
import jlib.sql.DbConnectionException;
import jlib.sql.DbConnectionPool;
import jlib.sql.DbPreparedStatement;
import jlib.threads.PoolOfThreads;
import jlib.threads.SimpleThreadPool;
import jlib.xml.Tag;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class DbTransferDesc
{
	private int nBatchSize = 0;
	private int nCommitEveryBatch = 0;
	private int nThreadsQuantity = 0;
	private String csDefinitionTable;
	private SQLConnectionManager connectionManager = null;
	private ArrayList<TableToTransfer> arrTableToTransfer = new ArrayList<TableToTransfer>();
	private PoolOfThreads threadsPool = null;
	private boolean bTransferGlobalStatus = true; 
	
	boolean load(Tag tagDbTransfer)
	{
		boolean b = true;
		
		if(tagDbTransfer != null)
		{
			Tag tagParameters = tagDbTransfer.getChild("Parameters");
			if(tagParameters != null)
			{
				nBatchSize = tagParameters.getValAsInt("BatchSize");
				nCommitEveryBatch = tagParameters.getValAsInt("CommitEveryBatch");
				nThreadsQuantity = tagParameters.getValAsInt("ThreadsQuantity");
				csDefinitionTable = tagParameters.getVal("DefinitionTable");
			}
			else b = false;
			
			Tag tagSQLConfig = tagDbTransfer.getChild("SQLConfig");
			if(tagSQLConfig != null)
			{
				connectionManager = new SQLConnectionManager();
				DbConnectionPool dbConnectionPool = connectionManager.init("", tagSQLConfig);
				BaseResourceManager.addDbConnectionPool(dbConnectionPool);
			}
			else b = false;
		}
		else
		{
			Log.logCritical("No Accounting tag in .cfg file: Accouting is disabled");
			b = false;
		}
		return b;
	}
	
	boolean getTablesList(BaseEnvironment env)
	{
		String csUpdateClause = getUpdateStatementString();
		
		String csClause = "Select TName, Replace, LastWrite, NbRead, NbWrite, SQLError From " + csDefinitionTable + " order by TName asc";
		DbConnectionBase dbConnectionSource = env.getSQLConnection();
		DbPreparedStatement st = dbConnectionSource.prepareStatement(csClause, 0, false);
		if(st != null)
		{
			ResultSet resultSet = st.executeSelect();
			if(resultSet != null)
			{
				try
				{
					while(resultSet.next())
					{
						String csTableName = resultSet.getString(1);
						String csReplace = resultSet.getString(2);
						String cs = resultSet.getString(3);
						cs = resultSet.getString(4);
						cs = resultSet.getString(5);
						cs = resultSet.getString(6);
						
						TableToTransfer tableToTransfer = new TableToTransfer(csTableName, csReplace, csUpdateClause);
						arrTableToTransfer.add(tableToTransfer);
					}
					st.close();
					return true;
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private String getUpdateStatementString()
	{
		StringBuilder sbUpdate = new StringBuilder("update ");
		sbUpdate.append(csDefinitionTable);
		sbUpdate.append(" set LASTWRITE=?, NBREAD=?, NBWRITE=?, SQLERROR=? Where TNAME=");
		String csUpdate = sbUpdate.toString();
		return csUpdate;
	}
	
	DbConnectionBase getNewDestinationConnection()
	{
		if(connectionManager != null)
		{
			try
			{
				DbConnectionBase dbConnection = connectionManager.getConnection("DBTR", false);	// get a new db connection
				return dbConnection;
			}
			catch (DbConnectionException e)
			{
				Log.logCritical("Could not get DB connection for accounting !");
			}
		}
		return null;
	}

	private DbConnectionBase getNewSourceConnection(BaseEnvironment env)
	{
		DbConnectionBase dbConnectionSource = env.getNewSQLConnection();
		return dbConnectionSource;
	}
		
	boolean doTransfers(BaseEnvironment env)
	{		
		int nNbTables = arrTableToTransfer.size();
		
		PooledThreadDbTransferFactory pooledThreadDbTransferFactory = new PooledThreadDbTransferFactory(this, env);
		
		threadsPool = new PoolOfThreads(pooledThreadDbTransferFactory, nThreadsQuantity, nNbTables);
		threadsPool.startAllThreads();
		for(int n=0; n<nNbTables; n++)
		{
			TableToTransfer tableToTransfer = arrTableToTransfer.get(n);
			threadsPool.enqueue(tableToTransfer);
		}
		
		threadsPool.stop();
		return bTransferGlobalStatus;
	}	
	
	synchronized void setTransferGlobalFailure()
	{
		bTransferGlobalStatus = false;;
	}
	
	int getCommitEveryBatch()
	{
		return nCommitEveryBatch;
	}
	
	int getBatchSize()
	{
		return nBatchSize;
	}
}

