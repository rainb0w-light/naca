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
    /** Executes the get type decl operation. */
    public String GetTypeDecl()
    {
        return "";
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetDataType()
     */
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#HasAccessors()
     */
    /** Executes the has accessors operation. */
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
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return true ;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetConstantValue()
     */
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
    public int getActualSubLevel()
    {
        return 0 ;
    }
}
