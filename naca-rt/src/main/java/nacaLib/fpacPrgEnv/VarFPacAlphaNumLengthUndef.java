/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import nacaLib.varEx.Var;
import nacaLib.varEx.VarBuffer;

/** Provides var fpac alpha num length undef behavior. */
public class VarFPacAlphaNumLengthUndef extends VarFPacLengthUndef
{
    /** Creates a new var fpac alpha num length undef instance. */
    public VarFPacAlphaNumLengthUndef(FPacVarManager fpacVarManager, VarBuffer varBuffer, int nAbsolutePosition1Based)
    {
        super(fpacVarManager, varBuffer, nAbsolutePosition1Based);
    }

    /** Creates the var. */
    public Var createVar(int nBufferSize)
    {
        return fpacVarManager.createFPacVarAlphaNum(varBuffer, nAbsolutePosition1Based, nBufferSize);
    }

    /** Creates the var. */
    public Var createVar()
    {
        return fpacVarManager.createFPacVarAlphaNum(varBuffer, nAbsolutePosition1Based, 100);
    }

    int getParamLength(String cs)
    {
        return cs.length();
    }

    int getParamLength(int n)
    {
        if (n < 0) {
            n = -n;
        }
        String cs = String.valueOf(n);
        return cs.length();
    }

    int getParamLength(Var varSource)
    {
        return varSource.getLength();
    }
}
