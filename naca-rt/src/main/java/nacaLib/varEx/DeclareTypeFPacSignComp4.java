/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/** Provides declare type fpac sign comp4 behavior. */
public class DeclareTypeFPacSignComp4 extends DeclareType9
{
    /** Creates a new declare type fpac sign comp4 instance. */
    public DeclareTypeFPacSignComp4()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel, int nNbDigitIntegerPart)
    {
        set(varLevel, true, nNbDigitIntegerPart, 0);
        comp();
    }
}
