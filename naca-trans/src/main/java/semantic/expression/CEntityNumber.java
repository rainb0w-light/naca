/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityNumber extends CDataEntity
{

	protected String csValue = "" ;
	
	public CEntityNumber(CObjectCatalog cat, String number)
	{
		super(0, "", cat);
		csValue = number;
	}
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.NUMBER;
	}
	public String getLiteralValue()
	{
		return csValue;
	}
	public String GetConstantValue()
	{
		return csValue;
	}
	public boolean isDecimalLiteral()
	{
		return csValue.indexOf('.') >= 0;
	}
	public String getNormalizedLiteralValue()
	{
		if (isDecimalLiteral()) return csValue;
		try
		{
			return String.valueOf(Integer.parseInt(csValue));
		}
		catch (NumberFormatException integerError)
		{
			try
			{
				return String.valueOf(Long.parseLong(csValue));
			}
			catch (NumberFormatException longError)
			{
				return csValue;
			}
		}
	}
	public boolean isLongLiteral()
	{
		if (isDecimalLiteral()) return false;
		try
		{
			Integer.parseInt(csValue);
			return false;
		}
		catch (NumberFormatException integerError)
		{
			try
			{
				Long.parseLong(csValue);
				return true;
			}
			catch (NumberFormatException longError)
			{
				return false;
			}
		}
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
		return true;
	}
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		return null ;
	}
}
