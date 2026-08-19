/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.spServer;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SpServerEnvironment.java,v 1.4 2007/06/22 21:38:46 u930bm Exp $
 */
import java.sql.SQLException;

import jlib.sql.DbConnectionManagerBase;
import nacaLib.basePrgEnv.BaseCESMManager;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseResourceManager;

/** Provides sp server environment behavior. */
public class SpServerEnvironment extends BaseEnvironment
{
    /** Creates a new sp server environment instance. */
    public SpServerEnvironment(
        SpServerSession spServerSession,
        DbConnectionManagerBase connectionManager,
        BaseResourceManager baseResourceManager)
    {
        super(spServerSession, connectionManager, baseResourceManager);
    }

    /** Creates the cesmmanager. */
    public BaseCESMManager createCESMManager()
    {
        return null;
    }

    public SpServerSession getSession()
    {
        return null;
    }

    /** Executes the break current session if timeout operation. */
    public void breakCurrentSessionIfTimeout()
    {
    }

    /** Executes the commit sql operation. */
    public SQLException commitSQL()
    {
        return null;
    }

    /** Executes the rollback sql operation. */
    public SQLException rollbackSQL()
    {
        return null;
    }

    /** Executes the cleanup on exception catched operation. */
    public void cleanupOnExceptionCatched()
    {
        rollbackSQL() ;
        releaseSQLConnection() ;
        autoCloseOpenFile();
        returnTempCacheToStack();
    }

    /** Executes the release sqlconnection operation. */
    public void releaseSQLConnection()
    {
        if (getSQLConnection() != null)
        {
            getSQLConnection().removeAllPreparedStatements();
            super.releaseSQLConnection();
        }
    }
}
