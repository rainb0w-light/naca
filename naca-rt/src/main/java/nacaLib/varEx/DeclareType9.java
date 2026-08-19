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
package nacaLib.varEx;

import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;


/** Provides declare type9 behavior. */
public class DeclareType9 extends DeclareTypeBase
{
    protected NumericValue numericValue = new NumericValue();
    private CInitialValue initialValue = null;
    boolean isblankWhenZero = false;

    /** Creates a new declare type9 instance. */
    public DeclareType9()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel, boolean bSigned, int nNbDigitInteger, int nNbDigitDecimal)
    {
        super.set(varLevel);
        numericValue.set(bSigned, nNbDigitInteger, nNbDigitDecimal);
        this.initialValue = null;
        this.isblankWhenZero = false;
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = numericValue.createVarDef(varDefParent, this);
        return varDef;
    }

    /** Executes the var operation. */
    public VarNum var()
    {
        VarNum var = numericValue.createVar(this);
        return var;
    }

    /** Executes the filler operation. */
    public VarNum filler()
    {
        VarNum var = numericValue.createVar(this);
        var.declareAsFiller();
        return null;
    }

    /** Executes the sign leading separated operation. */
    public DeclareType9 signLeadingSeparated()
    {
        numericValue.setSignLeadingSeparated(true);
        return this;
    }

    /** Executes the sign trailing separated operation. */
    public DeclareType9 signTrailingSeparated()
    {
        numericValue.setSignLeadingSeparated(false);
        return this;
    }

    /** Executes the comp3 operation. */
    public DeclareType9 comp3()
    {
        numericValue.nComp = -3;
        return this;
    }

    /** Executes the comp operation. */
    public DeclareType9 comp()
    {
        numericValue.nComp = -4;
        return this;
    }

    /** Executes the comp5 operation. */
    public DeclareType9 comp5()
    {
        numericValue.nComp = -5;
        return this;
    }

    /** Executes the value operation. */
    public DeclareType9 value(double d)
    {
        if (getProgramManager().isFirstInstance()) {
            initialValue = new CInitialValue(d, false);
        }
        return this;
    }

    /** Executes the value operation. */
    public DeclareType9 value(String s)
    {
        if (getProgramManager().isFirstInstance()) {
            initialValue = new CInitialValue(s, false);
        }
        return this;
    }

    /** Executes the value operation. */
    public DeclareType9 value(int n)
    {
        if (getProgramManager().isFirstInstance()) {
            initialValue = new CInitialValue(n, false);
        }
        return this;
    }

    /** Executes the value spaces operation. */
    public DeclareType9 valueSpaces()
    {
        //initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
        if (getProgramManager().isFirstInstance()) {
            initialValue = CInitialValueStd.Spaces;
        }
        return this;
    }

    //
//  // private VarLevelManager varLevelManager = null;
//
//
    /** Executes the value zero operation. */
    public DeclareType9 valueZero()
    {
        //initialValue = new CInitialValue(CobolConstant.Zero.getValue(), false);
        if (getProgramManager().isFirstInstance()) {
            initialValue = new CInitialValue(0, false);
        }
        return this ;
    }

    /** Executes the sync operation. */
    public DeclareType9 sync()
    {
        return this;
    }

    public CInitialValue getInitialValue()
    {
        return initialValue;
    }



    /** Executes the blank when zero operation. */
    public DeclareType9 blankWhenZero()
    {
        isblankWhenZero = true;
        return this;
    }

    /**
     * @return
     */
    public Edit edit()
    {
        TempCache tempCache = TempCacheLocator.getTLSTempCache();
        DeclareTypeEditInMapRedefineNum declareTypeEditInMapRedefineNum = tempCache.getDeclareTypeEditInMapRedefineNum();
        declareTypeEditInMapRedefineNum.set(getLevel(), numericValue);

        EditInMapRedefineNum var2Edit = new EditInMapRedefineNum(declareTypeEditInMapRedefineNum);
        return var2Edit;
    }
}
