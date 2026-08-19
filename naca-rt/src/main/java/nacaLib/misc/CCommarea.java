/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package nacaLib.misc;

import nacaLib.varEx.CCallParam;
import nacaLib.varEx.CallParamByCharBuffer;
import nacaLib.varEx.CallParamByRef;
import nacaLib.varEx.CallParamFpac;
import nacaLib.varEx.Form;
import nacaLib.varEx.InternalCharBuffer;
import nacaLib.varEx.Var;
import nacaLib.base.CJMapObject;

/** Provides ccommarea behavior. */
public class CCommarea extends CJMapObject
{
    /** Creates a new ccommarea instance. */
    public CCommarea()
    {
    }

    /** Sets the var passed by value. */
    public void setVarPassedByValue(Var var, int length)
    {
        if (var.getLength() < length)
        {
            length = var.getLength();
        }
        charBufferCopy = var.exportToCharBuffer(length);
        var = null;
        isbyValue = true;
    }
    /** Sets the var passed by value. */
    public void setVarPassedByValue(InternalCharBuffer buff)
    {
        charBufferCopy = buff ;
        var = null;
        isbyValue = true;
    }

    /** Sets the var passed by value. */
    public void setVarPassedByValue(Form form)
    {
        charBufferCopy = form.encodeToCharBuffer();
        var = null;
        isbyValue = true;
    }

    /** Sets the var passed by ref. */
    public void setVarPassedByRef(Var var)
    {
        charBufferCopy = null;
        this.var = var;
        isbyValue = false;
    }

    void setLength(int nLength)
    {
        this.nLength = nLength;
        islengthSpecified = true;
    }

    /** Returns the length. */
    public int getLength()
    {
        if(var != null)
        {
            if (islengthSpecified) {
                return nLength;
            }
            return var.getLength();
        }
        if(charBufferCopy != null)
        {
            if (islengthSpecified) {
                return nLength;
            }
            return charBufferCopy.getBufferSize();
        }
        return 0;
    }

    /** Builds the call param. */
    public CCallParam buildCallParam()
    {
        if(var != null) // By ref
        {
            CallParamByRef callParam = new CallParamByRef(var);
            return callParam;
        }
        if(charBufferCopy != null)  // By value
        {
            CallParamByCharBuffer callParam = new CallParamByCharBuffer(charBufferCopy);
            return callParam;
        }
        return null;
    }

    /** Builds the call param fpac. */
    public CallParamFpac buildCallParamFPac()
    {
        if(charBufferCopy != null)  // By value
        {
            CallParamFpac callParam = new CallParamFpac(charBufferCopy);
            return callParam;
        }
        return null;
    }

    private boolean isbyValue = false;
    private Var var = null;
    private InternalCharBuffer charBufferCopy = null;
    private int nLength = 0;
    private boolean islengthSpecified = false;
    /**
     * @param varDest
     */
//  public CCallParam GetParam()
//  {
//      CallParamByCharBuffer param = new CallParamByCharBuffer(charBufferCopy) ;
//      return param ;
//      //param.MapOn(varDest) ;
//  }
}
