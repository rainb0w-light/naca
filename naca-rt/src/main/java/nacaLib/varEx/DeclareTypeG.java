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
public class DeclareTypeG extends DeclareTypeBase
{
    public DeclareTypeG()
    {
    }

    public void set(VarLevel varLevel)
    {
        super.set(varLevel);
    }

    public VarGroup var()
    {
        VarGroup var2G = new VarGroup(this);
        return var2G;
    }

    public VarGroup filler()
    {
        VarGroup var2G = new VarGroup(this);
        var2G.declareAsFiller();
        return null;
    }

    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefG(varDefParent, this);
        return varDef;
    }

    public CInitialValue getInitialValue()
    {
        return getLevel().getInitialValue();
    }
}
