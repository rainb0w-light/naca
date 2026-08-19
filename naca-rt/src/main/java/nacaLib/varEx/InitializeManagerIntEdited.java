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
public class InitializeManagerIntEdited extends InitializeManager
{
    private int nValue = 0;

    /** Creates a new initialize manager int edited instance. */
    public InitializeManagerIntEdited(int nValue)
    {
        this.nValue = nValue;
    }

    /** Executes the set operation. */
    public void set(int nValue)
    {
        this.nValue = nValue;
    }

    /** Executes the initialize operation. */
    public void initialize(VarBufferPos buffer, VarDefBuffer varDef, int nOffset, InitializeCache initializeCache)
    {
        varDef.initializeEditedAtOffset(buffer, nOffset, nValue);
        if (initializeCache != null) {
            initializeCache.setNotManaged();
        }
    }
}
