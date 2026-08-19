/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.callPrg;

import jlib.sql.DbConnectionManagerBase;
import jlib.xml.Tag;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.CBaseMapFieldLoader;
import nacaLib.exceptions.AbortSessionException;

/** Provides called program loader behavior. */
public class CalledProgramLoader extends BaseProgramLoader
{
    /** Creates a new called program loader instance. */
    public CalledProgramLoader(DbConnectionManagerBase connectionManager, Tag tagSequencerConfig)
    {
        super(connectionManager, tagSequencerConfig, true);
    }

    /** Executes the run program operation. */
    public void RunProgram(BaseSession appSession) throws AbortSessionException
    {
    }

    /** Executes the get program loader instance operation. */
    public static CalledProgramLoader GetProgramLoaderInstance()
    {
        return (CalledProgramLoader)ms_Instance ;
    }

    /** Executes the do help operation. */
    public void doHelp(CBaseMapFieldLoader fieldLoader, BaseSession session)
    {
    }
}
