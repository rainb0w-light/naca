/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

/**
 * @author PJD
 *
 */
public class DeclareTypeNumEdited extends DeclareTypeBase
{
    public DeclareTypeNumEdited()
    {
    }

    public void set(VarLevel varLevel, String csFormat)
    {
        super.set(varLevel);
        this.csFormat = csFormat;
        this.isblankWhenZero = false;
    }

    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefNumEdited(varDefParent, this);
        return varDef;
    }

    public VarNumEdited var()
    {
        VarNumEdited var = new VarNumEdited(this);
        return var;
    }

    public Edit edit()  // Edit in a map redefine
    {
        TempCache tempCache = TempCacheLocator.getTLSTempCache();
        DeclareTypeEditInMapRedefineNumEdited declareTypeEditInMapRedefineNumEdited = tempCache.getDeclareTypeEditInMapRedefineNumEdited();
        declareTypeEditInMapRedefineNumEdited.set(getLevel(), csFormat, isblankWhenZero);

        EditInMapRedefineNumEdited var2Edit = new EditInMapRedefineNumEdited(declareTypeEditInMapRedefineNumEdited);
        return var2Edit;
    }

    public VarNumEdited filler()
    {
        VarNumEdited var = new VarNumEdited(this);
        var.declareAsFiller();
        return null;
    }

    public DeclareTypeNumEdited value(double d)
    {
        initialValue = new CInitialValue(d, false);
        return this;
    }

    public DeclareTypeNumEdited value(int n)
    {
        initialValue = new CInitialValue(n, false);
        return this;
    }

    public DeclareTypeNumEdited value(String cs)
    {
        initialValue = new CInitialValue(cs, false);
        return this;
    }

    public CInitialValue getInitialValue()
    {
        return initialValue;
    }

    private CInitialValue initialValue = null;
    String csFormat = null;
    boolean isblankWhenZero = false;

    public DeclareTypeNumEdited valueZero()
    {
        initialValue = new CInitialValue(0, true);
        return this;
    }

    public DeclareTypeNumEdited blankWhenZero()
    {
        isblankWhenZero = true;
        return this;
    }

    /**
     * @return
     */
    public DeclareTypeNumEdited valueSpaces()
    {
        initialValue = new CInitialValue(' ', true);
        return this;
    }
}
