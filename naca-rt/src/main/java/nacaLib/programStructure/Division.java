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
package nacaLib.programStructure;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseProgram;

/** Provides division behavior. */
public class Division extends CJMapObject
{
    Division(BaseProgram newProgram)
    {
        this.program = newProgram;
    }

    public BaseProgram getProgram()
    {
        return program;
    }


    protected BaseProgram program = null;
}
