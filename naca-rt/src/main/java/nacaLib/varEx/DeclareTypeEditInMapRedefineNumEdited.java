/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author U930DI
 *
 */
public class DeclareTypeEditInMapRedefineNumEdited extends DeclareTypeBase
{
    private String csNumEditedFormat = null;
    private boolean isblankWhenZero = false;

    /** Creates a new declare type edit in map redefine num edited instance. */
    public DeclareTypeEditInMapRedefineNumEdited()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel, String csFormat, boolean bBlankWhenZero)
    {
        super.set(varLevel);
        this.csNumEditedFormat = csFormat;
        this.isblankWhenZero = bBlankWhenZero;
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefEditInMapRedefineNumEdited(varDefParent, this);
        return varDef;
    }

    public CInitialValue getInitialValue()
    {
        return null;
    }

    String getNumEditedFormat()
    {
        return csNumEditedFormat;
    }

    boolean getBlankWhenZero()
    {
        return isblankWhenZero;
    }

}
