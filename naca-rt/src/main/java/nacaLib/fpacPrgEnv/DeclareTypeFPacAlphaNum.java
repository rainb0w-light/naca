/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import nacaLib.varEx.CInitialValue;
import nacaLib.varEx.CobolConstant;
import nacaLib.varEx.DeclareTypeBase;
import nacaLib.varEx.VarDefBuffer;
import nacaLib.varEx.VarDefFPacAlphaNum;
import nacaLib.varEx.VarLevel;

/** Provides declare type fpac alpha num behavior. */
public class DeclareTypeFPacAlphaNum extends DeclareTypeBase
{
    /** Creates a new declare type fpac alpha num instance. */
    public DeclareTypeFPacAlphaNum(VarLevel varLevel, int nLength)
    {
        super(varLevel);
        this.nLength = nLength;
    }

    public int getLength()
    {
        return nLength;
    }

    private int nLength = 0;


    /** Executes the var operation. */
    public VarFPacAlphaNum var()
    {
        VarFPacAlphaNum var = new VarFPacAlphaNum(this);
        return var;
    }

    /** Executes the filler operation. */
    public VarFPacAlphaNum filler()
    {
        VarFPacAlphaNum var = new VarFPacAlphaNum(this);
        var.declareAsFiller();
        //return null;
        return var;
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefFPacAlphaNum(varDefParent, this);
        return varDef;
    }

    /**
     *
     */

    public DeclareTypeFPacAlphaNum value(String cs)
    {
        initialValue = new CInitialValue(cs, false);
        return this;
    }

    /** Executes the value all operation. */
    public DeclareTypeFPacAlphaNum valueAll(char c)
    {
        initialValue = new CInitialValue(c, true);
        return this;
    }

    /** Executes the value all operation. */
    public DeclareTypeFPacAlphaNum valueAll(String cs)
    {
        initialValue = new CInitialValue(cs, true);
        return this;
    }

    /** Executes the value spaces operation. */
    public DeclareTypeFPacAlphaNum valueSpaces()
    {
        initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
        return this;
    }

    /** Executes the value zero operation. */
    public DeclareTypeFPacAlphaNum valueZero()
    {
        initialValue = new CInitialValue(CobolConstant.Zero.getValue(), true);
        return this;
    }

    /** Executes the value high value operation. */
    public DeclareTypeFPacAlphaNum valueHighValue()
    {
        initialValue = new CInitialValue(CobolConstant.HighValue.getValue(), true);
        return this;
    }

    /** Executes the value low value operation. */
    public DeclareTypeFPacAlphaNum valueLowValue()
    {
        initialValue = new CInitialValue(CobolConstant.LowValue.getValue(), true);
        return this;
    }

    public CInitialValue getInitialValue()
    {
        return initialValue;
    }

    private CInitialValue initialValue = null;
}
