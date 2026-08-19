/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CIgnoreExternalEntity extends CEntityExternalDataStructure
{

    /**
     * @param l
     * @param name
     * @param cat
     * @param out
     */
    public CIgnoreExternalEntity(int l, String name, CObjectCatalog cat)
    {
        super(l, name, cat);
    }

    /* (non-Javadoc)
     * @see semantic.CBaseExternalEntity#GetTypeDecl()
     */
    public String GetTypeDecl()
    {
        return "";
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetDataType()
     */
    public CDataEntityType GetDataType()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#HasAccessors()
     */
    public boolean HasAccessors()
    {
        return false;
    }

    public boolean isValNeeded()
    {
        return true;
    }


    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#ignore()
     */
    public boolean ignore()
    {
        return true ;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetConstantValue()
     */
    public String GetConstantValue()
    {
        return "" ;
    }
    public int getActualSubLevel()
    {
        return 0 ;
    }
}
