/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;


/** Provides sort descriptor behavior. */
public class SortDescriptor extends BaseFileDescriptor
{
    /** Creates a new sort descriptor instance. */
    public SortDescriptor()
    {
        super();
    }

    void fillRecord(byte[] tBytes)
    {
        int nOffset = 0;
        int nLength = tBytes.length;
        if(hasVarVariableLengthMarker())    // The sort descriptor is of variable length
        {
            nOffset = 4;    // Skip leading variable length
            nLength -= 4;
        }

        varLevel01.setFromByteArray(tBytes, nOffset, nLength);
    }

    /** Executes the move into operation. */
    public void moveInto(Var varInto)
    {
        varLevel01.transferTo(varInto);
    }

    protected boolean doClose()
    {
        return true;
    }

    protected FileDescriptor doOpenInput()
    {
        return null;
    }

    protected FileDescriptor doOpenOutput()
    {
        return null;
    }

    /** Executes the length depending on operation. */
    public SortDescriptor lengthDependingOn(Var varLengthDependingOn)
    {
        setVarLengthDependingOn(varLengthDependingOn);
        return this;
    }
}
