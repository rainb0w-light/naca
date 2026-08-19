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
import nacaLib.varEx.VarDefNumEdited;
import nacaLib.varEx.VarLevel;

/** Provides declare type fpac num edited behavior. */
public class DeclareTypeFPacNumEdited extends DeclareTypeBase
{
    /** Creates a new declare type fpac num edited instance. */
    public DeclareTypeFPacNumEdited(VarLevel varLevel, String csMask)
    {
        super(varLevel);
        this.csMask = csMask;
    }

    public int getLength()
    {
        return csMask.length();
    }

    public String csMask = null;


    /** Executes the var operation. */
    public VarFPacNumEdited var()
    {
        VarFPacNumEdited var = new VarFPacNumEdited(this);
        return var;
    }

    /** Executes the filler operation. */
    public VarFPacNumEdited filler()
    {
        VarFPacNumEdited var = new VarFPacNumEdited(this);
        var.declareAsFiller();
        //return null;
        return var;
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefNumEdited(varDefParent, this);
        return varDef;
    }

    /**
     *
     */

    public DeclareTypeFPacNumEdited value(String cs)
    {
        initialValue = new CInitialValue(cs, false);
        return this;
    }

    /** Executes the value all operation. */
    public DeclareTypeFPacNumEdited valueAll(char c)
    {
        initialValue = new CInitialValue(c, true);
        return this;
    }

    /** Executes the value all operation. */
    public DeclareTypeFPacNumEdited valueAll(String cs)
    {
        initialValue = new CInitialValue(cs, true);
        return this;
    }

    /** Executes the value spaces operation. */
    public DeclareTypeFPacNumEdited valueSpaces()
    {
        initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
        return this;
    }

    /** Executes the value zero operation. */
    public DeclareTypeFPacNumEdited valueZero()
    {
        initialValue = new CInitialValue(CobolConstant.Zero.getValue(), true);
        return this;
    }

    /** Executes the value high value operation. */
    public DeclareTypeFPacNumEdited valueHighValue()
    {
        initialValue = new CInitialValue(CobolConstant.HighValue.getValue(), true);
        return this;
    }

    /** Executes the value low value operation. */
    public DeclareTypeFPacNumEdited valueLowValue()
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
