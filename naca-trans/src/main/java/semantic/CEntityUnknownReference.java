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
public class CEntityUnknownReference extends CDataEntity
{
    /**
     * @see semantic.CDataEntity#GetDataType()
     */
    @Override
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.UNKNWON ;
    }
    /** Creates a new centity unknown reference instance. */
    public CEntityUnknownReference(int nLine, String csName, CObjectCatalog cat)
    {
        super(nLine, csName, cat);
    }

    /**
     * An unresolved identifier has no usable semantic reference. Returning null
     * preserves the legacy fail-closed value for direct callers while the ST4
     * binding deliberately renders no source.
     */
    @Override
    protected String getSemanticReference()
    {
        return null;
    }

    @Override
    public boolean HasAccessors()
    {
        return false;
    }

    @Override
    public boolean isValNeeded()
    {
        return false;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ; // maybe true
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
}
