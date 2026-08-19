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
public class InitializeManagerDoubleEdited extends InitializeManager
{
    private double value = 0.0;

    public InitializeManagerDoubleEdited(double d)
    {
        this.value = d;
    }

    public void set(double d)
    {
        this.value = d;
    }

    public void initialize(VarBufferPos buffer, VarDefBuffer varDef, int nOffset, InitializeCache initializeCache)
    {
        varDef.initializeEditedAtOffset(buffer, nOffset, value);
        if (initializeCache != null) {
            initializeCache.setNotManaged();
        }
    }
}
