/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.dbUtils;

import jlib.sql.DbConnectionBase;
import jlib.threads.PoolOfThreads;
import jlib.threads.PooledThread;
import jlib.threads.ThreadPoolRequest;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class PooledThreadDbTransfer extends PooledThread
{
    private DbTransferDesc dbTransferDesc = null;
    private DbConnectionBase dbConnectionSource = null;
    private DbConnectionBase dbConnectionDestination = null;

    /** Creates a new pooled thread db transfer instance. */
    public PooledThreadDbTransfer(
        PoolOfThreads owningPool,
        DbTransferDesc dbTransferDesc,
        DbConnectionBase dbConnectionSource,
        DbConnectionBase dbConnectionDestination)
    {
        super(owningPool);
        this.dbTransferDesc = dbTransferDesc;
        this.dbConnectionSource = dbConnectionSource;
        this.dbConnectionDestination = dbConnectionDestination;
    }

    /** Executes the pre run operation. */
    public boolean preRun()
    {
        return true;
    }

    /** Executes the post run operation. */
    public void postRun()
    {
    }

    protected boolean canHandleRequest()
    {
        return true;
    }

    protected void handleRequest(ThreadPoolRequest request)
    {
        TableToTransfer tableToTransfer = (TableToTransfer)request;
        tableToTransfer.execute(dbConnectionSource, dbConnectionDestination, dbTransferDesc);
    }
}
