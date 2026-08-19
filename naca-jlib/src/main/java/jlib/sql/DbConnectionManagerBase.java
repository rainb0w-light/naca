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

/** Provides db connection manager base behavior. */
public abstract class DbConnectionManagerBase
{
    DbConnectionParam dbConnectionParam = null;
    private DbConnectionPool qLConnectionPool = null;
    private DbDataCacheManager cacheManager = null;
    private String propertyPrefix = "";

    /** Creates a new db connection manager base instance. */
    public DbConnectionManagerBase()
    {
        dbConnectionParam = new DbConnectionParam();
        cacheManager = new DbDataCacheManager() ;
    }

    /** Returns the cached data. */
    public Object getCachedData(String table, String key)
    {
        return cacheManager.getData(table.toUpperCase(), key);
    }

    /** Executes the record cached data operation. */
    public void recordCachedData(String table, String key, Object value)
    {
        cacheManager.RegisterData(table.toUpperCase(), key, value);
    }

    /** Returns the connection. */
    public DbConnectionBase getConnection() throws DbConnectionException
    {
        return getConnection("", null, false);
    }

    /** Returns the connection. */
    public DbConnectionBase getConnection(boolean bUseStatementCache) throws DbConnectionException
    {
        return getConnection("", null, bUseStatementCache);
    }

    /** Returns the connection. */
    public DbConnectionBase getConnection(String csProgramId, boolean bUseStatementCache) throws DbConnectionException
    {
        return getConnection(csProgramId, null, bUseStatementCache);
    }

    /** Returns the connection. */
    public DbConnectionBase getConnection(
        String csProgramId,
        String csProgramParent,
        boolean bUseStatementCache) throws DbConnectionException
    {
        DbConnectionColl connectionColl = qLConnectionPool.getConnectionCollForPref(csProgramId, csProgramParent);
        if(connectionColl != null)
        {
            if (!connectionColl.isInit()) {
                connectionColl.init(dbConnectionParam);
            }
            String poolName = connectionColl.getName();
            DbConnectionBase sqlConnection = connectionColl.tryGetPooledValidConnection(
                csValidationQuery,
                poolName,
                bUseStatementCache,
                this);
            return sqlConnection;
        }
        return null;
    }

    /** Returns the new connection. */
    public DbConnectionBase getNewConnection() throws DbConnectionException
    {
        return getNewConnection("", null, false);
    }

    /** Returns the new connection. */
    public DbConnectionBase getNewConnection(boolean bUseStatementCache) throws DbConnectionException
    {
        return getNewConnection("", null, bUseStatementCache);
    }

    /** Returns the new connection. */
    public DbConnectionBase getNewConnection(String csProgramId, boolean bUseStatementCache) throws DbConnectionException
    {
        return getNewConnection(csProgramId, null, bUseStatementCache);
    }

    /** Returns the new connection. */
    public DbConnectionBase getNewConnection(
        String csProgramId,
        String csParentProgramId,
        boolean bUseStatementCache) throws DbConnectionException
    {
        DbConnectionColl connectionColl = qLConnectionPool.getConnectionCollForPref(csProgramId, csParentProgramId);
        if(connectionColl != null)
        {
            if (!connectionColl.isInit()) {
                connectionColl.init(dbConnectionParam);
            }
            String poolName = connectionColl.getName();
            DbConnectionBase sqlConnection = connectionColl.forceNewConnection(csValidationQuery, poolName, bUseStatementCache, this);
            return sqlConnection;
        }
        return null;
    }

    /** Creates the connection. */
    public abstract DbConnectionBase createConnection(
        Connection connection,
        String csPrefId,
        String csEnvironment,
        boolean bUseStatementCache,
        boolean bUseJmx,
        DbDriverId dbDriver);

    /** Executes the return connection operation. */
    public void returnConnection(DbConnectionBase sqlConnection)
    {
        qLConnectionPool.releaseConnection(sqlConnection);
    }

