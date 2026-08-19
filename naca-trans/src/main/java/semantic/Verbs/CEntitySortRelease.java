/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/** Provides centity sort release behavior. */
public class CEntitySortRelease extends CBaseActionEntity
{

    /** Creates a new centity sort release instance. */
    public CEntitySortRelease(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    protected CDataEntity eSortField = null ;

    public void setDataReference(CDataEntity e)
    {
        eSortField = e ;
    }

    protected CDataEntity eDatReference = null ;
    /** Sets the data reference. */
    public void setDataReference(CDataEntity e, CDataEntity from)
    {
        eDatReference = from ;
        eSortField = e ;
    }

    public CDataEntity getSortField() {
        return eSortField;
    }
    public CDataEntity getDatReference() {
        return eDatReference;
    }
}
