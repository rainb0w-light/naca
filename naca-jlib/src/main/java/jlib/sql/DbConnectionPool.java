/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedMap;

import jlib.log.Log;
import jlib.misc.StringUtil;
import jlib.xml.Tag;


/**
 * @author PJD
 *
 */
public class DbConnectionPool
{
    private int garbageCollectorStatementMillis = 0;
    private Hashtable<String, DbConnectionColl> hashConnectionsByProgramId = null;

    DbConnectionPool(
        String csPoolName,
        int nNbMaxConnections,
        int nTimeBeforeRemoveConnectionMs,
        int nMaxStatementLiveTimeMs,
        int nGarbageCollectorStatementMs)
    {
        this.garbageCollectorStatementMillis = nGarbageCollectorStatementMs;
        DbConnectionColl dbConnectionColl = new DbConnectionColl(
            csPoolName,
            nNbMaxConnections,
            nTimeBeforeRemoveConnectionMs,
            nMaxStatementLiveTimeMs,
            false,
            nGarbageCollectorStatementMs);
        addProgram("", null, dbConnectionColl);
    }

    DbConnectionPool(Tag tagSQLConfig)
    {
        garbageCollectorStatementMillis = tagSQLConfig.getValAsInt("GarbageCollectorStatement_ms");

        Tag tagPools = tagSQLConfig.getChild("Pools") ;
        if (tagPools != null)
        {
            Tag tagPool = tagPools.getEnumChild("Pool");
            while(tagPool != null)
            {
                int nMaxConnection = tagPool.getValAsInt("MaxConnection");
                boolean bUseExplain = tagPool.getValAsBoolean("UseExplain");
                int nTimeBeforeRemoveConnectionMs = tagPool.getValAsInt("TimeBeforeRemoveConnection_ms");
                int nMaxStatementLiveTimeMs = tagPool.getValAsInt("MaxStatementLiveTime_ms");

                String poolName = tagPool.getVal("Name");
                if (StringUtil.isEmpty(poolName)) {
                    poolName = "UnknownPoolName";
                }
                DbConnectionColl dbConnectionColl = new DbConnectionColl(
                    poolName,
                    nMaxConnection,
                    nTimeBeforeRemoveConnectionMs,
                    nMaxStatementLiveTimeMs,
                    bUseExplain,
                    garbageCollectorStatementMillis);

                // enum all Program
                String parentProgramId = tagPool.getVal("ParentProgramId");
                parentProgramId = parentProgramId.trim();

                String programIds = tagPool.getVal("ProgramId");
                if(!StringUtil.isEmpty(programIds))
                {
                    int nIndex = programIds.indexOf(',');
                    while(nIndex != -1)
                    {
                        String programId = programIds.substring(0, nIndex).trim();
                        addProgram(programId, parentProgramId, dbConnectionColl);

                        programIds = programIds.substring(nIndex+1);
                        nIndex = programIds.indexOf(',');
                    }
                    String programId = programIds.trim();
                    addProgram(programId, parentProgramId, dbConnectionColl);
                }
                else
                {
                    addProgram("", null, dbConnectionColl);
                }

                tagPool = tagPools.getEnumChild();
            }
        }
    }


    private void addProgram(String programId, String parentProgramId, DbConnectionColl dbConnectionColl)
    {
        if (hashConnectionsByProgramId == null) {
            hashConnectionsByProgramId = new Hashtable<String, DbConnectionColl>();
        }

        if(!StringUtil.isEmpty(parentProgramId))
        {
            String fullName = makeFullName(programId, parentProgramId);
            hashConnectionsByProgramId.put(fullName, dbConnectionColl);
        } else {
            hashConnectionsByProgramId.put(programId, dbConnectionColl);
        }
    }

    void releaseConnection(DbConnectionBase sqlConnection)
    {
        if (sqlConnection.dbConnectionColl != null) {
            sqlConnection.dbConnectionColl.releaseConnection(sqlConnection);
        }
    }

    /** Returns the connection coll for pref. */
    synchronized public DbConnectionColl getConnectionCollForPref(String csProgramId, String csProgramParent)
    {
        DbConnectionColl connectionColl = null;

        if (hashConnectionsByProgramId != null)
        {
            String fullName = makeFullName(csProgramId, csProgramParent);
            connectionColl = hashConnectionsByProgramId.get(fullName);

            // Not found with a program parent name; try with only required program name
            if (connectionColl == null && !StringUtil.isEmpty(csProgramParent)) {
                connectionColl = hashConnectionsByProgramId.get(csProgramId);
            }

            if (connectionColl == null) {  // Still not found; try default naming
                connectionColl = hashConnectionsByProgramId.get("");
            }
        }
        return connectionColl;
    }

