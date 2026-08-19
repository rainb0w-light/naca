/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.bdb;

import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;
import jlib.threads.PooledThread;
import jlib.threads.PoolOfThreads;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BtreePooledWriterThread.java,v 1.1 2006/11/29 09:31:30 u930di Exp $
 */
public class BtreePooledWriterThread extends PooledThread
{
    private BtreeKeyDescription keyDescription = null;

    /** Creates a new btree pooled writer thread instance. */
    public BtreePooledWriterThread(PoolOfThreads owningPool)
    {
        super(owningPool);
    }

    void setBtreeKeyDescription(BtreeKeyDescription keyDescription)
    {
        this.keyDescription = keyDescription;
    }

    /** Executes the pre run operation. */
    public boolean preRun()
    {
        // Fill the TLS with key description
        if(keyDescription != null)
        {
            TempCacheLocator.setTempCache();    // Init TLS
            TempCache t = TempCacheLocator.getTLSTempCache();
            if(t != null)
            {
                t.setBtreeKeyDescription(keyDescription);
                return true;
            }
        }
        return false;   // No key desc !
    }

    /** Executes the post run operation. */
    public void postRun()
    {
        TempCacheLocator.relaseTempCache();
    }

}
