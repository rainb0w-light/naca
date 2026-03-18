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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DbConnectionPool
{
	private int nGarbageCollectorStatement_ms = 0;
	private Hashtable<String, DbConnectionColl> hashConnectionsByProgramId = null; 

	DbConnectionPool(String csPoolName, int nNbMaxConnections, int nTimeBeforeRemoveConnection_ms, int nMaxStatementLiveTime_ms, int nGarbageCollectorStatement_ms)
	{
		nGarbageCollectorStatement_ms = nGarbageCollectorStatement_ms;
		DbConnectionColl dbConnectionColl = new DbConnectionColl(csPoolName, nNbMaxConnections, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, false, nGarbageCollectorStatement_ms);
		addProgram("", null, dbConnectionColl);
	}
	
	DbConnectionPool(Tag tagSQLConfig)
	{
		nGarbageCollectorStatement_ms = tagSQLConfig.getValAsInt("GarbageCollectorStatement_ms");
		
		Tag tagPools = tagSQLConfig.getChild("Pools") ;
		if (tagPools != null)
		{				
			Tag tagPool = tagPools.getEnumChild("Pool");
			while(tagPool != null)
			{
				int nMaxConnection = tagPool.getValAsInt("MaxConnection");
				boolean bUseExplain = tagPool.getValAsBoolean("UseExplain");
				int nTimeBeforeRemoveConnection_ms = tagPool.getValAsInt("TimeBeforeRemoveConnection_ms");
				int nMaxStatementLiveTime_ms = tagPool.getValAsInt("MaxStatementLiveTime_ms");
				
				String csPoolName = tagPool.getVal("Name");
				if(StringUtil.isEmpty(csPoolName))
					csPoolName = "UnknownPoolName";
				DbConnectionColl dbConnectionColl = new DbConnectionColl(csPoolName, nMaxConnection, nTimeBeforeRemoveConnection_ms, nMaxStatementLiveTime_ms, bUseExplain, nGarbageCollectorStatement_ms);

				// enum all Program
				String csParentProgramId = tagPool.getVal("ParentProgramId");
				csParentProgramId = csParentProgramId.trim();
				
				String csProgramIds = tagPool.getVal("ProgramId");
				if(!StringUtil.isEmpty(csProgramIds))
				{
					int nIndex = csProgramIds.indexOf(',');
					while(nIndex != -1)
					{
						String csProgramId = csProgramIds.substring(0, nIndex).trim();
						addProgram(csProgramId, csParentProgramId, dbConnectionColl);
							
						csProgramIds = csProgramIds.substring(nIndex+1);
						nIndex = csProgramIds.indexOf(',');
					}
					String csProgramId = csProgramIds.trim();
					addProgram(csProgramId, csParentProgramId, dbConnectionColl);
				}
				else
				{
					addProgram("", null, dbConnectionColl);
				}
				
				tagPool = tagPools.getEnumChild();
			}
		}		
	}

		
	private void addProgram(String csProgramId, String csParentProgramId, DbConnectionColl dbConnectionColl)
	{
		if(hashConnectionsByProgramId == null)
			hashConnectionsByProgramId = new Hashtable<String, DbConnectionColl>();
		
		if(!StringUtil.isEmpty(csParentProgramId))
		{
			String csFullName = makeFullName(csProgramId, csParentProgramId);
			hashConnectionsByProgramId.put(csFullName, dbConnectionColl);
		}
		else
			hashConnectionsByProgramId.put(csProgramId, dbConnectionColl);
	}
	
	void releaseConnection(DbConnectionBase sqlConnection)
	{		
		if(sqlConnection.dbConnectionColl != null)
			sqlConnection.dbConnectionColl.releaseConnection(sqlConnection);
	}
	
	synchronized public DbConnectionColl getConnectionCollForPref(String csProgramId, String csProgramParent)
	{
		DbConnectionColl connectionColl = null;
		
		if (hashConnectionsByProgramId != null)
		{	
			String csFullName = makeFullName(csProgramId, csProgramParent);
			connectionColl = hashConnectionsByProgramId.get(csFullName);

			if(connectionColl == null && !StringUtil.isEmpty(csProgramParent))	// Not found with a program parent name; try with only required program name
				connectionColl = hashConnectionsByProgramId.get(csProgramId);
		
			if(connectionColl == null)	// Still not found; try default naming
				connectionColl = hashConnectionsByProgramId.get("");
		}
		return connectionColl;
	}
	
	private String makeFullName(String csProgramId, String csProgramParent)
	{
		if(!StringUtil.isEmpty(csProgramId) && !StringUtil.isEmpty(csProgramParent))
			return csProgramId + "$" + csProgramParent;
		else if(!StringUtil.isEmpty(csProgramId))
			return csProgramId;
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
	
	public void buildStatementOrderedList(SortedMap<Long, StatementPosInPool> mapStatements) 
	{
		if(hashConnectionsByProgramId != null)
		{
			Collection<DbConnectionColl> colDbConnectionColl = hashConnectionsByProgramId.values();
			buildStatementOrderedList(colDbConnectionColl, mapStatements);
		}
	}
	
	private void buildStatementOrderedList(Collection<DbConnectionColl> colDbConnectionColl, SortedMap<Long, StatementPosInPool> mapStatements)
	{
		Iterator<DbConnectionColl> iterDbConnectionColl = colDbConnectionColl.iterator();
		while(iterDbConnectionColl.hasNext())
		{			
			DbConnectionColl dbConnectionColl = iterDbConnectionColl.next();
			dbConnectionColl.dumpListStatements(mapStatements);
		}
	}
	
	public synchronized int getNbUnusedConnections()
	{
		if(hashConnectionsByProgramId == null)
			return 0;
		
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
	
	public synchronized int getNbRunningConnections()
	{
		if(hashConnectionsByProgramId == null)
			return 0;
		
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

	public synchronized int getNbCachedStatementsForAccessor()
	{
		if(hashConnectionsByProgramId == null)
			return 0;
		
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
	
	public synchronized int getNbAllocConnnections()
	{
		if(hashConnectionsByProgramId == null)
			return 0;
		
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
	
	public synchronized int getNbMaxConnection()
	{
		if(hashConnectionsByProgramId == null)
			return 0;
		
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
