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
public class InitializeManagerString extends InitializeManager
{
    public InitializeManagerString(String cs)
    {
        this.cs = cs;
    }

    public void set(String cs)
    {
        this.cs = cs;
    }

//  public void initialize(VarBufferPos buffer, VarDefBuffer varDef)
//  {
//      varDef.initialize(buffer, cs);
//  }

    public void initialize(VarBufferPos buffer, VarDefBuffer varDef, int nOffset, InitializeCache initializeCache)
    {
        varDef.initializeAtOffset(buffer, nOffset, cs);
        if (initializeCache != null) {
            initializeCache.setNotManaged();
        }
    }



    String getString()
    {
        return cs;
    }

    private String cs;
}
