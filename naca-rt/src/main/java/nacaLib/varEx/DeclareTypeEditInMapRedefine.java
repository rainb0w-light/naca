/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author PJD
 *
 */
public class DeclareTypeEditInMapRedefine extends DeclareTypeBase
{
    /** Creates a new declare type edit in map redefine instance. */
    public DeclareTypeEditInMapRedefine()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel)
    {
        super.set(varLevel);
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefEditInMapRedefine(varDefParent, this);
        return varDef;
    }

    public CInitialValue getInitialValue()
    {
        return null;
    }
}
