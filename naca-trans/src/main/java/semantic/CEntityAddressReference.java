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
public class CEntityAddressReference extends CBaseDataReference
{
    /**
     * @param l
     * @param name
     * @param cat
     */
    public CEntityAddressReference(CObjectCatalog cat, CDataEntity ref)
    {
        super(0, "", cat);
        reference = ref ;
    }
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.ADDRESS ;
    }
//  protected CDataEntity reference = null ;

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return reference.ignore() ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }
    public boolean isValNeeded()
    {
        return false;
    }
}
