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

import jlib.Helpers.PropertyLoader;
import jlib.exception.TechnicalException;
import jlib.misc.NumberParser;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @vers
 * ion $Id: DbConnectionManagerContext.java,v 1.9 2008/07/09 06:40:18 u930di Exp $
 */
public class DbConnectionManagerContext
{
	private String csDBProvider = null;
	private String csDBUrl = null;
	private String csDBUser = null;
	private String csDBPassword = null;
	private String csEnvironment = null;
	private int nNbMaxConnections = 0;
	private int nTimeBeforeRemoveConnection_ms = 0;
	private int nMaxStatementLiveTime_ms = 0;
	private boolean bCreated = false;	
	
	private DbConnectionManagerBase dbConnectionManager = null;
	
	public  DbConnectionManagerContext()
	{
	}
	
	public boolean create(String csPropertyPrefix)
		throws TechnicalException
	{
		if(!csPropertyPrefix.endsWith("."))
			csPropertyPrefix += ".";

		PropertyLoader pl = new PropertyLoader();
		csDBProvider = pl.getProperty(csPropertyPrefix + "driver");
		csDBUrl = pl.getProperty(csPropertyPrefix + "connectionString");
		csDBUser = pl.getProperty(csPropertyPrefix + "user");
		csDBPassword = pl.getProperty(csPropertyPrefix + "password");
		csEnvironment = pl.getProperty(csPropertyPrefix + "environment", "");
					
		String cs = pl.getProperty(csPropertyPrefix + "NbMaxConnections", "2");
		nNbMaxConnections = NumberParser.getAsInt(cs);
		
		cs = pl.getProperty(csPropertyPrefix + "TimeBeforeRemoveConnection_ms", "600000");	// 10 minutes by defaut
		nTimeBeforeRemoveConnection_ms = NumberParser.getAsInt(cs);
		
		cs = pl.getProperty(csPropertyPrefix + "MaxStatementLiveTime_ms", "600000");	// 10 minutes by defaut
		nMaxStatementLiveTime_ms = NumberParser.getAsInt(cs);
		
		bCreated = doCreateConnection(csPropertyPrefix);

		return bCreated;
	}
	
	public boolean create(String csDBProvider, String csUrl, String csUser, String csPassword, String csEnvironment)
		throws TechnicalException
	{
		csDBProvider = csDBProvider;
		csDBUrl = csUrl;
		csDBUser = csUser;
		csDBPassword = csPassword;
		csEnvironment = csEnvironment;
		
		nNbMaxConnections = 2;		// Resonable default values; Should be parametrized ???
		nTimeBeforeRemoveConnection_ms = 10 * 60 * 1000; // 10 minutes
		nMaxStatementLiveTime_ms = 10 * 60 * 1000; 	// 10 minutes too
		
		bCreated = doCreateConnection("");
		return bCreated;
	}

	private boolean doCreateConnection(String csPropertyPrefix)
		throws TechnicalException
	{		
		dbConnectionManager = new DbConnectionManager();
		dbConnectionManager.setPropertyPrefix(csPropertyPrefix);
		try
		{
			bCreated = dbConnectionManager.create(csDBUser, csDBPassword, csDBUrl, csDBProvider, nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms);
			if(bCreated)
				dbConnectionManager.setEnvironment(csEnvironment);
			return bCreated;
		}
		catch (TechnicalException e) {
			throw e;
		}
		catch (RuntimeException e)
		{
			bCreated = false;
			TechnicalException.throwException(TechnicalException.DB_ERROR_CONNECTION_CREATION, "Could not create DB connection", e);
		}
		return bCreated;
	}
	
	public boolean isOracle()
	{
		if(csDBProvider.equalsIgnoreCase("Oracle"))
			return true;
		return false;
	}
	
	public DbConnectionBase getConnection()
	{
		if(dbConnectionManager == null)
			return null;
		
		try
		{
			DbConnectionBase connection = dbConnectionManager.getConnection(true);
			return connection;
		}
		catch (DbConnectionException e)
		{
			TechnicalException.throwException(TechnicalException.DB_ERROR_CONNECTION_CREATION, "Could not create DB connection (getConnection())", e);
		}
		return null;
	}
	
	public boolean isCreated()
	{
		return bCreated;
	}
	
	/**
	 * @function getNbUnusedConnections
	 * @return Number of currently unused connection
	 */
	public int getNbUnusedConnections()
	{
		if(dbConnectionManager == null)
			return 0;
		return dbConnectionManager.getNbUnusedConnections();		
	}
	
	public int getNbRunningConnections()
	{
		if(dbConnectionManager == null)
			return 0;
		return dbConnectionManager.getNbRunningConnections();		
	}
	
	public void showHideRunningConnections(boolean bShowRunningCon)
	{
		if(dbConnectionManager != null)
			dbConnectionManager.showHideRunningConnections(bShowRunningCon);
	}
	
	public void dumpConnections(StringBuilder sbText)
	{
		if(dbConnectionManager != null)
			dbConnectionManager.dumpConnections(sbText);
	}
	
	public int getNbAllocConnnections()
	{
		if(dbConnectionManager == null)
			return 0;
		return dbConnectionManager.getNbAllocConnnections();		
	}
	
	public int getNbMaxConnection()
	{
		if(dbConnectionManager == null)
			return 0;
		return dbConnectionManager.getNbMaxConnection();
	}
	
	public int getNbCachedStatementsForAccessor()
	{
		if(dbConnectionManager == null)
			return 0;
		return dbConnectionManager.getNbCachedStatementsForAccessor();
	}
}
