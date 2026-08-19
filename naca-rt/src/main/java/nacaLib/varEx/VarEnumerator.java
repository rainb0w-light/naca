/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import nacaLib.basePrgEnv.BaseProgramManager;


/**
 * @author U930DI
 *
 */
public class VarEnumerator
{
    public VarEnumerator(BaseProgramManager programManager, VarBase var)
    {
        this.programManager = programManager;
        this.var = var;
        if (var != null) {
            varDef = var.getVarDef();
        }
    }

    public VarBase getFirstVarChild()
    {
        nIndex = 0;
        return getNextVarChild();
    }

    public VarBase getNextVarChild()
    {
        VarBase v = getChildAtIndex(nIndex);
        nIndex++;
        return v;
    }

    VarBase getChildAtIndex(int nIndex)
    {
        if(varDef != null)
        {
            VarDefBase varDefChild = varDef.getChild(nIndex);
            if(varDefChild != null)
            {
                VarBase varChild = programManager.getVarFullName(varDefChild);
                return varChild;
            }
        }
        return null;
    }

    private int nIndex = 0;
    private VarBase var = null;
    private VarDefBase varDef = null;
    BaseProgramManager programManager = null;
}
