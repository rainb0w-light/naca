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
public class InitializeManagerDouble extends InitializeManager
{
    public InitializeManagerDouble(String csd)
    {
        this.csd = csd;
    }

    public void set(String csd)
    {
        this.csd = csd;
    }

    public void initialize(VarBufferPos buffer, VarDefBuffer varDef, int nOffset, InitializeCache initializeCache)
    {
        //varDef.write(buffer, csd);
        varDef.write(buffer, csd, nOffset, csd.length());
        if(initializeCache != null)
            initializeCache.setNotManaged();
    }

    private String csd;
}
