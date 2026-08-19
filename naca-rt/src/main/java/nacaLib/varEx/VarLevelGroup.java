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
public class VarLevelGroup
{
    VarLevelGroup(VarLevel varLevel)
    {
        this.varLevel = varLevel;
    }

    /** Executes the var operation. */
    public VarGroup var()   // Creates a group
    {
        if (varLevel != null) {
            return varLevel.var();
        }
        return null;
    }

    /** Executes the filler operation. */
    public Var filler()
    {
        if (varLevel != null) {
            return varLevel.filler();
        }
        return null;
    }

    private VarLevel varLevel = null;
}
