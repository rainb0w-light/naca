/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import nacaLib.programPool.SharedProgramInstanceData;

/** Provides base program manager factory behavior. */
public abstract class BaseProgramManagerFactory
{
    /** Creates the program manager. */
    abstract public BaseProgramManager createProgramManager(
        BaseProgram prg,
        SharedProgramInstanceData sharedProgramInstanceData,
        boolean bInheritedSharedProgramInstanceData);
}
