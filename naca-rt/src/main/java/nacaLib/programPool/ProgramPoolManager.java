/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.programPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import jlib.log.Log;
import jlib.misc.StopWatch;

import nacaLib.base.JmxGeneralStat;
import nacaLib.basePrgEnv.BaseProgram;

/** Provides program pool manager behavior. */
public class ProgramPoolManager //extends BaseOpenMBean
{
    private Hashtable<String, ProgramInstancesPool> hashProgramInstancesPool = null;

    /** Creates a new program pool manager instance. */
    public ProgramPoolManager(boolean bUseJmx)
    {
        // hash table of ProgramInstancePool, indexed by program name
        hashProgramInstancesPool = new Hashtable<String, ProgramInstancesPool>();
        if (bUseJmx) {
            JmxGeneralStat.addProgramPoolManager(this);
        }
    }

    /** Sets the show program beans. */
    public void setShowProgramBeans(boolean b)
    {
        Collection<ProgramInstancesPool> col = hashProgramInstancesPool.values();
        Iterator<ProgramInstancesPool> iter = col.iterator();
        while(iter.hasNext())
        {
            ProgramInstancesPool p = iter.next();
            p.showBean(b);
        }
    }

    /** Loads the pooled program instance. */
    public BaseProgram loadPooledProgramInstance(String csProgramName)
    {
        ProgramInstancesPool programInstancesPool = hashProgramInstancesPool.get(csProgramName);
        if(programInstancesPool == null)    // new program pool: this prg has never been loaded
        {
            programInstancesPool = createProgramInstancesPool(csProgramName);
        }

        BaseProgram program = programInstancesPool.getOrCreateUnusedInstance();
        // if programInstance != null then we are inside a read lock, else no read lock set

        // Double check programInstancesPool, as it may have been destroyed in jmx thread (double check pattern)
        programInstancesPool = hashProgramInstancesPool.get(csProgramName);
        if(programInstancesPool == null)    // new program pool: this prg has never been loaded
        {
            programInstancesPool = createProgramInstancesPool(csProgramName);
        }

        return program;
    }

    /** Executes the preload second instance program operation. */
    public BaseProgram preloadSecondInstanceProgram(String csProgramName)
    {
        ProgramInstancesPool programInstancesPool = hashProgramInstancesPool.get(csProgramName);
        if(programInstancesPool != null)    // The prg pool must exists
        {
            BaseProgram program = programInstancesPool.preloadSecondInstance();
            return program;
        }
        return null;
    }

    /** Executes the unload all programs operation. */
    public void unloadAllPrograms(boolean bDoGCAfterEachProgramUnload)
    {
        Collection<ProgramInstancesPool> collectionprogramInstancesPool = hashProgramInstancesPool.values();
        if(collectionprogramInstancesPool != null)
        {
            // Create another ProgramInstancesPool container, as m_hashProgramInstancesPool will be structurally modified in
            // ProgramInstancesPool::unloadProgram() call
            int nNbEntries = 0;
            ArrayList<ProgramInstancesPool> arrProgramInstancesPool = new ArrayList<ProgramInstancesPool>();
            Iterator<ProgramInstancesPool> iter = collectionprogramInstancesPool.iterator();
            while(iter.hasNext())
            {
                ProgramInstancesPool programInstancesPool = iter.next();
                arrProgramInstancesPool.add(programInstancesPool);
                nNbEntries++;
            }
            collectionprogramInstancesPool = null;


            StopWatch sw = new StopWatch();
            for(int n=0; n<nNbEntries; n++)
            {
                ProgramInstancesPool programInstancesPool = arrProgramInstancesPool.get(n);
                programInstancesPool.unloadProgram();
                if (bDoGCAfterEachProgramUnload) {
                    System.gc();
                }
                programInstancesPool = null;
            }
            arrProgramInstancesPool = null;

            Log.logNormal("Unload time="+sw.getElapsedTimeReset());
            System.gc();
            Log.logNormal("GC 1 after Unload time="+sw.getElapsedTimeReset());
            System.gc();
            Log.logNormal("GC 2 after Unload time="+sw.getElapsedTimeReset());
            System.gc();
            Log.logNormal("GC 3 after Unload time="+sw.getElapsedTimeReset());
        }
    }

    /** Returns the program pool. */
    public ProgramInstancesPool getProgramPool(String csProgramName)
    {
        ProgramInstancesPool programInstancesPool = hashProgramInstancesPool.get(csProgramName);
        return programInstancesPool;
    }

    private ProgramInstancesPool createProgramInstancesPool(String csProgramName)
    {
        // create a program pool, and register it into the hash table
        ProgramInstancesPool programPool = new ProgramInstancesPool(this, csProgramName);
        hashProgramInstancesPool.put(csProgramName, programPool);
        return programPool;
    }

    /** Removes the program instances pool. */
    public void removeProgramInstancesPool(String csProgramName)
    {
        hashProgramInstancesPool.remove(csProgramName);
    }


    /** Executes the return program instance to pool operation. */
    public void returnProgramInstanceToPool(BaseProgram program)
    {
        String csProgramName = program.getProgramManager().getProgramName();
        ProgramInstancesPool programInstancesPool = hashProgramInstancesPool.get(csProgramName);
        if(programInstancesPool != null)
        {
            programInstancesPool.returnProgram(program);
        }
    }

    /** Returns the nb program stacked. */
    public int getNbProgramStacked()
    {
        int n = 0;
        Collection<ProgramInstancesPool> col = hashProgramInstancesPool.values();
        Iterator<ProgramInstancesPool> iter = col.iterator();
        while(iter.hasNext())
        {
            ProgramInstancesPool p = iter.next();
            n += p.getNbInstancesStacked();
        }
        return n;
    }
}
