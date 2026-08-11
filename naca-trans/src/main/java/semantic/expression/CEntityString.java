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
	public CEntityString(CObjectCatalog cat, char[] val)
	{
		super(0, "", cat);
		carrValue= val ;
	}
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.STRING;
	}
	public String getLiteralValue()
	{
		return new String(carrValue);
	}
	public boolean HasAccessors()
	{
		return false;
	}
	public boolean ignore()
	{
		return false ;
	}
	public boolean isValNeeded()
	{
		return false;
	}
	public String GetConstantValue()
	{
		return new String(carrValue);
	}
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		return null ;
	}

}
