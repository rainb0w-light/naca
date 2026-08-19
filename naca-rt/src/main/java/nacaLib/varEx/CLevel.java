/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author u930di
 *
 */
package nacaLib.varEx;


public class CLevel
{
    CLevel(VarDefBuffer varDef, int nLevel)
    {
        this.nLevel = nLevel;
        this.varDef = varDef;
    };

    void setWith(CLevel levelSource)
    {
        nLevel = levelSource.nLevel;
        varDef = levelSource.varDef;
    }

    VarDefBuffer getVarDef()
    {
        return varDef;
    }

    boolean hasLowerLevel(int nLevel)
    {
        if(this.nLevel < nLevel)
            return true;
        return false;
    }

    int nLevel = 0;
    private VarDefBuffer varDef = null ;
}