    private String makeFullName(String programId, String programParent)
    {
        if (!StringUtil.isEmpty(programId) && !StringUtil.isEmpty(programParent)) {
            return programId + "$" + programParent;
        } else if (!StringUtil.isEmpty(programId)) {
            return programId;
        }
        return "";
    }

    private int removeStatements(Collection<DbConnectionColl> colDbConnectionColl)
    {
        int nNbStatementRemoved = 0;
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            nNbStatementRemoved += dbConnectionColl.garbageCollectorStatementsOfCollection();
        }
        return nNbStatementRemoved;
    }

    int garbageCollectorStatementsOfAllCollections()
    {
        int nNbStatementRemoved = 0;

        Collection<DbConnectionColl> colDbConnectionColl = null;

        if(hashConnectionsByProgramId != null)
        {
            colDbConnectionColl = hashConnectionsByProgramId.values();
            nNbStatementRemoved += removeStatements(colDbConnectionColl);
        }

        Log.logNormal("garbageCollectorStatementsOfAllConnections remove " + nNbStatementRemoved + " statements");

        return nNbStatementRemoved;
    }

    // Force the removal of all statements for all connections
    /** Executes the force remove all statements of all collections operation. */
    public void forceRemoveAllStatementsOfAllCollections()
    {
        Collection<DbConnectionColl> colDbConnectionColl = null;
        if(hashConnectionsByProgramId != null)
        {
            colDbConnectionColl = hashConnectionsByProgramId.values();
            forceRemoveAllStatements(colDbConnectionColl);
        }

        Log.logNormal("forceRemoveAllStatementsOfAllCollections removed all DB connections with their statements");
    }

    private void forceRemoveAllStatements(Collection<DbConnectionColl> colDbConnectionColl)
    {
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            dbConnectionColl.forceRemoveAllStatementsOfCollection();
        }
    }

    /** Builds the statement ordered list. */
    public void buildStatementOrderedList(SortedMap<Long, StatementPosInPool> mapStatements)
    {
        if(hashConnectionsByProgramId != null)
        {
            Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
            buildStatementOrderedList(colDbConnectionColl, mapStatements);
        }
    }

    private void buildStatementOrderedList(
        Collection<DbConnectionColl> colDbConnectionColl,
        SortedMap<Long, StatementPosInPool> mapStatements)
    {
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            dbConnectionColl.dumpListStatements(mapStatements);
        }
    }

    /** Returns the nb unused connections. */
    public synchronized int getNbUnusedConnections()
    {
        if (hashConnectionsByProgramId == null) {
            return 0;
        }

        int n = 0;
        Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            n += dbConnectionColl.getNbFreeConnection();
        }
        return n;
    }

    /** Returns the nb running connections. */
    public synchronized int getNbRunningConnections()
    {
        if (hashConnectionsByProgramId == null) {
            return 0;
        }

        int n = 0;
        Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            n += dbConnectionColl.getNbRunningConnections();
        }
        return n;
    }

    /** Executes the show hide running connections operation. */
    public synchronized void showHideRunningConnections(boolean bShowRunningCon)
    {
        if(hashConnectionsByProgramId != null)
        {
            Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
            Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
            while(iterDbConnectionColl.hasNext())
            {
                DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
                dbConnectionColl.showHideRunningConnections(bShowRunningCon);
            }
        }
    }

    /** Executes the dump connections operation. */
    public synchronized void dumpConnections(StringBuilder sbText)
    {
        if(hashConnectionsByProgramId != null)
        {
            Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
            Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
            while(iterDbConnectionColl.hasNext())
            {
                DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
                dbConnectionColl.dumpConnections(sbText);
            }
        }
    }

    /** Returns the nb cached statements for accessor. */
    public synchronized int getNbCachedStatementsForAccessor()
    {
        if (hashConnectionsByProgramId == null) {
            return 0;
        }

        int n = 0;
        Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            n += dbConnectionColl.getNbCachedStatementsForAccessor();
        }
        return n;
    }

    /** Returns the nb alloc connnections. */
    public synchronized int getNbAllocConnnections()
    {
        if (hashConnectionsByProgramId == null) {
            return 0;
        }

        int n = 0;
        Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            n += dbConnectionColl.getNbAllocConnnections();
        }
        return n;
    }

    /** Returns the nb max connection. */
    public synchronized int getNbMaxConnection()
    {
        if (hashConnectionsByProgramId == null) {
            return 0;
        }

        int n = 0;
        Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
        Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
        while(iterDbConnectionColl.hasNext())
        {
            DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
            n += dbConnectionColl.getNbMaxConnection();
        }
        return n;
    }
}
