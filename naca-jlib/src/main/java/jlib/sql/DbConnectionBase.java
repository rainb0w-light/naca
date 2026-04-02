/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 8 juil. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jlib.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import jlib.exception.ProgrammingException;
import jlib.exception.TechnicalException;
import jlib.log.Log;
import jlib.misc.BaseJmxGeneralStat;
import jlib.misc.StopWatch;
import jlib.misc.StringUtil;
import jlib.misc.Time_ms;
import jlib.threads.Threadutil;

public abstract class DbConnectionBase //extends BaseOpenMBean
{
	private boolean isuseJmx = true;
	private String csPrefId = null;
	private String csEnvironment = "" ;
	protected Connection dbConnection = null;
	private boolean isuseRowId = false;	// true if must use RowId to support updates in cursors "select for update" (Oracle needs it)
	private Hashtable<String, DbPreparedStatement> hashStatement = new Hashtable<String, DbPreparedStatement>();	// Hsah collection of statement; Vey=int (hashed statement string), Value=Statement
	private boolean isuseCachedStatements = true;	// true if a cache of all met statements is kept by the current connection, false if the statement is recreated
	private StopWatch stopWatchLastUsage = null;
	private int nMaxStatementLiveTime_ms = -1;	// INFINITE by default
	private int nGenerationId = -1;
	public DbConnectionColl dbConnectionColl = null;
	private boolean isuseExplain = false;
	private DbDriverId dbDriverId = null;
	private String csUUID = null;
	
	public DbConnectionBase(Connection conn, String csPrefId, String csEnv, boolean isuseCachedStatements, boolean bUseJmx, DbDriverId dbDriverId)
	{
		dbDriverId = dbDriverId;
		//super("DbConnectionBase_"+csPrefId, "DbConnectionBase");
		csPrefId = csPrefId;
		isuseCachedStatements = isuseCachedStatements;
		dbConnection = conn ;
		if(csEnv.equals("OracleTest"))	// Tests have no prefixe
		{
			isuseRowId = true;
		}
		else
		{
			csEnvironment = csEnv ;
		}
		stopWatchLastUsage = new StopWatch();
		
		bUseJmx = bUseJmx;
		if(bUseJmx)
		{
			BaseJmxGeneralStat.incCounter(BaseJmxGeneralStat.COUNTER_INDEX_NbNonFinalizedConnection);
			BaseJmxGeneralStat.incCounter(BaseJmxGeneralStat.COUNTER_INDEX_NbActiveConnection);
		}
		
		nGenerationId = ConnectionGenerationManager.getGenerationId();
	}
	
	public DbDriverId getDbDriverId()
	{
		return dbDriverId;
	}
		
	public void finalize()
	{
		if(isuseJmx)
			BaseJmxGeneralStat.decCounter(BaseJmxGeneralStat.COUNTER_INDEX_NbNonFinalizedConnection);
	}
	
	public void close()
	{
		if(isuseJmx)
			BaseJmxGeneralStat.decCounter(BaseJmxGeneralStat.COUNTER_INDEX_NbActiveConnection);
		doClose();
	}	
	
	void setDbConnectionColl(DbConnectionColl dbConnectionColl)
	{
		dbConnectionColl = dbConnectionColl;
		if(dbConnectionColl != null)
		{
			nMaxStatementLiveTime_ms = dbConnectionColl.getMaxStatementLiveTime_ms(); 
		}
	}
		
	boolean isGenerationCurrent()
	{
		return ConnectionGenerationManager.isGenerationCurrent(nGenerationId);
	}
	
	public void setConnectionUnreusable()
	{
		nGenerationId = -1;	// This connection won't reused
	}	
	
	boolean canBeUsed(int nTimeBeforeRemoveConnection_ms, String csValidationQuery)
	{
		if(ConnectionGenerationManager.isGenerationCurrent(nGenerationId))
		{
			if(isValid(nTimeBeforeRemoveConnection_ms) && isOpen())
			{
				if(checkWithQuery(csValidationQuery))
					return true;
			}
		}
		return false;
	}
	
