/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import nacaLib.varEx.Var;
import nacaLib.varEx.VarBuffer;

/** Provides var fpac num int sign comp3 length undef behavior. */
public class VarFPacNumIntSignComp3LengthUndef extends VarFPacLengthUndef
{
    /** Creates a new var fpac num int sign comp3 length undef instance. */
    public VarFPacNumIntSignComp3LengthUndef(FPacVarManager fpacVarManager, VarBuffer varBuffer, int nAbsolutePosition1Based)
    {
        super(fpacVarManager, varBuffer, nAbsolutePosition1Based);
    }

    /** Creates the var. */
    public Var createVar(int nBufferSize)
    {
        Var v = fpacVarManager.createFPacVarNumIntSignComp3(varBuffer, nAbsolutePosition1Based, nBufferSize);
        return v;
    }

    /** Creates the var. */
    public Var createVar()
    {
        Var v = fpacVarManager.createFPacVarNumIntSignComp3(varBuffer, nAbsolutePosition1Based, 8);
        return v;
    }

    int getParamLength(String cs)
    {
        return 8;
    }

    int getParamLength(int n)
    {
        return 8;
    }

    int getParamLength(Var varSource)
    {
        return 8;
    }
}
