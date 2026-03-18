/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */


package jlib.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;
import java.util.SortedMap;

import jlib.blowfish.Blowfish;
//import jlib.log.Log;
import jlib.misc.BaseJmxGeneralStat;
import jlib.misc.EnvironmentVar;
import jlib.misc.StopWatch;
import jlib.misc.StringUtil;
import jlib.misc.ThreadSafeCounter;
import jlib.misc.Time_ms;


/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DbConnectionColl
{
	private LinkedList<DbConnectionBase> collFreeConnections = null;		// Collection of the connections currently not in use
	private LinkedList<DbConnectionBase> collUsedConnections = null;		// Collection of the connections currently in use 
	
	private DbConnectionParam dbConnectionParam = null;
	private int nGarbageCollectorStatement_ms = 0;
	
	private int nNbMaxConnection = 1;	// Unlimited
	private int nTimeBeforeRemoveConnection_ms = 0;
	private int nMaxStatementLiveTime_ms = -1;
	private boolean bUseExplain = false;
	private StopWatch swLastCheckRemoveObsoleteConnections = new StopWatch();
	//private int nNbConnectionCreated = 0;
	private ThreadSafeCounter tscNbConnectionCreated = new ThreadSafeCounter(0);
	private boolean bInit = false;
	private String csName = null;
	private boolean bShowRunningConnections = false;
	
	DbConnectionColl(String csName, int nNbMaxConnection, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms, boolean bUseExplain, int nGarbageCollectorStatement_ms)
	{
		collFreeConnections = new LinkedList<DbConnectionBase>();
		collUsedConnections = new LinkedList<DbConnectionBase>();
		nNbMaxConnection = nNbMaxConnection;
		nTimeBeforeRemoveConnection_ms = nTimeBeforeRemoveConnection_ms;
		nMaxStatementLiveTime_ms = nMaxStatementLiveTime_ms;
		bUseExplain = bUseExplain;
		nGarbageCollectorStatement_ms = nGarbageCollectorStatement_ms;
		csName = csName;
	}
	
	public boolean isInit()
	{
		return bInit;
	}
	
	void setName(String csName)
	{
		csName = csName;
	}

	public String getName()
	{
		return csName;
	}

	void init(DbConnectionParam dbConnectionParam)
	{
		dbConnectionParam = dbConnectionParam;
		bInit = true;
	}


	private synchronized DbConnectionBase popAtIndex(int nIndex)
	{
		try
		{
			if(collFreeConnections.size() > 0)
			{
				DbConnectionBase connection = collFreeConnections.remove(nIndex);	// The connection is not free anymore
				collUsedConnections.add(connection);		// It's then in use
				connection.showHideJMXBean(bShowRunningConnections);
				return connection;
			}
		}
		catch(IndexOutOfBoundsException e)
		{
		}
		return null;
	}
	
	DbConnectionBase tryGetPooledValidConnection(String csValidationQuery, String csPoolName, boolean bUseStatementCache, DbConnectionManagerBase connectionManager)
		throws DbConnectionException
	{
		while(true)
		{
			DbConnectionBase sqlConnection = popAtIndex(0);
			while(sqlConnection != null)
			{
				if(sqlConnection.canBeUsed(nTimeBeforeRemoveConnection_ms, csValidationQuery))
				{
//					Log.logNormal("Re-using validated db connection from cache. "+ getNbFreeConnection()+" still available.");
					return sqlConnection;
				}
				removeConnection(sqlConnection);
				sqlConnection = popAtIndex(0);
			}
			
			// No sqlConnection found in the pool: Create a new one if max limit not reached
			sqlConnection = createNewConnection(csPoolName, bUseStatementCache, connectionManager, csValidationQuery);
			
			if(sqlConnection != null)	// Could create a new connection
				return sqlConnection;
			
			BaseJmxGeneralStat.incCounter(BaseJmxGeneralStat.COUNTER_INDEX_NbWaitDuringConnectionCreate);
			waitUntilConnectionAvailableOrCreatable();
		}
	}
	
	DbConnectionBase forceNewConnection(String csValidationQuery, String csPoolName, boolean bUseStatementCache, DbConnectionManagerBase connectionManager)
		throws DbConnectionException
	{
		// No sqlConnection found in the pool: Create a new one if max limit not reached
		DbConnectionBase sqlConnection = createNewConnection(csPoolName, bUseStatementCache, connectionManager, csValidationQuery);
		//if (sqlConnection != null) // PJD 19/06/2008: commented line
			//tscNbConnectionCreated.dec();	// PJD 19/06/2008: commented line
		return sqlConnection;
	}
	
	private void waitUntilConnectionAvailableOrCreatable()
	{
		Time_ms.wait_ms(1000);
	}
	
	private String replaceEnvVarsByValue(String csUrl)
	{
		int nStartPos = csUrl.indexOf('%');
		while(nStartPos >= 0)
		{
			int nEndPos = csUrl.indexOf('%', nStartPos+1);
			if(nEndPos >= 0)
			{
				String csLeft = csUrl.substring(0, nStartPos);
				String csRight = csUrl.substring(nEndPos+1);
				String csToken = csUrl.substring(nStartPos+1, nEndPos);
				String csValue = EnvironmentVar.getParamValue(csToken);
				if(StringUtil.isEmpty(csValue))
					csValue = "NULL";
				csUrl = csLeft + csValue + csRight;
			}
			nStartPos = csUrl.indexOf('%');
		}
		return csUrl;
	}
	
	private DbConnectionBase createNewConnection(String csPoolName, boolean bUseStatementCache, DbConnectionManagerBase connectionManager, String csValidationQuery)
		throws DbConnectionException
	{
		
//		Log.logNormal(tscNbConnectionCreated.get()+" created connections, out of "+nNbMaxConnection+" allowed.");
		
		if(tscNbConnectionCreated.get() < nNbMaxConnection || nNbMaxConnection == -1)
		{
		    try
			{
		    	String csUrl = dbConnectionParam.csUrl;
		    	if(dbConnectionParam.csConnectionUrlOptionalParams != null)
		    		csUrl += dbConnectionParam.csConnectionUrlOptionalParams;
		    	csUrl = StringUtil.replace(csUrl, "$FoundPoolName", csPoolName, true);
		    	csUrl = replaceEnvVarsByValue(csUrl);
		    	
				Connection connection = null;
				String csUser = (String)dbConnectionParam.propertiesUserPassword.get("user");
				String csCryptedPassword = (String)dbConnectionParam.propertiesUserPassword.get("CryptedPassword");
				String csCryptKey = (String)dbConnectionParam.propertiesUserPassword.get("CryptKey");
				if(!StringUtil.isEmpty(csCryptedPassword) && !StringUtil.isEmpty(csCryptKey))
				{
					// Got a crypted db password
					// Code to move in a private jar; not distrobuted as a source
					Blowfish blowfish = new Blowfish(csCryptKey, true);
					String csPassword = blowfish.decrypt(csCryptedPassword);
					
					Properties propertiesUserPassword = new Properties();
					propertiesUserPassword.setProperty("user", csUser);	
					propertiesUserPassword.setProperty("password", csPassword);
				
					connection = dbConnectionParam.driver.connect(csUrl, propertiesUserPassword);
//					if(connection != null)
//						Log.logNormal("Correctly created new DB connection with crypted user/password. "+ tscNbConnectionCreated.get()+" created connections, out of "+nNbMaxConnection+" allowed.");
					
				}
				else
				{
					connection = dbConnectionParam.driver.connect(csUrl, dbConnectionParam.propertiesUserPassword);
//					if(connection != null)
//						Log.logNormal("Correctly created new DB connection. "+ tscNbConnectionCreated.get()+" created connections, out of "+nNbMaxConnection+" allowed.");
				}
				if(connection == null)
				{
//					Log.logCritical("ERROR: Could not create new DB connection. "+ tscNbConnectionCreated.get()+" existing connections, out of "+nNbMaxConnection+" allowed.");
					throw new DbConnectionException("Could not get valid DB Connection");
				}

				if (!StringUtil.isEmpty(dbConnectionParam.csPackage))
				{
					setConnectionPackage(connection, dbConnectionParam.csPackage);
				}

		    	connection.setAutoCommit(dbConnectionParam.bAutoCommit);
		    	if(dbConnectionParam.bCloseCursorOnCommit)
		    	{
		    		connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
		    	}
		    	
		    	DbDriverId dbDriverId = dbConnectionParam.getDbDriverId();
		    	
		    	DbConnectionBase sqlConnection = connectionManager.createConnection(connection, csPoolName, dbConnectionParam.getEnvironment(), bUseStatementCache, true, dbDriverId);
		    	sqlConnection.setDbConnectionColl(this);
		    	
		    	sqlConnection.setUseExplain(getUseExplain());
				
				if(sqlConnection.checkWithQuery(csValidationQuery))
				{
					tscNbConnectionCreated.inc();
					String csPrefix = connectionManager.getPropertyPrefix();
					sqlConnection.setOnceUUID(csPrefix);
					collUsedConnections.add(sqlConnection);	// this connection is in use
					sqlConnection.showHideJMXBean(bShowRunningConnections);
					return sqlConnection;
				}
				else
				{
					sqlConnection.close();
					sqlConnection = null;
				}
		    }
		    catch(DbConnectionException e)
			{
//		    	Log.logCritical("DbConnectionException; exception=" + e.getMessage());
		    	throw e;
			}
		    catch(SQLException e)
			{
		    	throw new DbConnectionException(e.getMessage());
			}
		    catch (Exception ex) 
			{
		    	throw new RuntimeException(ex.getMessage());
		    }
		}
		return null;
	}
	
	private void setConnectionPackage(Connection connection, String csDbPackage)
	{
		try
		{
			Statement stmt = connection.createStatement();
			stmt.execute("SET CURRENT PACKAGESET = '" + csDbPackage + "'");
			stmt.close();
		}
		catch (Exception ex)
		{
		}
	}
	
	int removeConnection(DbConnectionBase connection)
	{
		int n = connection.removeAllPreparedStatements();
		connection.close();
		connection.dbConnectionColl = null;
		connection.dbConnection = null;
		tscNbConnectionCreated.dec();
//		Log.logNormal("Removing DB connection from pool. "+ tscNbConnectionCreated.get()+" existing connections, out of "+nNbMaxConnection+" allowed.");

		return n;
	}
	
	void removeConnectionFromUsed(DbConnectionBase sqlConnection)
	{
		if(collUsedConnections.contains(sqlConnection))
		{
			collUsedConnections.remove(sqlConnection);
			sqlConnection.showHideJMXBean(false);	// Hide
		}
	}
	
	synchronized void releaseConnection(DbConnectionBase sqlConnection)
	{
		removeConnectionFromUsed(sqlConnection);
		if(sqlConnection.isGenerationCurrent())	// The connection belongs to the current generation and can be kept 
		{
			// Check if the current generation matches the last generation
			sqlConnection.markLastTimeUsage();
			
			if(swLastCheckRemoveObsoleteConnections.isTimeElapsed(nGarbageCollectorStatement_ms))
			{
				removeObsoleteConnections(); // Remove connections in timeout
				sqlConnection.garbageCollectorStatementsOptinalResetReservedStatement(true);
			}
			else
				sqlConnection.resetReservedStatements();
//			Log.logNormal("Returning DB connection to pool. "+ tscNbConnectionCreated.get()+" existing connections, out of "+nNbMaxConnection+" allowed.");
			collFreeConnections.addFirst(sqlConnection);
		}
		else
		{
			// The connection generation has changed and connection can't be kept
//			Log.logNormal("DB Connection generation changed; DB connection is not returned to pool and removed. "+ tscNbConnectionCreated.get()+" existing connections, out of "+nNbMaxConnection+" allowed.");
			removeConnection(sqlConnection);
			sqlConnection = null;
		}
	}
	
	synchronized private void removeObsoleteConnections()
	{	
		DbConnectionBase connection = null;
		if(collFreeConnections.size() > 0)
			connection = collFreeConnections.getLast();
		while(connection != null && !connection.isValid(nTimeBeforeRemoveConnection_ms))
		{
			removeConnection(connection);
			collFreeConnections.removeLast();
			if(collFreeConnections.size() > 0)
				connection = collFreeConnections.getLast();
			else
				connection = null;
		}
		swLastCheckRemoveObsoleteConnections.Reset();
	}	
	
	synchronized int garbageCollectorStatementsOfCollection()
	{
		int nNbStatementRemoved = 0;
		int nIndex = 0;
		
		DbConnectionBase connection = popAtIndex(nIndex);
		while(connection != null)
		{			
			if(!connection.isValid(nTimeBeforeRemoveConnection_ms))
			{
				nNbStatementRemoved += removeConnection(connection);
			}
			else
			{
				// Connection is still valid
				nNbStatementRemoved += connection.garbageCollectorStatementsOptinalResetReservedStatement(false);
				collFreeConnections.add(nIndex, connection);
				nIndex++;
			}
			
			connection = popAtIndex(nIndex);
		}
		return nNbStatementRemoved;		
	}
	
	synchronized void forceRemoveAllStatementsOfCollection()
	{
		int nIndex = 0;
		DbConnectionBase connection = popAtIndex(nIndex);
		while(connection != null)
		{			
			removeConnection(connection);
			connection = popAtIndex(nIndex);
		}	
	}
	
	synchronized void dumpListStatements(SortedMap<Long, StatementPosInPool> mapStatements)
	{
		for(int nConnectionId=0; nConnectionId<collFreeConnections.size(); nConnectionId++)
		{
			DbConnectionBase connection = collFreeConnections.get(nConnectionId);
			connection.dumpListStatements(mapStatements);
		}
	}
	
	int getMaxStatementLiveTime_ms()
	{
		return nMaxStatementLiveTime_ms;
	}
	
	private boolean getUseExplain()
	{
		return bUseExplain;
	}
	
	int getNbMaxConnection()
	{
		return nNbMaxConnection;
	}

	/**
	 * Returns the size of the collection of free connections.
	 * To be used for logging.
	 * @return The size of the collection of free connections.
	 */
	synchronized int getNbFreeConnection()
	{
		if(collFreeConnections != null)
			return collFreeConnections.size();
		return 0;
	}
	
	synchronized int getNbCachedStatementsForAccessor()
	{
		if(collFreeConnections == null)
			return 0;
		
		int n = 0;
		for(int nConnectionId=0; nConnectionId<collFreeConnections.size(); nConnectionId++)		
		{
			DbConnectionBase connection = collFreeConnections.get(nConnectionId);
			n += connection.getNbCachedStatements();
		}
		return n;
	}
	
	int getNbAllocConnnections()
	{
		return tscNbConnectionCreated.get();
	}
	
	int getNbRunningConnections()
	{
		if(collUsedConnections != null)
			return collUsedConnections.size();
		return 0;
	}

	void showHideRunningConnections(boolean bShowRunningCon)
	{
		bShowRunningConnections = bShowRunningCon;
		if(collUsedConnections != null)
		{
			for(int nConnectionId=0; nConnectionId<collUsedConnections.size(); nConnectionId++)		
			{
				DbConnectionBase connection = collUsedConnections.get(nConnectionId);
				connection.showHideJMXBean(bShowRunningConnections);
			}
		}		
	}
	
	public void dumpConnections(StringBuilder sbText)
	{
		if(collUsedConnections != null)
		{
			for(int nConnectionId=0; nConnectionId<collUsedConnections.size(); nConnectionId++)		
			{
				DbConnectionBase connection = collUsedConnections.get(nConnectionId);
				connection.dumpConnections(sbText);
			}
		}		
	}
}
