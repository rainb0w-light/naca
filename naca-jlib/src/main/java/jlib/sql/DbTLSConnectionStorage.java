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

import java.sql.Connection;



/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbTLSConnectionStorage.java,v 1.5 2008/07/09 06:40:18 u930di Exp $
 */
public class DbTLSConnectionStorage
{
    private static ThreadLocal<DbTLSStoredConnections> mtls = new ThreadLocal<DbTLSStoredConnections>();

    /** Executes the get operation. */
    public static DbConnectionBase get(DbAccessor dbId)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections != null)
        {
            DbConnectionBase dbConnectionBase = storedConnections.getForeignConnection();
            if (dbConnectionBase == null) {    // No foreign connection specified; search for a connection bound to dbId
                dbConnectionBase = storedConnections.getDbId(dbId);
            }
            return dbConnectionBase;
        }
        return null;
    }

    /** Executes the set operation. */
    public static void set(DbAccessor dbId, DbConnectionBase dbConnectionBase)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections == null)
        {
            storedConnections = new DbTLSStoredConnections();
            mtls.set(storedConnections);
        }
        storedConnections.putDbId(dbId, dbConnectionBase);
    }

    /** Executes the return all connections to pool operation. */
    public static boolean returnAllConnectionsToPool()
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections != null)
        {
            return storedConnections.returnAllConnectionsToPool();
        }
        return false;
    }

    /** Executes the return connection to pool operation. */
    public static boolean returnConnectionToPool(DbAccessor dbId)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections != null)
        {
            return storedConnections.returnConnectionToPool(dbId);
        }
        return false;
    }

    /** Executes the dump connections for all accessors operation. */
    public static void dumpConnectionsForAllAccessors(StringBuilder sbText)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections != null)
        {
            storedConnections.dumpConnections(sbText);
        }
    }

    /** Executes the commit operation. */
    public static boolean commit(DbAccessor dbId)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections != null)
        {
            return storedConnections.commit(dbId);
        }
        return false;
    }

    /** Method added by Jilali Raki for WLC stored procedures
     *
     * @param dbId
     * @param autoCommit
     * @return
     */
    public static boolean setAutoCommit(DbAccessor dbId, boolean autoCommit)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections != null)
        {
            return storedConnections.setAutoCommit(dbId, autoCommit);
        }
        return false;
    }

    /** Executes the roll back operation. */
    public static boolean rollBack(DbAccessor dbId)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections != null)
        {
            return storedConnections.rollBack(dbId);
        }
        return false;
    }

    /** Sets the foreign connection. */
    public static void setForeignConnection(Connection connectionJDBC, String csEnv)
    {
        DbConnectionBase foreignDbConnection = new DbConnection(connectionJDBC, csEnv, false);
        setForeignConnection(foreignDbConnection);
    }

    /** Sets the foreign connection. */
    public static void setForeignConnection(Connection connectionJDBC, String csEnv, boolean bUseCachedStatements)
    {
        DbConnectionBase foreignDbConnection = new DbConnection(connectionJDBC, csEnv, bUseCachedStatements);
        setForeignConnection(foreignDbConnection);
    }

    /** Sets the foreign connection. */
    public static void setForeignConnection(DbConnectionBase foreignDbConnection)
    {
        DbTLSStoredConnections storedConnections = mtls.get();
        if(storedConnections == null)
        {
            storedConnections = new DbTLSStoredConnections();
            mtls.set(storedConnections);
        }
        storedConnections.setForeignConnection(foreignDbConnection);
    }
}
