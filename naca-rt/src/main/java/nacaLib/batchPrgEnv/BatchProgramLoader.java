/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.batchPrgEnv;

import jlib.sql.DbConnectionManagerBase;
import jlib.xml.Tag;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.CBaseMapFieldLoader;
import nacaLib.exceptions.AbortSessionException;

/** Provides batch program loader behavior. */
public class BatchProgramLoader extends BaseProgramLoader
{
    /** Creates a new batch program loader instance. */
    public BatchProgramLoader(DbConnectionManagerBase connectionManager, Tag tagSequencerConfig)
    {
        super(connectionManager, tagSequencerConfig, true);
    }

    /** Executes the run program operation. */
    public void RunProgram(BaseSession appSession) throws AbortSessionException
    {
    }

    /** Executes the get program loader instance operation. */
    public static BatchProgramLoader GetProgramLoaderInstance()
    {
        return (BatchProgramLoader)ms_Instance ;
    }

    /** Executes the do help operation. */
    public void doHelp(CBaseMapFieldLoader fieldLoader, BaseSession session)
    {
    }
}