    /** Executes the init operation. */
    public DbConnectionPool init(String csDBParameterPrefix, Tag tagSQLConfig)
    {
        dbConnectionParam.csUrl = tagSQLConfig.getVal(csDBParameterPrefix+"dburl");
        dbConnectionParam.setEnvironment(tagSQLConfig.getVal(csDBParameterPrefix+"dbenvironment"));
        dbConnectionParam.csPackage = tagSQLConfig.getVal(csDBParameterPrefix+"dbpackage");
        csValidationQuery = tagSQLConfig.getVal("validationQuery");
        dbConnectionParam.iscloseCursorOnCommit = tagSQLConfig.getValAsBoolean(csDBParameterPrefix+"CloseCursorOnCommit");
        dbConnectionParam.isautoCommit = tagSQLConfig.getValAsBoolean("AutoCommit");

        String driverClass = tagSQLConfig.getVal(csDBParameterPrefix+"driverClass");
        String connectionUrlOptionalParams = tagSQLConfig.getVal(csDBParameterPrefix+"dbConnectionUrlOptionalParams");

        String user = tagSQLConfig.getVal(csDBParameterPrefix+"dbuser");
        String cryptedDbPassword = tagSQLConfig.getVal(csDBParameterPrefix+"CryptedDbpassword");
        String cryptKey = tagSQLConfig.getVal(csDBParameterPrefix+"CryptKey");
        if (!StringUtil.isEmpty(cryptedDbPassword) && !StringUtil.isEmpty(cryptKey)) {
            createDriver(driverClass, user, cryptedDbPassword, cryptKey, connectionUrlOptionalParams);
        } else
        {
            String password = tagSQLConfig.getVal(csDBParameterPrefix + "dbpassword");
            createDriver(driverClass, user, password, connectionUrlOptionalParams);
        }

        qLConnectionPool = new DbConnectionPool(tagSQLConfig);
        return qLConnectionPool;
    }

    /** Executes the init db2 operation. */
    public boolean initDB2(
        String csUrl,
        String csUser,
        String csPassword,
        String csConnectionUrlOptionalParams,
        int nNbMaxConnections,
        int nTimeBeforeRemoveConnectionMs,
        int nMaxStatementLiveTimeMs,
        int nGarbageCollectorStatementMs)
    {
        String driverClass = "com.ibm.db2.jcc.DB2Driver";
        return initDriverClass(
            csUrl,
            csUser,
            csPassword,
            driverClass,
            csConnectionUrlOptionalParams,
            nNbMaxConnections,
            nTimeBeforeRemoveConnectionMs,
            nMaxStatementLiveTimeMs,
            nGarbageCollectorStatementMs);
    }

    /** Executes the init oracle operation. */
    public boolean initOracle(
        String csUrl,
        String csUser,
        String csPassword,
        String csConnectionUrlOptionalParams,
        int nNbMaxConnections,
        int nTimeBeforeRemoveConnectionMs,
        int nMaxStatementLiveTimeMs,
        int nGarbageCollectorStatementMs)
    {
        String driverClass = "oracle.jdbc.driver.OracleDriver";
        return initDriverClass(
            csUrl,
            csUser,
            csPassword,
            driverClass,
            csConnectionUrlOptionalParams,
            nNbMaxConnections,
            nTimeBeforeRemoveConnectionMs,
            nMaxStatementLiveTimeMs,
            nGarbageCollectorStatementMs);
    }

    /** Executes the init my sql operation. */
    public boolean initMySql(
        String csUrl,
        String csUser,
        String csPassword,
        String csConnectionUrlOptionalParams,
        int nNbMaxConnections,
        int nTimeBeforeRemoveConnectionMs,
        int nMaxStatementLiveTimeMs,
        int nGarbageCollectorStatementMs)
    {
        String driverClass = "com.mysql.jdbc.Driver";
        return initDriverClass(
            csUrl,
            csUser,
            csPassword,
            driverClass,
            csConnectionUrlOptionalParams,
            nNbMaxConnections,
            nTimeBeforeRemoveConnectionMs,
            nMaxStatementLiveTimeMs,
            nGarbageCollectorStatementMs);
    }

