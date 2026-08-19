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
public class DeclareTypeMapRedefine extends DeclareTypeBase
{
    private int nLength = 0;
    Form formRedefineOrigin = null;

    public DeclareTypeMapRedefine()
    {
    }

    public void set(VarLevel varLevel, Form formRedefineOrigin)
    {
        super.set(varLevel);
        this.formRedefineOrigin = formRedefineOrigin;
        this.nLength = 0;
    }

    int getLength()
    {
        return nLength;
    }

    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefMapRedefine varDef = new VarDefMapRedefine(varDefParent, this);
        varDef.varDefFormRedefineOrigin = formRedefineOrigin.getDefForm();
        return varDef;
    }

    public CInitialValue getInitialValue()
    {
        return null;
    }
}
