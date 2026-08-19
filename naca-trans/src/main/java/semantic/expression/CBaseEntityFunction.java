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
    public void Clear()
    {
        super.Clear() ;
        reference = null ;
    }

    public CBaseEntityFunction(CObjectCatalog cat, CDataEntity data)
    {
        super(0, "", cat);
        reference = data ;
    }
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
    public boolean HasAccessors()
    {
        return false;
    }
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
    public String GetConstantValue()
    {
        return "" ;
    }
}