    /** Executes the init sql server operation. */
    public boolean initSqlServer(
        String csUrl,
        String csUser,
        String csPassword,
        String csConnectionUrlOptionalParams,
        int nNbMaxConnections,
        int nTimeBeforeRemoveConnectionMs,
        int nMaxStatementLiveTimeMs,
        int nGarbageCollectorStatementMs)
    {
        String driverClass = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        return initDriverClass(
            csUrl,
            csUser,
            csPassword,
            driverClass,
            csConnectionUrlOptionalParams,
            nNbMaxConnections,
            nTimeBeforeRemoveConnectionMs,
            nMaxStatementLiveTimeMs,
            nGarbageCollectorStatementMs);
    }

    /** Executes the init driver class operation. */
    public boolean initDriverClass(
        String csUrl,
        String csUser,
        String csPassword,
        String csDriverClass,
        String csConnectionUrlOptionalParams,
        int nNbMaxConnections,
        int nTimeBeforeRemoveConnectionMs,
        int nMaxStatementLiveTimeMs,
        int nGarbageCollectorStatementMs)
    {
        if (csDriverClass.indexOf("oracle") != -1) {   // Oracle doesn't support SetCloseCursorOnCommit
            iscanSetCloseCursorOnCommit = false;
        } else {
            iscanSetCloseCursorOnCommit = true;
        }
        dbConnectionParam.csUrl = csUrl;

        boolean b = createDriver(csDriverClass, csUser, csPassword, csConnectionUrlOptionalParams);
        if (b) {
            qLConnectionPool = new DbConnectionPool(
                    "UnknownPoolName",
                    nNbMaxConnections,
                    nTimeBeforeRemoveConnectionMs,
                    nMaxStatementLiveTimeMs,
                    nGarbageCollectorStatementMs);
        }
        return b;
    }

    public void setAutoCommit(boolean bAutoCommit)
    {
        dbConnectionParam.isautoCommit = bAutoCommit;
    }

