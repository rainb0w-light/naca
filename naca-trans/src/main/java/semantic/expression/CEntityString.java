/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityString extends CDataEntity
{
    protected char[] carrValue = {} ;
    /** Creates a new centity string instance. */
    public CEntityString(CObjectCatalog cat, char[] val)
    {
        super(0, "", cat);
        carrValue= val ;
    }
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.STRING;
    }
    public String getLiteralValue()
    {
        return new String(carrValue);
    }
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    public boolean isValNeeded()
    {
        return false;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return new String(carrValue);
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
