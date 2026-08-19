/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityDataSection extends CBaseLanguageEntity
{
    /**
     * @param line
     * @param name
     * @param cat
     */
    public CEntityDataSection(int line, String name, CObjectCatalog cat)
    {
        super(line, name, cat);
        if (name.equals("LinkageSection"))
        {
            cat.RegisterLinkageSection(this) ;
        }
        else if (name.equals("WorkingStorageSection"))
        {
            cat.RegisterWorkingSection(this) ;
        }
    }
    protected void RegisterMySelfToCatalog()
    {
        // nothing
    }

    /** Executes the get internal level operation. */
    public int GetInternalLevel()
    {
        return 0 ;
    }
    public CEntityProcedureSection getSectionContainer()
    {
        return null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return lstChildren.isEmpty() ;
    }

    public boolean isWorkingStorageSection()
    {
        return "WorkingStorageSection".equals(GetName());
    }

    public boolean isLinkageSection()
    {
        return "LinkageSection".equals(GetName());
    }

    public boolean isFileSection()
    {
        return "FileSection".equals(GetName());
    }

    public boolean isVariableSection()
    {
        return "VariableSection".equals(GetName());
    }

}
