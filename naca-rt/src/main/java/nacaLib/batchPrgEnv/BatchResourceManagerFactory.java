/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.batchPrgEnv;

/** Provides batch resource manager factory behavior. */
public class BatchResourceManagerFactory
{
    private static BatchResourceManager ms_instance = null;

    /** Executes the get instance operation. */
    public static BatchResourceManager GetInstance(String csIniFilePath)
    {
        if(ms_instance == null)
        {
            ms_instance = new BatchResourceManager();
            ms_instance.initialize(csIniFilePath);
        }
        return ms_instance;
    }

    /** Executes the get instance operation. */
    public static BatchResourceManager GetInstance(String csIniFilePath, String csDBParameterPrefix)
    {
        if(ms_instance == null)
        {
            ms_instance = new BatchResourceManager();
            ms_instance.initialize(csIniFilePath, csDBParameterPrefix);
        }
        return ms_instance;
    }
}
