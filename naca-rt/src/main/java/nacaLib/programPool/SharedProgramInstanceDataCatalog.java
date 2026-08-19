/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.programPool;

import java.util.Hashtable;

import nacaLib.basePrgEnv.BaseResourceManager;

/**
 * @author U930DI
 *
 */
public class SharedProgramInstanceDataCatalog
{
    public SharedProgramInstanceDataCatalog()
    {
    }

    static synchronized public SharedProgramInstanceData getSharedProgramInstanceData(String csSimpleName)
    {
        SharedProgramInstanceData s = ms_hashSharedProgramInstanceData.get(csSimpleName);
        return s;
    }

    static synchronized public void removeSharedProgramInstanceData(String csSimpleName)
    {
        SharedProgramInstanceData s = ms_hashSharedProgramInstanceData.get(csSimpleName);
        if(s != null)
        {
            int nNbForm = s.getNbVarDefForm();
            for(int n=0; n<nNbForm; n++)
            {
                String csFormName = s.getFormName(n);
                if(csFormName != null)
                    BaseResourceManager.removeResourceCache(csFormName);
            }
        }

        ms_hashSharedProgramInstanceData.remove(csSimpleName);
        s.prepareAutoRemoval();
    }

    static synchronized public void putSharedProgramInstanceData(String csSimpleName, SharedProgramInstanceData s)
    {
        ms_hashSharedProgramInstanceData.put(csSimpleName, s);
    }

    // hash table of SharedProgramInstanceData, indexed by program's simple name
    private static Hashtable<String, SharedProgramInstanceData> ms_hashSharedProgramInstanceData
            = new Hashtable<String, SharedProgramInstanceData>();
}
