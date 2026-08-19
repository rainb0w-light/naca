/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.callPrg;

import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.tempCache.TempCacheLocator;

/** Provides called program manager behavior. */
public class CalledProgramManager extends BaseProgramManager
{
    /** Creates a new called program manager instance. */
    public CalledProgramManager(
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
        eSMEnv = env;
    }

    /** Executes the detach from env operation. */
    public void detachFromEnv()
    {
        eSMEnv = null;
    }

    public BaseEnvironment getEnv()
    {
        return eSMEnv;
    }

    /** Executes the prepare run main operation. */
    public void prepareRunMain(BaseProgram prg)
    {
        ((CalledProgram)prg).prepareRunMain(eSMEnv);
    }

    private BaseEnvironment eSMEnv = null;
}
