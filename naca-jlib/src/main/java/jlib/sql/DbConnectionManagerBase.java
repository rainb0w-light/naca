/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.sql.Connection;
import java.util.Properties;

import jlib.exception.TechnicalException;
import jlib.misc.ListCoupleRender;
import jlib.misc.StringUtil;
import jlib.xml.Tag;

public abstract class DbConnectionManagerBase
{
	DbConnectionParam dbConnectionParam = null;
	private DbConnectionPool qLConnectionPool = null;
	private DbDataCacheManager cacheManager = null;
	private String csPropertyPrefix = "";
	
	public DbConnectionManagerBase()
	{
		dbConnectionParam = new DbConnectionParam();
		cacheManager = new DbDataCacheManager() ;
	}
	
	public Object getCachedData(String table, String key)
	{
		return cacheManager.getData(table.toUpperCase(), key);
	}

	public void recordCachedData(String table, String key, Object value)
	{
		cacheManager.RegisterData(table.toUpperCase(), key, value);
	}
	
	public DbConnectionBase getConnection() throws DbConnectionException
	{
		return getConnection("", null, false);
	}
	
	public DbConnectionBase getConnection(boolean bUseStatementCache) throws DbConnectionException
	{
		return getConnection("", null, bUseStatementCache);
	}

	public DbConnectionBase getConnection(String csProgramId, boolean bUseStatementCache) throws DbConnectionException
	{
		return getConnection(csProgramId, null, bUseStatementCache);
	}
	
	public DbConnectionBase getConnection(String csProgramId, String csProgramParent, boolean bUseStatementCache) throws DbConnectionException
	{
		DbConnectionColl connectionColl = qLConnectionPool.getConnectionCollForPref(csProgramId, csProgramParent);
		if(connectionColl != null)
		{
			if(!connectionColl.isInit())
				connectionColl.init(dbConnectionParam);
			String csPoolName = connectionColl.getName();
	 		DbConnectionBase sqlConnection = connectionColl.tryGetPooledValidConnection(csValidationQuery, csPoolName, bUseStatementCache, this);
	 		return sqlConnection;
		}
		return null;
	}
	
	public DbConnectionBase getNewConnection() throws DbConnectionException
	{
		return getNewConnection("", null, false);
	}

	public DbConnectionBase getNewConnection(boolean bUseStatementCache) throws DbConnectionException
	{
		return getNewConnection("", null, bUseStatementCache);
	}
	
	public DbConnectionBase getNewConnection(String csProgramId, boolean bUseStatementCache) throws DbConnectionException
	{
		return getNewConnection(csProgramId, null, bUseStatementCache);
	}
	
	public DbConnectionBase getNewConnection(String csProgramId, String csParentProgramId, boolean bUseStatementCache) throws DbConnectionException
	{
		DbConnectionColl connectionColl = qLConnectionPool.getConnectionCollForPref(csProgramId, csParentProgramId);
		if(connectionColl != null)
		{
			if(!connectionColl.isInit())
				connectionColl.init(dbConnectionParam);
			String csPoolName = connectionColl.getName();
	 		DbConnectionBase sqlConnection = connectionColl.forceNewConnection(csValidationQuery, csPoolName, bUseStatementCache, this);
	 		return sqlConnection;
		}
		return null;
	}
	
	public abstract DbConnectionBase createConnection(Connection connection, String csPrefId, String csEnvironment, boolean bUseStatementCache, boolean bUseJmx, DbDriverId dbDriver);
	
	public void returnConnection(DbConnectionBase SQLConnection)
	{
		qLConnectionPool.releaseConnection(SQLConnection);
	}
	
	public DbConnectionPool init(String csDBParameterPrefix, Tag tagSQLConfig)
	{
		dbConnectionParam.csUrl = tagSQLConfig.getVal(csDBParameterPrefix+"dburl");
		dbConnectionParam.setEnvironment(tagSQLConfig.getVal(csDBParameterPrefix+"dbenvironment"));
		dbConnectionParam.csPackage = tagSQLConfig.getVal(csDBParameterPrefix+"dbpackage");
		csValidationQuery = tagSQLConfig.getVal("validationQuery");
		dbConnectionParam.iscloseCursorOnCommit = tagSQLConfig.getValAsBoolean(csDBParameterPrefix+"CloseCursorOnCommit");
		dbConnectionParam.isautoCommit = tagSQLConfig.getValAsBoolean("AutoCommit");
		
		String csDriverClass = tagSQLConfig.getVal(csDBParameterPrefix+"driverClass");
		String csConnectionUrlOptionalParams = tagSQLConfig.getVal(csDBParameterPrefix+"dbConnectionUrlOptionalParams");

		String csUser = tagSQLConfig.getVal(csDBParameterPrefix+"dbuser");
		String csCryptedDbPassword = tagSQLConfig.getVal(csDBParameterPrefix+"CryptedDbpassword");
		String csCryptKey = tagSQLConfig.getVal(csDBParameterPrefix+"CryptKey");
		if(!StringUtil.isEmpty(csCryptedDbPassword) && !StringUtil.isEmpty(csCryptKey))
			createDriver(csDriverClass, csUser, csCryptedDbPassword, csCryptKey, csConnectionUrlOptionalParams);
		else
		{
			String csPassword = tagSQLConfig.getVal(csDBParameterPrefix+"dbpassword");
			createDriver(csDriverClass, csUser, csPassword, csConnectionUrlOptionalParams);
		}
		
		qLConnectionPool = new DbConnectionPool(tagSQLConfig);
		return qLConnectionPool;
	}
	
