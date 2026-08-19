/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import nacaLib.tempCache.CStr;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarBase;
import nacaLib.varEx.VarBuffer;
import nacaLib.varEx.VarBufferPos;
import nacaLib.varEx.VarType;
import nacaLib.varEx.VarTypeEnum;

/** Provides var fpac num edited behavior. */
public class VarFPacNumEdited extends Var
{
    /** Creates a new var fpac num edited instance. */
    public VarFPacNumEdited(DeclareTypeFPacNumEdited declareTypeFPacNumEdited, VarBuffer varBuffer, int nPosition)
    {
        super(declareTypeFPacNumEdited);
        bufferPos = new VarBufferPos(varBuffer, nPosition);
        varDef.setTotalSize(varDef.getSingleItemRequiredStorageSize());
    }

    /** Creates a new var fpac num edited instance. */
    public VarFPacNumEdited(DeclareTypeFPacNumEdited declareTypeFPacNumEdited)
    {
        super(declareTypeFPacNumEdited);
    }

    protected VarFPacNumEdited()
    {
        super();
    }

    protected VarBase allocCopy()
    {
        VarFPacAlphaNum v = new VarFPacAlphaNum();
        return v;
    }


    protected String getAsLoggableString()
    {
        CStr cstr = bufferPos.getOwnCStr(varDef.getLength());
        String cs = cstr.getAsString();
        //cstr.resetManagerCache();
        return cs;
    }

    /** Returns whether s type. */
    public boolean hasType(VarTypeEnum e)
    {
        if (e == VarTypeEnum.TypeX) {
            return true;
        }
        return false;
    }

    /** Executes the compare to operation. */
    public int compareTo(int nValue)
    {
        int nVarValue = getInt();
        return nVarValue - nValue;
    }

    /** Executes the compare to operation. */
    public int compareTo(double dValue)
    {
        double varValue = getDouble();
        double d = varValue - dValue;
        if (d < -0.00001) {    //Consider epsilon precision at 10 e-5
            return -1;
        } else if (d > 0.00001) {    //Consider epsilon precision at 10 e-5
            return 1;
        }
        return 0;
    }

    protected byte[] convertUnicodeToEbcdic(char[] tChars)
    {
        return doConvertUnicodeToEbcdic(tChars);
    }

    protected char[] convertEbcdicToUnicode(byte[] tBytes)
    {
        return doConvertEbcdicToUnicode(tBytes);
    }

    /** Creates the var fpac undef. */
    public VarFPacLengthUndef createVarFPacUndef(FPacVarManager fpacVarManager, VarBuffer varBuffer, int nAbsolutePosition)
    {
        return new VarFPacAlphaNumLengthUndef(fpacVarManager, varBuffer, nAbsolutePosition);
    }

    public VarType getVarType()
    {
        return VarType.VarFPacNumEdited;
    }
//
//  VarFPacUndef createVarFPacUndef(FPacVarManager fpacVarManager, VarBuffer varBuffer, int nAbsolutePosition)
//  {
//      return fpacVarManager.createFPacVarAlphaNum(varBuffer, nAbsolutePosition);
//  }
}
