/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author u930di
 *
 */
public class DeclareTypeEditInMapRedefineNum extends DeclareTypeBase
{
    private NumericValue numericValue = null;

    /** Creates a new declare type edit in map redefine num instance. */
    public DeclareTypeEditInMapRedefineNum()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel, NumericValue numericValue)
    {
        super.set(varLevel);
        this.numericValue = numericValue;
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefEditInMapRedefineNum(varDefParent, this);
        return varDef;
    }

    public CInitialValue getInitialValue()
    {
        return null;
    }

    NumericValue getNumericValue()
    {
        return numericValue;
    }
}