    /** Sets the close cursor on commit. */
    public void setCloseCursorOnCommit(boolean bCloseCursorOnCommit)
    {
        if (iscanSetCloseCursorOnCommit) {
            dbConnectionParam.iscloseCursorOnCommit = bCloseCursorOnCommit;
        }
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
//          if(m_DbConnectionParam.driver != null)
//              Log.logNormal("Created driver " + csDriverClass + " for user " + csUser);
        }
        catch (Exception e)
        {
            TechnicalException.throwException(
                TechnicalException.DB_ERROR_DRIVER_CREATION,
                "Could not initialize database driver '"+csDriverClass+"'.",
                e);
        }
//      if(m_DbConnectionParam.driver == null)
//          Log.logImportant("Could not create driver " + csDriverClass + " for user " + csUser);
        return true;
    }

    protected boolean createDriver(
        String csDriverClass,
        String csUser,
        String csCryptedPassword,
        String csCryptKey,
        String csConnectionUrlOptionalParams)
    {
        dbConnectionParam.propertiesUserPassword = new Properties();
        dbConnectionParam.propertiesUserPassword.setProperty("user", csUser);
        dbConnectionParam.propertiesUserPassword.setProperty("CryptedPassword", csCryptedPassword);
        dbConnectionParam.propertiesUserPassword.setProperty("CryptKey", csCryptKey);
        dbConnectionParam.csConnectionUrlOptionalParams = csConnectionUrlOptionalParams;
        try
        {
            dbConnectionParam.driver = (java.sql.Driver)Class.forName(csDriverClass).newInstance();
//          if(m_DbConnectionParam.driver != null)
//              Log.logNormal("Created driver " + csDriverClass + " for user " + csUser);
        }
        catch (Exception ex)
        {
            String params = ListCoupleRender.set("Parameters: ").set("DriverClass", csDriverClass).set(
                "User",
                csUser).set("CryptedPassword", csCryptedPassword).toString();
            TechnicalException.throwException(TechnicalException.DB_ERROR_DRIVER_CREATION, params, ex);
        }
//      if(m_DbConnectionParam.driver == null)
//          Log.logImportant("Could not create driver " + csDriverClass + " for user " + csUser);
        return true;
    }

    /** Executes the create operation. */
    public boolean create(
        String csDBUser,
        String csDBPassword,
        String csDBUrl,
        String csDBProvider,
        int nNbMaxConnections,
        int nTimeBeforeRemoveConnectionMs,
        int nMaxStatementLiveTimeMs)
    {
        dbConnectionParam.csUrl = csDBUrl;
        String driverClass = null;
        if (csDBProvider.equalsIgnoreCase("DB2")) {
            driverClass = "com.ibm.db2.jcc.DB2Driver";
        } else if (csDBProvider.equalsIgnoreCase("Oracle")) {
            driverClass = "oracle.jdbc.driver.OracleDriver";
        } else if (csDBProvider.equalsIgnoreCase("MySQL")) {
            driverClass = "com.mysql.jdbc.Driver";
        } else if (csDBProvider.equalsIgnoreCase("SqlServer")) {
            driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else {
            driverClass = csDBProvider;
        }

        boolean b = createDriver(driverClass, csDBUser, csDBPassword, "");
        if(b)
        {
            //Log.logNormal("Created DB driver " + csDriverClass + " for user " + csDBUser + " on url "+csDBUrl);
            qLConnectionPool = new DbConnectionPool(
                "UnknownPoolName",
                nNbMaxConnections,
                nTimeBeforeRemoveConnectionMs,
                nMaxStatementLiveTimeMs,
                0);
        }
//      else
//          Log.logImportant("Could not create DB driver " + csDriverClass + " for user " + csDBUser + " on url "+csDBUrl);

        return b;
    }

    /** Sets the environment. */
    public void setEnvironment(String csEnvironment)
    {
        dbConnectionParam.setEnvironment(csEnvironment);
    }

    public void setValidationQuery(String csValidationQuery)
    {
        this.csValidationQuery = csValidationQuery;
    }

    /** Returns the nb unused connections. */
    public int getNbUnusedConnections()
    {
        if (qLConnectionPool == null) {
            return 0;
        }
        return qLConnectionPool.getNbUnusedConnections();
    }

    /** Returns the nb running connections. */
    public int getNbRunningConnections()
    {
        if (qLConnectionPool == null) {
            return 0;
        }
        return qLConnectionPool.getNbRunningConnections();
    }

    /** Executes the show hide running connections operation. */
    public void showHideRunningConnections(boolean bShowRunningCon)
    {
        if (qLConnectionPool != null) {
            qLConnectionPool.showHideRunningConnections(bShowRunningCon);
        }
    }

    /** Executes the dump connections operation. */
    public void dumpConnections(StringBuilder sbText)
    {
        if (qLConnectionPool != null) {
            qLConnectionPool.dumpConnections(sbText);
        }
    }

    /** Returns the nb cached statements for accessor. */
    public int getNbCachedStatementsForAccessor()
    {
        if (qLConnectionPool == null) {
            return 0;
        }
        return qLConnectionPool.getNbCachedStatementsForAccessor();
    }

    /** Returns the nb alloc connnections. */
    public int getNbAllocConnnections()
    {
        if (qLConnectionPool == null) {
            return 0;
        }
        return qLConnectionPool.getNbAllocConnnections();
    }

    /** Returns the nb max connection. */
    public int getNbMaxConnection()
    {
        if (qLConnectionPool == null) {
            return 0;
        }
        return qLConnectionPool.getNbMaxConnection();
    }



    private int maxWaitTimeSeconds = 60 ;
    protected String csValidationQuery = "" ;
    private boolean iscloseCursorOnCommit = false;
    private boolean iscanSetCloseCursorOnCommit = false;    // Oracle cannot set CloseCursorOnCommit, but DB2 can do it

    /**
     * @return the csPropertyName
     */
    public String getPropertyPrefix()
    {
        return propertyPrefix;
    }

    /**
     * @param propertyName the csPropertyName to set
     */
    public void setPropertyPrefix(String csPropertyPrefix)
    {
        this.propertyPrefix = csPropertyPrefix;
    }
}
