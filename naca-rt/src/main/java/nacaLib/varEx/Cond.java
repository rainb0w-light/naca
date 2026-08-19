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

/** Provides cond behavior. */
public class Cond extends CJMapObject
{
    /** Creates a new cond instance. */
    public Cond(Var varParent, DeclareTypeCond declareTypeCond)
    {
        this.var = varParent;
        values = declareTypeCond.values;
    }

    public String getSTCheckValue()
    {
        return toString();
    }

    private Cond(Var varParent, Cond condValue)
    {
        this.var = varParent;
        values = condValue.values;
    }

    /** Sets the true. */
    public void setTrue()
    {
        int nNbValues = values.size();
        if(nNbValues > 0)
        {
            CondValue condValue = (CondValue) values.get(0);
            String s = condValue.getMin();
            if (s != null) {
                var.set(s);
            }
        }
    }

    /** Sets the false. */
    public void setFalse()
    {
        String[] candidates = {"0", "1", " ", "__NACA_CONDITION_FALSE__"};
        for (String candidate : candidates)
        {
            var.set(candidate);
            if (!is())
            {
                return;
            }
        }
        throw new IllegalStateException("No value outside the level-88 condition range");
    }
    /** Executes the is operation. */
    public boolean is()
    {
        int nNbValues = values.size();
        for(int n=0; n<nNbValues; n++)
        {
            CondValue condValue = (CondValue) values.get(n);
            if (condValue.is(var)) {
                return true;
            }
        }
        return false;
    }

    /** Returns the at. */
    public Cond getAt(Var xCmaj)
    {
        return getAt(xCmaj.getInt());
    }

    /** Returns the at. */
    public Cond getAt(int xCmaj)   // 1 based
    {
        Var var = this.var.getAt(xCmaj);
        return new Cond(var, this);
    }

    /** Returns the at. */
    public Cond getAt(VarAndEdit x, VarAndEdit y)
    {
        return getAt(x.getInt(), y.getInt());
    }

    /** Returns the at. */
    public Cond getAt(VarAndEdit x, int y)
    {
        return getAt(x.getInt(), y);
    }

    /** Returns the at. */
    public Cond getAt(int x, VarAndEdit y)
    {
        return getAt(x, y.getInt());
    }

    /** Returns the at. */
    public Cond getAt(int x, int y)
    {
        return new Cond(var.getAt(x, y), this);
    }

    public void setName(String csName)
    {
        this.csName = csName;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        String cs = "Cond {";
        for(int n = 0; n< values.size(); n++)
        {
            if (n != 0) {
                cs += "; ";
            }
            CondValue condValue = (CondValue) values.get(n);
            cs += condValue.toString();
        }
        cs += "}";
        return cs;
    }

    @SuppressWarnings("unused")
    private String csName = null;
    private Var var = null;
    private ArrayList<CondValue> values = null; // Array of CondValue
}
