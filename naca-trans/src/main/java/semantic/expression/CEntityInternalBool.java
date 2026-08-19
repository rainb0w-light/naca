/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityInternalBool extends CDataEntity
{

    /**
     * @param l
     * @param name
     * @param cat
     */
    public CEntityInternalBool(String name, CObjectCatalog cat)
    {
        super(0, name, cat);
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetDataType()
     */
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.VAR ;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#HasAccessors()
     */
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetConstantValue()
     */
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }

    public boolean isValNeeded()
    {
        return true;
    }

}
