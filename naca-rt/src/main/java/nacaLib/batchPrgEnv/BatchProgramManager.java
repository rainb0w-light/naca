/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.batchPrgEnv;

import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.tempCache.TempCacheLocator;

/** Provides batch program manager behavior. */
public class BatchProgramManager extends BaseProgramManager
{
    /** Creates a new batch program manager instance. */
    public BatchProgramManager(
        BaseProgram program,
        SharedProgramInstanceData sharedProgramInstanceData,
        boolean bInheritedSharedProgramInstanceData)
    {
        super(program, sharedProgramInstanceData, bInheritedSharedProgramInstanceData);

        BaseEnvironment env = TempCacheLocator.getTLSTempCache().getCurrentEnv();
        setEnv(env);
    }

    public String getTerminalID()
    {
        return "";
    }

    public void setEnv(BaseEnvironment env)
    {
        this.env = env;
    }

    /** Executes the detach from env operation. */
    public void detachFromEnv()
    {
        env = null;
    }

    public BaseEnvironment getEnv()
    {
        return env;
    }

    /** Executes the prepare run main operation. */
    public void prepareRunMain(BaseProgram prg)
    {
        if (prg instanceof BatchProgram) {
            ((BatchProgram) prg).prepareRunMain(env);
        }
    }

    private BaseEnvironment env = null;
}