	public boolean initDB2(String csUrl, String csUser, String csPassword, String csConnectionUrlOptionalParams, int nNbMaxConnections, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms, int nGarbageCollectorStatement_ms)
	{
		String csDriverClass = "com.ibm.db2.jcc.DB2Driver";
		return initDriverClass(csUrl, csUser, csPassword, csDriverClass, csConnectionUrlOptionalParams, nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, nGarbageCollectorStatement_ms);
	}
	
	public boolean initOracle(String csUrl, String csUser, String csPassword, String csConnectionUrlOptionalParams, int nNbMaxConnections, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms, int nGarbageCollectorStatement_ms)
	{
		String csDriverClass = "oracle.jdbc.driver.OracleDriver";
		return initDriverClass(csUrl, csUser, csPassword, csDriverClass, csConnectionUrlOptionalParams, nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, nGarbageCollectorStatement_ms);
	}
	
	public boolean initMySql(String csUrl, String csUser, String csPassword, String csConnectionUrlOptionalParams, int nNbMaxConnections, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms, int nGarbageCollectorStatement_ms)
	{
		String csDriverClass = "com.mysql.jdbc.Driver";
		return initDriverClass(csUrl, csUser, csPassword, csDriverClass, csConnectionUrlOptionalParams, nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, nGarbageCollectorStatement_ms);
	}
	
	public boolean initSqlServer(String csUrl, String csUser, String csPassword, String csConnectionUrlOptionalParams, int nNbMaxConnections, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms, int nGarbageCollectorStatement_ms)
	{
		String csDriverClass = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		return initDriverClass(csUrl, csUser, csPassword, csDriverClass, csConnectionUrlOptionalParams, nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, nGarbageCollectorStatement_ms);
	}
	
	public boolean initDriverClass(String csUrl, String csUser, String csPassword, String csDriverClass, String csConnectionUrlOptionalParams, int nNbMaxConnections, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms, int nGarbageCollectorStatement_ms)
	{
		if(csDriverClass.indexOf("oracle") != -1)	// Oracle doesn't support SetCloseCursorOnCommit 
			iscanSetCloseCursorOnCommit = false;
		else
			iscanSetCloseCursorOnCommit = true;
		dbConnectionParam.csUrl = csUrl;

		boolean b = createDriver(csDriverClass, csUser, csPassword, csConnectionUrlOptionalParams);
		if(b)
			qLConnectionPool = new DbConnectionPool("UnknownPoolName", nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, nGarbageCollectorStatement_ms);
		return b;
	}
	
	public void setAutoCommit(boolean bAutoCommit)
	{
		dbConnectionParam.isautoCommit = bAutoCommit;
	}
	
	public void setCloseCursorOnCommit(boolean bCloseCursorOnCommit)
	{
		if(iscanSetCloseCursorOnCommit)
			bCloseCursorOnCommit = bCloseCursorOnCommit;
	}
	
	protected boolean createDriver(String csDriverClass, String csUser, String csPassword, String csConnectionUrlOptionalParams)
	{
		dbConnectionParam.propertiesUserPassword = new Properties();
		dbConnectionParam.propertiesUserPassword.setProperty("user", csUser);
		dbConnectionParam.propertiesUserPassword.setProperty("password", csPassword); 
		dbConnectionParam.csConnectionUrlOptionalParams = csConnectionUrlOptionalParams;
	    try 
		{
	    	dbConnectionParam.driver = (java.sql.Driver)Class.forName(csDriverClass).newInstance();
//	    	if(m_DbConnectionParam.driver != null)
//	    		Log.logNormal("Created driver " + csDriverClass + " for user " + csUser);
	    } 
	    catch (Exception e) 
		{
	    	TechnicalException.throwException(TechnicalException.DB_ERROR_DRIVER_CREATION, "Could not initialize database driver '"+csDriverClass+"'.", e);
	    }
//	    if(m_DbConnectionParam.driver == null)
//	    	Log.logImportant("Could not create driver " + csDriverClass + " for user " + csUser);
	    return true;
	}
	
