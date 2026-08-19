/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.CESM;

import jlib.log.Log;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.exceptions.CXctlException;
import nacaLib.misc.CCommarea;
import nacaLib.varEx.Var;

/** Provides cesmxctl behavior. */
public class CESMXctl extends CJMapObject
{
    protected BaseEnvironment environment = null;

    /** Creates a new cesmxctl instance. */
    public CESMXctl(BaseEnvironment env, String csProgramName)
    {
        environment = env ;
        environment.setNextProgramToLoad(csProgramName);
    }

    /** Executes the go operation. */
    public void go()
    {
        if (isLogCESM) {
            Log.logDebug("Xctling program: " + environment.getNextProgramToLoad());
        }
        CXctlException exp = new CXctlException(environment);
        throw exp ;
    }

    /** Executes the commarea operation. */
    public void commarea(Var var, int length)
    {
        CCommarea comm = new CCommarea() ;
        environment.setCommarea(comm);
        comm.setVarPassedByValue(var, length) ;
        if (isLogCESM) {
            Log.logDebug("Xctling program: " + environment.getNextProgramToLoad());
        }
        CXctlException exp = new CXctlException(environment) ;
        throw exp ;
    }

    /** Executes the commarea operation. */
    public void commarea(Var var)
    {
        commarea(var, -1) ;
    }

    /** Executes the commarea operation. */
    public void commarea(Var v, Var length)
    {
        int l = length.getInt() ;
        commarea(v, l) ;
    }

    /** Executes the commarea operation. */
    public void commarea(Var var, int length, int datalength)
    {
        commarea(var, length) ;
    }

    /** Executes the commarea operation. */
    public void commarea(Var var, Var length, int datalength)
    {
        int l = length.getInt() ;
        commarea(var, l) ;
    }
}
