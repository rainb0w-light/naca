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

public abstract class VarDefEdit extends VarDefBuffer
{
    /** Creates a new var def edit instance. */
    public VarDefEdit(VarDefBase varDefParent, VarLevel varLevel)
    {
        super(varDefParent, varLevel);
    }

    protected VarDefEdit()
    {
        super();
    }

    void transfer(VarBufferPos bufferSource, Var dest)
    {
    }

    protected boolean isAVarDefMapRedefine()
    {
        return false;
    }

    public int getSingleItemRequiredStorageSize()
    {
        return getHeaderLength() + getBodyLength();
    }
}
