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
    private int periodMillis = 0;
    private ArrayDbConnectionPool arrayDbConnectionPool = null;
    private MemoryPoolMXBean tenuredPool = null;
    private int nNbStatementForcedRemoved = 0;
    private boolean isactive = false;
    private int nNbStatementsToRemoveBeforeGC = 0;
    private int nNbSystemGCCall = 0;
    private int maxPermanentHeapMegabytes = 0;
    private boolean maxPermanentHeapMegabytesSet = false;

    /** Creates a new thread statement gc instance. */
    public ThreadStatementGC(Tag tagGCThread, ArrayDbConnectionPool arrayDbConnectionPool)
    {
        this.arrayDbConnectionPool = arrayDbConnectionPool;
        isactive = tagGCThread.getValAsBoolean("ActivateThreadGarbageCollectorStatement");
        if(isactive)
        {
            periodMillis = tagGCThread.getValAsInt("GarbageCollectorStatement_ms");
            if (periodMillis <= 30000) {
                periodMillis = 30000; // Cannot be less than 30 seconds
            }
            nNbStatementForcedRemoved = tagGCThread.getValAsInt("NbStatementForcedRemoved");
            maxPermanentHeapMegabytes = tagGCThread.getValAsInt("MaxPermanentHeap_Mo");

            nNbStatementsToRemoveBeforeGC = tagGCThread.getValAsInt("NbStatementsToRemoveBeforeGC", -1);
            nNbSystemGCCall = tagGCThread.getValAsInt("NbSystemGCCall", 0);

            if (maxPermanentHeapMegabytes > 0 && nNbStatementForcedRemoved > 0)
            {
                setMemThreshold();
            }
        }
    }

    private void setMemThreshold()
    {
        maxPermanentHeapMegabytesSet = false;

        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean p: pools)
        {
            if(p.getType().compareTo(MemoryType.HEAP) == 0)
            {
                String cs = p.getName();
                if(cs.equalsIgnoreCase("Tenured gen"))
                {
                    long l = 1024L * 1024L * (long)maxPermanentHeapMegabytes;
                    p.setUsageThreshold(l);
                    tenuredPool = p;
                }
            }
        }
    }

    /** Sets the current max permanent heap mo. */
    public synchronized void setCurrentMaxPermanentHeap_Mo(int nMaxPermanentHeapMo)
    {
        this.maxPermanentHeapMegabytes = nMaxPermanentHeapMo;
        maxPermanentHeapMegabytesSet = true;
    }

    public synchronized int getCurrentMaxPermanentHeap_Mo()
    {
        return maxPermanentHeapMegabytes;
    }

//  public void addDbConnectionPool(DbConnectionPool dbConnectionPool)
//  {
//      if(arrDbConnectionPool == null)
//          arrDbConnectionPool = new ArrayList<DbConnectionPool>();
//      arrDbConnectionPool.add(dbConnectionPool);
//  }

    /** Runs this operation. */
    public void run()
    {
        while(isactive && waitPeriod())
        {
            BaseJmxGeneralStat.incCounter(BaseJmxGeneralStat.COUNTER_INDEX_NbRunThreadGC);
            if(maxPermanentHeapMegabytesSet)    // Mem threshhold has changed
            {
                setMemThreshold();
            }
            if (arrayDbConnectionPool != null) {
                arrayDbConnectionPool.handleCleanings(
                        tenuredPool,
                        nNbStatementsToRemoveBeforeGC,
                        nNbStatementForcedRemoved,
                        nNbSystemGCCall);
            }
        }
    }

//  private synchronized void doRun()
//  {
//      if(arrDbConnectionPool != null)
//      {
//          for(int n=0; n<arrDbConnectionPool.size(); n++)
//          {
//              DbConnectionPool dbConnectionPool = arrDbConnectionPool.get(n);
//              nNbTotalStatementRemoved += dbConnectionPool.garbageCollectorStatementsOfAllCollections();
//          }
//          int nNbStatementAggressiveRemoved = 0;
//          if(tenuredPool != null && tenuredPool.isUsageThresholdExceeded() && nNbStatementForcedRemoved > 0)
//          {
//              // Aggressivelly remove statements is heap usage is to high
//              // Collect all statements from all pools
//              SortedMap<Long, StatementPosInPool> mapStatements = new TreeMap<Long, StatementPosInPool>();
//              for(int n=0; n<arrDbConnectionPool.size(); n++)
//              {
//                  DbConnectionPool dbConnectionPool = arrDbConnectionPool.get(n);
//                  dbConnectionPool.buildStatementOrderedList(mapStatements);
//              }
//              nNbStatementAggressiveRemoved = aggressiveRemoveObsoleteStatements(mapStatements);
//              if(nNbStatementAggressiveRemoved != 0)
// Log.logNormal("Aggressivelly removed " + nNbStatementAggressiveRemoved + " SQL statements, because mem usage is too high");
//          }
//          nNbTotalStatementRemoved += nNbStatementAggressiveRemoved;
//          if(nNbTotalStatementRemoved >= nNbStatementsToRemoveBeforeGC)
//          {
//              Log.logNormal("Forcing garbage collector");
//              tryForceGC();
//              nNbTotalStatementRemoved = 0;
//          }
//      }
//  }

    private boolean waitPeriod()
    {
        try
        {
            Thread.sleep(periodMillis);
            return true;
        }
        catch (InterruptedException e)
        {
            return false;
        }
    }
}
