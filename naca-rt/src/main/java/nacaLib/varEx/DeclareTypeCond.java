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

import java.util.ArrayList;

import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;

/** Provides declare type cond behavior. */
public class DeclareTypeCond extends CJMapObject
{
    /** Creates a new declare type cond instance. */
    public DeclareTypeCond()
    {
    }

    /** Executes the set operation. */
    public void set(BaseProgram program)
    {
        programManager = program.getProgramManager();
        values = new ArrayList<CondValue>();
    }

    /** Executes the value operation. */
    public DeclareTypeCond value(String s)
    {
        CondValue condValue = new CondValue(s);
        values.add(condValue);
        return this;
    }

    /** Executes the value operation. */
    public DeclareTypeCond value(String sMin, String sMax)
    {
        CondValue condValue = new CondValue(sMin, sMax);
        values.add(condValue);
        return this;
    }

    /** Executes the value operation. */
    public DeclareTypeCond value(int nMin, int nMax)
    {
        String min = String.valueOf(nMin);
        String max = String.valueOf(nMax);

        CondValue condValue = new CondValue(min, max);
        values.add(condValue);
        return this;
    }

    /** Executes the value operation. */
    public DeclareTypeCond value(int n)
    {
        String s = String.valueOf(n);

        CondValue condValue = new CondValue(s);
        values.add(condValue);
        return this;
    }

    /** Executes the value operation. */
    public DeclareTypeCond value(CobolConstantBase constant)
    {
        CondValue condValue = new CondValue(constant);
        values.add(condValue);
        return this;
    }

    /** Executes the var operation. */
    public Cond var()
    {
        Var varParent = (Var)programManager.getLastVarCreated();

        Cond cond = new Cond(varParent, this);
        return cond;
    }

    ArrayList<CondValue> values = null;
    BaseProgramManager programManager = null;
}
