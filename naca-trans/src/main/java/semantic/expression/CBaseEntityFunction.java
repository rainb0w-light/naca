/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseDataReference;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public abstract class CBaseEntityFunction extends CBaseDataReference
{
    //protected CDataEntity dataRef = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        reference = null ;
    }

    /** Creates a new cbase entity function instance. */
    public CBaseEntityFunction(CObjectCatalog cat, CDataEntity data)
    {
        super(0, "", cat);
        reference = data ;
    }
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        if (reference != null)
        {
            return reference.GetDataType();
        }
        else
        {
            return null ;
        }
    }
    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#HasAccessors()
     */
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        if (reference != null)
        {
            return reference.ignore() ;
        }
        else
        {
            return false ;
        }
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
}
