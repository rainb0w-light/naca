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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.List;

import jlib.misc.BaseJmxGeneralStat;
import jlib.xml.Tag;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ThreadStatementGC.java,v 1.16 2007/03/15 06:44:38 u930di Exp $
 */
public class ThreadStatementGC extends Thread
{
	private int nPeriod_ms = 0;
	private ArrayDbConnectionPool arrayDbConnectionPool = null;
	private MemoryPoolMXBean tenuredPool = null;
	private int nNbStatementForcedRemoved = 0;
	private boolean bActive = false;
	private int nNbStatementsToRemoveBeforeGC = 0;
	private int nNbSystemGCCall = 0;
	private int nMaxPermanentHeap_Mo = 0;
	private boolean bMaxPermanentHeap_MoSet = false;

	public ThreadStatementGC(Tag tagGCThread, ArrayDbConnectionPool arrayDbConnectionPool)
	{				
		arrayDbConnectionPool = arrayDbConnectionPool;
		bActive = tagGCThread.getValAsBoolean("ActivateThreadGarbageCollectorStatement");
		if(bActive)
		{
			nPeriod_ms = tagGCThread.getValAsInt("GarbageCollectorStatement_ms");
			if(nPeriod_ms <= 30000)
				nPeriod_ms = 30000;	// Cannot be less than 30 seconds
			nNbStatementForcedRemoved = tagGCThread.getValAsInt("NbStatementForcedRemoved");
			nMaxPermanentHeap_Mo = tagGCThread.getValAsInt("MaxPermanentHeap_Mo");
						
			nNbStatementsToRemoveBeforeGC = tagGCThread.getValAsInt("NbStatementsToRemoveBeforeGC", -1);
			nNbSystemGCCall = tagGCThread.getValAsInt("NbSystemGCCall", 0);
			
			if(nMaxPermanentHeap_Mo > 0 && nNbStatementForcedRemoved > 0)
			{
				setMemThreshold();
			}
		}
	}

	private void setMemThreshold()
	{
		bMaxPermanentHeap_MoSet = false;

		List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean p: pools)
		{
			if(p.getType().compareTo(MemoryType.HEAP) == 0)
			{
				String cs = p.getName();
				if(cs.equalsIgnoreCase("Tenured gen"))
				{
					long l = 1024L * 1024L * (long)nMaxPermanentHeap_Mo;
					p.setUsageThreshold(l);
					tenuredPool = p;
				}				
			}
		}
	}

	public synchronized void setCurrentMaxPermanentHeap_Mo(int nMaxPermanentHeap_Mo)
	{
		nMaxPermanentHeap_Mo = nMaxPermanentHeap_Mo;
		bMaxPermanentHeap_MoSet = true;
	}
	
	public synchronized int getCurrentMaxPermanentHeap_Mo()
	{
		return nMaxPermanentHeap_Mo;
	}
	
//	public void addDbConnectionPool(DbConnectionPool dbConnectionPool)
//	{
//		if(arrDbConnectionPool == null)
//			arrDbConnectionPool = new ArrayList<DbConnectionPool>();
//		arrDbConnectionPool.add(dbConnectionPool);
//	}
	
	public void run()
    {
		while(bActive && waitPeriod())
		{
			BaseJmxGeneralStat.incCounter(BaseJmxGeneralStat.COUNTER_INDEX_NbRunThreadGC);
			if(bMaxPermanentHeap_MoSet)	// Mem threshhold has changed
			{
				setMemThreshold();
			}
			if(arrayDbConnectionPool != null)
				arrayDbConnectionPool.handleCleanings(tenuredPool, nNbStatementsToRemoveBeforeGC, nNbStatementForcedRemoved, nNbSystemGCCall);
		}
    }

//	private synchronized void doRun()
//	{
//		if(arrDbConnectionPool != null)
//		{				
//			for(int n=0; n<arrDbConnectionPool.size(); n++)
//			{
//				DbConnectionPool dbConnectionPool = arrDbConnectionPool.get(n);
//				nNbTotalStatementRemoved += dbConnectionPool.garbageCollectorStatementsOfAllCollections();
//			}
//			int nNbStatementAggressiveRemoved = 0;
//			if(tenuredPool != null && tenuredPool.isUsageThresholdExceeded() && nNbStatementForcedRemoved > 0)
//			{
//				// Aggressivelly remove statements is heap usage is to high 
//				// Collect all statements from all pools
//				SortedMap<Long, StatementPosInPool> mapStatements = new TreeMap<Long, StatementPosInPool>();
//				for(int n=0; n<arrDbConnectionPool.size(); n++)
//				{
//					DbConnectionPool dbConnectionPool = arrDbConnectionPool.get(n);
//					dbConnectionPool.buildStatementOrderedList(mapStatements);
//				}
//				nNbStatementAggressiveRemoved = aggressiveRemoveObsoleteStatements(mapStatements);
//				if(nNbStatementAggressiveRemoved != 0)
//					Log.logNormal("Aggressivelly removed " + nNbStatementAggressiveRemoved + " SQL statements, because mem usage is too high");
//			}
//			nNbTotalStatementRemoved += nNbStatementAggressiveRemoved;
//			if(nNbTotalStatementRemoved >= nNbStatementsToRemoveBeforeGC)
//			{
//				Log.logNormal("Forcing garbage collector");
//				tryForceGC();
//				nNbTotalStatementRemoved = 0;
//			}
//		}				
//	}
	
	private boolean waitPeriod()
	{
		try
		{			
			Thread.sleep(nPeriod_ms);
			return true;
		}
		catch (InterruptedException e)
		{
			return false;
		}
	}
}
