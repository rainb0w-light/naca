/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.callPrg;

import nacaLib.basePrgEnv.BaseCESMManager;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.varEx.VarSectionDeclaration;

/** Provides called program behavior. */
public class CalledProgram extends BaseProgram
{
    private static CalledProgramManagerFactory ms_callProgramManagerFactory = new CalledProgramManagerFactory();

    /** Creates a new called program instance. */
    public CalledProgram()
    {
        super(ms_callProgramManagerFactory);
        declare = new VarSectionDeclaration(this);
    }

    void prepareRunMain(BaseEnvironment e)
    {
        CESM = e.createCESMManager();
    }

    protected BaseCESMManager getCESM()
    {
        return CESM;
    }

    public BaseCESMManager CESM = null;
    protected VarSectionDeclaration declare = null;
}
