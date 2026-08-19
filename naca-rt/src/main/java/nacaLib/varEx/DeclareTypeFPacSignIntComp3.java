/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/** Provides declare type fpac sign int comp3 behavior. */
public class DeclareTypeFPacSignIntComp3 extends DeclareType9
{
    /** Creates a new declare type fpac sign int comp3 instance. */
    public DeclareTypeFPacSignIntComp3()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel, int nNbDigitIntegerPart)
    {
        set(varLevel, true, nNbDigitIntegerPart, 0);
        comp3();
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = numericValue.createVarDefFPacNum(varDefParent, this);
        return varDef;
    }
}
