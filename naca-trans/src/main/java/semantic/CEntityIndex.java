/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityIndex extends CDataEntity
{
    /**
     * @param l
     * @param name
     * @param cat
     */
    public CEntityIndex(String name, CObjectCatalog cat)
    {
        super(0, name, cat);
    }

    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.VAR ;
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

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        return null ;
    }
}