	boolean isValid(int nTimeBeforeRemoveConnection_ms)
	{
		if(dbConnection != null)
		{
			try
			{
				if(dbConnection.isClosed())
					return false;
				// Still open
				if(stopWatchLastUsage.isTimeElapsed(nTimeBeforeRemoveConnection_ms))	// Obsolete
					return false;				
				return true;
			} 
			catch (SQLException e)
			{
				LogSQLException.log(e);
			}
		}
		return false;
	}
	
	
	boolean isOpen()
	{
		if(dbConnection != null)
		{
			try
			{
				if(!dbConnection.isClosed())
				{
					return true;
				}
			} 
			catch (SQLException e)
			{
				LogSQLException.log(e);
			}
		}
		return false;				
	}
		
	protected void doClose()
	{
		if(dbConnection != null)
		{
			try
			{
				if(!dbConnection.isClosed())
					dbConnection.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	synchronized int garbageCollectorStatementsOptinalResetReservedStatement(boolean bResetReservedStatements)
	{
		if(hashStatement == null)
			return 0;
		
		int n = 0;
		Set<Map.Entry<String, DbPreparedStatement>> set = hashStatement.entrySet();
		Iterator<Map.Entry<String, DbPreparedStatement>> iterMapEntry = set.iterator();
		while(iterMapEntry.hasNext())
		{
			Map.Entry<String, DbPreparedStatement> entry = iterMapEntry.next();
			DbPreparedStatement dbPreparedStatement = entry.getValue();
			if(dbPreparedStatement.isTimeOut(nMaxStatementLiveTime_ms))
			{
				dbPreparedStatement.close();
				iterMapEntry.remove();
				n++;
			}
			else if(bResetReservedStatements)
				dbPreparedStatement.resetReserved();
		}
		
		return n;
	}
	
	synchronized int getNbCachedStatements()
	{
		if(hashStatement == null)
			return 0;
		return hashStatement.size();
	}
	
	synchronized void resetReservedStatements()
	{
		if(hashStatement == null)
			return;
		
		Set<Map.Entry<String, DbPreparedStatement>> set = hashStatement.entrySet();
		Iterator<Map.Entry<String, DbPreparedStatement>> iterMapEntry = set.iterator();
		while(iterMapEntry.hasNext())
		{
			Map.Entry<String, DbPreparedStatement> entry = iterMapEntry.next();
			DbPreparedStatement dbPreparedStatement = entry.getValue();
			dbPreparedStatement.resetReserved();
		}
	}
	
	synchronized void dumpListStatements(SortedMap<Long, StatementPosInPool> mapStatements)
	{
		if(hashStatement == null)
			return;
		
		Set<Map.Entry<String, DbPreparedStatement>> set = hashStatement.entrySet();
		Iterator<Map.Entry<String, DbPreparedStatement>> iterMapEntry = set.iterator();
		while(iterMapEntry.hasNext())
		{			
			Map.Entry<String, DbPreparedStatement> entry = iterMapEntry.next();
			String csStatementId = entry.getKey();
			DbPreparedStatement dbPreparedStatement = entry.getValue();
			
			if(!dbPreparedStatement.isReserved())
			{
				StatementPosInPool pos = new StatementPosInPool(this, csStatementId);			
				mapStatements.put(dbPreparedStatement.getLastUsageTimeValue(), pos);
			}
		}
	}
	
	boolean checkWithQuery(String csValidationQuery)
	{
		if(StringUtil.isEmpty(csValidationQuery))
			return true;
		
		boolean b = false;
		DbPreparedStatement sqlStatement = prepareStatement(csValidationQuery, 0, false);
		if(sqlStatement != null)
		{
			ResultSet r = sqlStatement.executeSelect();			
			if(r != null)
			{
				try
				{
					r.close();
					b = true;
				}
				catch (SQLException e)
				{
				}
			}
			else
			{
				Log.logCritical("Error during check DB connection with query " + csValidationQuery);
			}
	
			if(!isuseCachedStatements)
				sqlStatement.close();
		}
		return b;
	}
	
	void markLastTimeUsage()
	{
		stopWatchLastUsage.Reset();
	}
	
	public synchronized int removeAllPreparedStatements()
	{
		if(hashStatement == null)
			return 0;
		
		int n = 0;
		Collection<DbPreparedStatement> col = hashStatement.values();
		Iterator<DbPreparedStatement> iter = col.iterator();
		while(iter.hasNext())
		{
			DbPreparedStatement statement = iter.next();
			statement.close();
			n++;
		}

		hashStatement = null;
		return n;
	}

	//int nDEBUGCount = 0;
	
	synchronized private DbPreparedStatement getCachedStatement(String csQueryHash)
	{
		if(hashStatement == null)
			return null;
		
		DbPreparedStatement SQLStatement = hashStatement.get(csQueryHash);
		if(SQLStatement != null)
			SQLStatement.setStatementUsed();
		return SQLStatement;
	}
	
	synchronized boolean forceRemoveStatement(String csStatementId)
	{
		if(hashStatement == null)
			return false;
		
		DbPreparedStatement dbPreparedStatement = hashStatement.get(csStatementId);
		boolean b = dbPreparedStatement.closeIfNotReserved();
		if(b)
			hashStatement.remove(csStatementId);
		return b; 
	}
	
	public int executeOperation(SQLTypeOperation typeOperation)
	{
		if(typeOperation== SQLTypeOperation.Commit)
		{
			int n = commit();
			if(n != 0)
				return -1;
			return 0;
		}
		else if(typeOperation == SQLTypeOperation.Rollback)
		{
			int n = rollBack();
			if(n != 0)
				return -1;
			return 0;
		}
		return -1;
	}
	
	public DbPreparedStatement prepareStatement(String csQuery)
	{
		return prepareStatement(csQuery, 0, false);
	}
	
	public DbPreparedStatement prepareStatement(SQLClause sqlStatement)
	{
		String csQuery = sqlStatement.getQuery();
		DbPreparedStatement preparedStatement = prepareStatement(csQuery, 0, false);
		sqlStatement.fillParameters(preparedStatement);
		
		return preparedStatement; 
	}
	
//	ResultSet prepareAndExecuteSelect(SQLClause sqlClause) 
//		throws TechnicalException
//	{
//		ResultSet resultSet = null;		
//		
//		String csQuery = sqlClause.getQuery();
//		SQLTypeOperation typeOperation = SQLTypeOperation.determineOperationType(csQuery, false);
//		if(typeOperation != SQLTypeOperation.Select)
//		{			
//			TechnicalException.throwException(TechnicalException.NOT_SELECT_STMT, csQuery);
//		}
//		
//		String csPrefixedQuery = SQLTypeOperation.addEnvironmentPrefix(getEnvironmentPrefix(), csQuery, typeOperation, "");
//		DbPreparedStatement preparedStatement = prepareStatementWithException(csPrefixedQuery, 0, false);
//		if(preparedStatement != null)
//		{
//			sqlClause.fillParameters(preparedStatement);
//			
//			resultSet = preparedStatement.executeSelectWithException();
//		}
//		return resultSet; 
//	}
	
	int prepareAndExecuteWithException(SQLClause sqlClause)
		throws TechnicalException
	{
		String csQuery = sqlClause.getQuery();

		TechnicalException.throwIfNullOrEmpty(csQuery, TechnicalException.DB_ERROR_PREPARE_STATEMENT,"Query is not set. Call 'SQLClause.set' before trying to execute the query.");
		
		SQLTypeOperation typeOperation = SQLTypeOperation.determineOperationType(csQuery, false);
		
		String csPrefixedQuery = SQLTypeOperation.addEnvironmentPrefix(getEnvironmentPrefix(), csQuery, typeOperation, "");
		DbPreparedStatement preparedStatement = prepareStatementWithException(csPrefixedQuery, 0, false);
		if(preparedStatement != null)
		{
			sqlClause.fillParameters(preparedStatement);
			if(typeOperation == SQLTypeOperation.Select || typeOperation==null)
			{
				try {
					ResultSet resultSet = preparedStatement.executeSelectWithException();
					sqlClause.setResultSetSet(resultSet);
				}
				catch (ProgrammingException e) {
					if (e.getCause() instanceof SQLException) {
						ProgrammingException.throwException(ProgrammingException.DB_ERROR_SELECT, sqlClause, (SQLException)e.getCause());
					} else
						throw e;
				}
				return 1;
			}
			else
			{
				int n = preparedStatement.executeWithException(typeOperation, sqlClause);
				return n;
			}
		}
			
		return -1; 
	}

	synchronized public DbPreparedStatement prepareStatement(String csQuery, int nSuffixeHash, boolean bHoldability)
	{
		String csQueryHash = csQuery + nSuffixeHash;
		if(isuseCachedStatements)
		{
			DbPreparedStatement SQLStatement = getCachedStatement(csQueryHash);
			if(SQLStatement != null)
				return SQLStatement;
		}
		
		DbPreparedStatement SQLStatement = createAndPrepare(csQuery, bHoldability);
		if(SQLStatement != null && hashStatement != null)
		{
			if(isuseCachedStatements)
				hashStatement.put(csQueryHash, SQLStatement);
		}
		return SQLStatement;
	}
	
	synchronized public DbPreparedStatement prepareStatementWithException(String csQuery, int nSuffixeHash, boolean bHoldability) 
		throws TechnicalException
	{
		String csQueryHash = csQuery + nSuffixeHash;
		if(isuseCachedStatements)
		{
			DbPreparedStatement SQLStatement = getCachedStatement(csQueryHash);
			if(SQLStatement != null)
				return SQLStatement;
		}
		
		DbPreparedStatement SQLStatement = createAndPrepareWithException(csQuery, bHoldability);
		if(SQLStatement != null && hashStatement != null)
		{
			if(isuseCachedStatements)
				hashStatement.put(csQueryHash, SQLStatement);
		}
		return SQLStatement;
	}
	
	public Statement create()
	{
		try
		{
			Statement statement = dbConnection.createStatement();
			return statement;
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public abstract DbPreparedStatement createAndPrepare(String csQuery, boolean bHoldability);
	public abstract DbPreparedStatement createAndPrepareWithException(String csQuery, boolean bHoldability) throws TechnicalException;
	
	public abstract boolean prepareCallableStatement(DbPreparedCallableStatement preparedCallableStatement, String csStoredProcName, int nNbParamToProvide);
	
	public int rollBack()
	{
		//markLastTimeStamp();
		if (dbConnection != null)
		{
			try
			{
				dbConnection.rollback() ;
				return 0;
			}
			catch (SQLException e)
			{
				LogSQLException.log(e);
				return e.getErrorCode();
			}
		}
		return 0;
	}
	
	public int commit()
	{
		//markLastTimeStamp();
		if (dbConnection != null)
		{
			try
			{
				dbConnection.commit() ;
				return 0;
			}
			catch (SQLException e)
			{
				LogSQLException.log(e);
				return e.getErrorCode();
			}
		}
		return 0;
	}
	
	
	/** Method added by Jilali Raki for WLC stored procedures
	 * 
	 * @param autoCommit
	 * @return
	 */
	public int setAutoCommit(boolean autoCommit)
	{
		//markLastTimeStamp();
		if (dbConnection != null)
		{
			try
			{
				dbConnection.setAutoCommit(autoCommit) ;
				return 0;
			}
			catch (SQLException e)
			{
				LogSQLException.log(e);
				return e.getErrorCode();
			}
		}
		return 0;
	}
	
	
	public SQLException rollBackWithException()
	{
		if (dbConnection != null)
		{
			try
			{
				dbConnection.rollback() ;
				return null;
			}
			catch (SQLException e)
			{
				LogSQLException.log(e);
				return e;
			}
		}
		return null;
	}
	
	public SQLException commitWithException()
	{
		if (dbConnection != null)
		{
			try
			{
				dbConnection.commit() ;
				return null;
			}
			catch (SQLException e)
			{
				LogSQLException.log(e);
				return e;
			}
		}
		return null;
	}
	
	public String getEnvironmentPrefix()
	{
		return csEnvironment ;
	}


	public boolean supportCursorName()
	{
		if(isuseRowId)
			return false;
		return true;
	}

	/**
	 * @param n
	 * @return
	 */

	public void Release()
	{
		if (dbConnection != null)
		{
			try
			{
				dbConnection.close() ;
			} 
			catch (SQLException e)
			{
				LogSQLException.log(e);
				e.printStackTrace();
			}
			dbConnection = null ;
		}
	}
	
	public void returnConnectionToPool()
	{
		if(dbConnectionColl != null)
			dbConnectionColl.releaseConnection(this);
	}
	
	String getPrefId()
	{
		return csPrefId;
	}
	
	void setUseExplain(boolean bUseExplain)
	{
		bUseExplain = bUseExplain;
	}
	
	public boolean getUseExplain()
	{
		return isuseExplain;
	}
	
	public Connection getDbConnection()
	{
		return dbConnection;		
	}
	
	
	private DbConnectionBaseJMXBean dbConnectionBaseJMXBean = null;
	
	void showHideJMXBean(boolean bToShow)
	{
		doShowHideJMXBean(bToShow);
	}
	
	synchronized void doShowHideJMXBean(boolean bToShow)
	{
		if(isuseJmx)
		{
			if(bToShow && !isBeanCreated())
			{
				dbConnectionBaseJMXBean = new DbConnectionBaseJMXBean(this); 
				dbConnectionBaseJMXBean.createMBean("Con_"+csUUID, csUUID);
			}
			else if(!bToShow && isBeanCreated())
			{
				dbConnectionBaseJMXBean.unregisterMBean();
				dbConnectionBaseJMXBean.cleanup();
				dbConnectionBaseJMXBean = null;
			}
		}
	}
	
	public void dumpConnections(StringBuilder sbText)
	{
		sbText.append("-------------------------------------------------------------------------\n");
		sbText.append("Connection: Con_"+csUUID+"\n;");
		sbText.append("    Last usage"+stopWatchLastUsage.getElapsedTime()+" ms\n;");
		sbText.append("    Statements:\n");
				
		Enumeration<String> eStsmt = hashStatement.keys();
		while(eStsmt.hasMoreElements())
		{
			String csStmt = eStsmt.nextElement();
			DbPreparedStatement statement = hashStatement.get(csStmt);
			long lastUsageTimeValue = statement.getLastUsageTimeValue();
			sbText.append("    " + lastUsageTimeValue + ";  " + csStmt + "\n");
		}
	}
			
	private synchronized boolean isBeanCreated()
	{
		if(dbConnectionBaseJMXBean == null)
			return false;
		return true;
	}
	
	public void setOnceUUID(String csConnId)
	{
		if(csUUID == null)
			csUUID = csConnId + "_" + Time_ms.getCurrentTime_ms() + "_" + Threadutil.getCurrentThreadId();
	}
	
	public String getUUID()
	{
		return csUUID; 
	}
	
	void createStmtJMXBeans(DbConnectionBaseJMXBean JMXBeanOwner, String csName, String csDescription)
	{
		if(hashStatement == null)
			return;
		
		int n = 0;
		Enumeration<String> eStsmt = hashStatement.keys();
		while(eStsmt.hasMoreElements())
		{
			String csStmt = eStsmt.nextElement();
			DbPreparedStatement statement = hashStatement.get(csStmt);
			long lastUsageTimeValue = statement.getLastUsageTimeValue();
			DbConnectionBaseStmtJMXBean dbConnectionBaseStmtJMXBean = new DbConnectionBaseStmtJMXBean(csStmt, lastUsageTimeValue);
			dbConnectionBaseStmtJMXBean.createMBean(csName + "_" + lastUsageTimeValue, csDescription);
			JMXBeanOwner.add(dbConnectionBaseStmtJMXBean);
			n++;
		}	
	}
}
