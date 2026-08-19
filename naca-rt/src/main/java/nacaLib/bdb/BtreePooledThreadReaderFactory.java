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

import jlib.threads.BasePooledThreadFactory;
import jlib.threads.PoolOfThreads;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BtreePooledThreadReaderFactory.java,v 1.1 2006/11/29 09:31:30 u930di Exp $
 */
public class BtreePooledThreadReaderFactory extends BasePooledThreadFactory
{
    private BtreeFile btreeFile = null;
    /** Creates a new btree pooled thread reader factory instance. */
    public BtreePooledThreadReaderFactory(BtreeFile btreeFile)
    {
        this.btreeFile = btreeFile;
    }

    /** Executes the make operation. */
    public BtreePooledReaderThread make(PoolOfThreads owningPool)
    {
        BtreePooledReaderThread thread = new BtreePooledReaderThread(owningPool);
        thread.setBtreeFile(btreeFile);

        return thread;
    }
}
