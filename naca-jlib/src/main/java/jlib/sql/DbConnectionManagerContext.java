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
 *     ion $Id: DbConnectionManagerContext.java,v 1.9 2008/07/09 06:40:18 u930di Exp $
 */
public class DbConnectionManagerContext
{
    private String dbProvider = null;
    private String dbUrl = null;
    private String dbUser = null;
    private String dbPassword = null;
    private String environment = null;
    private int nNbMaxConnections = 0;
    private int timeBeforeRemoveConnectionMillis = 0;
    private int maxStatementLiveTimeMillis = 0;
    private boolean iscreated = false;

    private DbConnectionManagerBase dbConnectionManager = null;

    /** Creates a new db connection manager context instance. */
    public  DbConnectionManagerContext()
    {
    }

    /** Executes the create operation. */
    public boolean create(String csPropertyPrefix)
        throws TechnicalException
    {
        if (!csPropertyPrefix.endsWith(".")) {
            csPropertyPrefix += ".";
        }

        PropertyLoader pl = new PropertyLoader();
        dbProvider = pl.getProperty(csPropertyPrefix + "driver");
        dbUrl = pl.getProperty(csPropertyPrefix + "connectionString");
        dbUser = pl.getProperty(csPropertyPrefix + "user");
        dbPassword = pl.getProperty(csPropertyPrefix + "password");
        environment = pl.getProperty(csPropertyPrefix + "environment", "");

        String cs = pl.getProperty(csPropertyPrefix + "NbMaxConnections", "2");
        nNbMaxConnections = NumberParser.getAsInt(cs);

        cs = pl.getProperty(csPropertyPrefix + "TimeBeforeRemoveConnection_ms", "600000");  // 10 minutes by defaut
        timeBeforeRemoveConnectionMillis = NumberParser.getAsInt(cs);

        cs = pl.getProperty(csPropertyPrefix + "MaxStatementLiveTime_ms", "600000");    // 10 minutes by defaut
        maxStatementLiveTimeMillis = NumberParser.getAsInt(cs);

        iscreated = doCreateConnection(csPropertyPrefix);

        return iscreated;
    }

    /** Executes the create operation. */
    public boolean create(String csDBProvider, String csUrl, String csUser, String csPassword, String csEnvironment)
        throws TechnicalException
    {
        this.dbProvider = csDBProvider;
        dbUrl = csUrl;
        dbUser = csUser;
        dbPassword = csPassword;
        this.environment = csEnvironment;

        nNbMaxConnections = 2;      // Resonable default values; Should be parametrized ???
        timeBeforeRemoveConnectionMillis = 10 * 60 * 1000; // 10 minutes
        maxStatementLiveTimeMillis = 10 * 60 * 1000;  // 10 minutes too

        iscreated = doCreateConnection("");
        return iscreated;
    }

    private boolean doCreateConnection(String propertyPrefix)
        throws TechnicalException
    {
        dbConnectionManager = new DbConnectionManager();
        dbConnectionManager.setPropertyPrefix(propertyPrefix);
        try
        {
            iscreated = dbConnectionManager.create(
                dbUser,
                dbPassword,
                dbUrl,
                dbProvider,
                nNbMaxConnections,
                timeBeforeRemoveConnectionMillis,
                maxStatementLiveTimeMillis);
            if (iscreated) {
                dbConnectionManager.setEnvironment(environment);
            }
            return iscreated;
        }
        catch (TechnicalException e) {
            throw e;
        }
        catch (RuntimeException e)
        {
            iscreated = false;
            TechnicalException.throwException(TechnicalException.DB_ERROR_CONNECTION_CREATION, "Could not create DB connection", e);
        }
        return iscreated;
    }

    /** Returns whether oracle. */
    public boolean isOracle()
    {
        if (dbProvider.equalsIgnoreCase("Oracle")) {
            return true;
        }
        return false;
    }

    /** Returns the connection. */
    public DbConnectionBase getConnection()
    {
        if (dbConnectionManager == null) {
            return null;
        }

        try
        {
            DbConnectionBase connection = dbConnectionManager.getConnection(true);
            return connection;
        }
        catch (DbConnectionException e)
        {
            TechnicalException.throwException(
                TechnicalException.DB_ERROR_CONNECTION_CREATION,
                "Could not create DB connection (getConnection())",
                e);
        }
        return null;
    }

    public boolean isCreated()
    {
        return iscreated;
    }

    /**
     * @function getNbUnusedConnections
     * @return Number of currently unused connection
     */
    public int getNbUnusedConnections()
    {
        if (dbConnectionManager == null) {
            return 0;
        }
        return dbConnectionManager.getNbUnusedConnections();
    }

    /** Returns the nb running connections. */
    public int getNbRunningConnections()
    {
        if (dbConnectionManager == null) {
            return 0;
        }
        return dbConnectionManager.getNbRunningConnections();
    }

    /** Executes the show hide running connections operation. */
    public void showHideRunningConnections(boolean bShowRunningCon)
    {
        if (dbConnectionManager != null) {
            dbConnectionManager.showHideRunningConnections(bShowRunningCon);
        }
    }

    /** Executes the dump connections operation. */
    public void dumpConnections(StringBuilder sbText)
    {
        if (dbConnectionManager != null) {
            dbConnectionManager.dumpConnections(sbText);
        }
    }

    /** Returns the nb alloc connnections. */
    public int getNbAllocConnnections()
    {
        if (dbConnectionManager == null) {
            return 0;
        }
        return dbConnectionManager.getNbAllocConnnections();
    }

    /** Returns the nb max connection. */
    public int getNbMaxConnection()
    {
        if (dbConnectionManager == null) {
            return 0;
        }
        return dbConnectionManager.getNbMaxConnection();
    }

    /** Returns the nb cached statements for accessor. */
    public int getNbCachedStatementsForAccessor()
    {
        if (dbConnectionManager == null) {
            return 0;
        }
        return dbConnectionManager.getNbCachedStatementsForAccessor();
    }
}