	protected boolean createDriver(String csDriverClass, String csUser, String csCryptedPassword, String csCryptKey, String csConnectionUrlOptionalParams)
	{
		dbConnectionParam.propertiesUserPassword = new Properties();
		dbConnectionParam.propertiesUserPassword.setProperty("user", csUser);
		dbConnectionParam.propertiesUserPassword.setProperty("CryptedPassword", csCryptedPassword);
		dbConnectionParam.propertiesUserPassword.setProperty("CryptKey", csCryptKey);
		dbConnectionParam.csConnectionUrlOptionalParams = csConnectionUrlOptionalParams;
	    try 
		{
	    	dbConnectionParam.driver = (java.sql.Driver)Class.forName(csDriverClass).newInstance();
//	    	if(m_DbConnectionParam.driver != null)
//	    		Log.logNormal("Created driver " + csDriverClass + " for user " + csUser);
	    } 
	    catch (Exception ex) 
		{
	    	String csParams = ListCoupleRender.set("Parameters: ").set("DriverClass", csDriverClass).set("User", csUser).set("CryptedPassword", csCryptedPassword).toString();
	    	TechnicalException.throwException(TechnicalException.DB_ERROR_DRIVER_CREATION, csParams, ex);
	    }
//	    if(m_DbConnectionParam.driver == null)
//	    	Log.logImportant("Could not create driver " + csDriverClass + " for user " + csUser);
	    return true;
	}

	public boolean create(String csDBUser, String csDBPassword, String csDBUrl, String csDBProvider, int nNbMaxConnections, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms)
	{
		dbConnectionParam.csUrl = csDBUrl;
		String csDriverClass = null;
		if(csDBProvider.equalsIgnoreCase("DB2"))
			csDriverClass = "com.ibm.db2.jcc.DB2Driver"; 
		else if(csDBProvider.equalsIgnoreCase("Oracle"))
			csDriverClass = "oracle.jdbc.driver.OracleDriver"; 
		else if(csDBProvider.equalsIgnoreCase("MySQL"))
			csDriverClass = "com.mysql.jdbc.Driver";
		else if(csDBProvider.equalsIgnoreCase("SqlServer"))
			csDriverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		else 
			csDriverClass = csDBProvider;
		
		boolean b = createDriver(csDriverClass, csDBUser, csDBPassword, "");
		if(b)
		{
			//Log.logNormal("Created DB driver " + csDriverClass + " for user " + csDBUser + " on url "+csDBUrl);
			qLConnectionPool = new DbConnectionPool("UnknownPoolName", nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, 0);
		}
//		else
//			Log.logImportant("Could not create DB driver " + csDriverClass + " for user " + csDBUser + " on url "+csDBUrl);		
		
		return b;		
	}
	
	public void setEnvironment(String csEnvironment)
	{
		dbConnectionParam.setEnvironment(csEnvironment);
	}
	
	public void setValidationQuery(String csValidationQuery)
	{
		csValidationQuery = csValidationQuery;
	}
	
	public int getNbUnusedConnections()
	{
		if(qLConnectionPool == null)
			return 0;
		return qLConnectionPool.getNbUnusedConnections();
	}
	
	public int getNbRunningConnections()
	{
		if(qLConnectionPool == null)
			return 0;
		return qLConnectionPool.getNbRunningConnections();
	}	
	
	public void showHideRunningConnections(boolean bShowRunningCon)
	{
		if(qLConnectionPool != null)
			qLConnectionPool.showHideRunningConnections(bShowRunningCon);
	}
	
	public void dumpConnections(StringBuilder sbText)
	{
		if(qLConnectionPool != null)
			qLConnectionPool.dumpConnections(sbText);
	}
	
	public int getNbCachedStatementsForAccessor()
	{
		if(qLConnectionPool == null)
			return 0;
		return qLConnectionPool.getNbCachedStatementsForAccessor();
	}
	
	public int getNbAllocConnnections()
	{
		if(qLConnectionPool == null)
			return 0;
		return qLConnectionPool.getNbAllocConnnections();
	}
	
	public int getNbMaxConnection()
	{
		if(qLConnectionPool == null)
			return 0;
		return qLConnectionPool.getNbMaxConnection();
	}
	
	
	
	protected int maxWaitTime_s = 60 ;
	protected String csValidationQuery = "" ;
	private boolean iscloseCursorOnCommit = false;
	private boolean iscanSetCloseCursorOnCommit = false;	// Oracle cannot set CloseCursorOnCommit, but DB2 can do it

	/**
	 * @return the csPropertyName
	 */
	public String getPropertyPrefix()
	{
		return csPropertyPrefix;
	}

	/**
	 * @param propertyName the csPropertyName to set
	 */
	public void setPropertyPrefix(String csPropertyPrefix)
	{
		csPropertyPrefix = csPropertyPrefix;
	}
}
