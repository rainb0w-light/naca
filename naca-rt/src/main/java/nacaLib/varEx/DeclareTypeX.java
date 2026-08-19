/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author PJD
 *
 */
package nacaLib.varEx;


/** Provides declare type x behavior. */
public class DeclareTypeX extends DeclareTypeBase
{
    private int nLength = 0;
    private boolean isjustifyRight = false;
    private CInitialValue initialValue = null;

    /** Creates a new declare type x instance. */
    public DeclareTypeX()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel, int nLength)
    {
        super.set(varLevel);
        this.nLength = nLength;
        this.isjustifyRight = false;
        this.initialValue = null;
    }

    int getLength()
    {
        return nLength;
    }

    boolean getJustifyRight()
    {
        return isjustifyRight;
    }

    /** Executes the var operation. */
    public VarAlphaNum var()
    {
        VarAlphaNum var2X = new VarAlphaNum(this);
        return var2X;
    }

    /** Executes the filler operation. */
    public VarAlphaNum filler()
    {
        VarAlphaNum var2X = new VarAlphaNum(this);
        var2X.declareAsFiller();
        //return null;
        return var2X;
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefX(varDefParent, this);
        return varDef;
    }

    /**
     *
     */

    public DeclareTypeX value(String cs)
    {
        if (getProgramManager().isFirstInstance()) {
            initialValue = new CInitialValue(cs, false);
        }
        return this;
    }

    /** Executes the value all operation. */
    public DeclareTypeX valueAll(char c)
    {
        if (getProgramManager().isFirstInstance()) {
            initialValue = new CInitialValue(c, true);
        }
        return this;
    }

    /** Executes the value all operation. */
    public DeclareTypeX valueAll(String cs)
    {
        if (getProgramManager().isFirstInstance()) {
            initialValue = new CInitialValue(cs, true);
        }
        return this;
    }

    /** Executes the value spaces operation. */
    public DeclareTypeX valueSpaces()
    {
        //initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
        if (getProgramManager().isFirstInstance()) {
            initialValue = CInitialValueStd.Spaces;
        }
        return this;
    }

    /** Executes the value zero operation. */
    public DeclareTypeX valueZero()
    {
        //initialValue = new CInitialValue(CobolConstant.Zero.getValue(), true);
        if (getProgramManager().isFirstInstance()) {
            initialValue = CInitialValueStd.Zero;
        }
        return this;
    }

    /** Executes the value high value operation. */
    public DeclareTypeX valueHighValue()
    {
        //initialValue = new CInitialValue(CobolConstant.HighValue.getValue(), true);
        if (getProgramManager().isFirstInstance()) {
            initialValue = CInitialValueStd.HighValue;
        }
        return this;
    }

    /** Executes the value low value operation. */
    public DeclareTypeX valueLowValue()
    {
        //initialValue = new CInitialValue(CobolConstant.LowValue.getValue(), true);
        if (getProgramManager().isFirstInstance()) {
            initialValue = CInitialValueStd.LowValue;
        }
        return this;
    }

    public CInitialValue getInitialValue()
    {
        return initialValue;
    }

    /** Executes the justify right operation. */
    public DeclareTypeX justifyRight()
    {
        isjustifyRight = true;
        return this ;
    }
}
