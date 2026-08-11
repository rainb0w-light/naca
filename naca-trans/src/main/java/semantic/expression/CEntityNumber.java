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
public class CEntityNumber extends CDataEntity
{

	protected String csValue = "" ;
	private boolean preserveSourceLexeme = false ;
	private boolean valueNeeded = true ;

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
	public boolean isPreserveSourceLexeme()
	{
		return preserveSourceLexeme ;
	}
	public void preserveSourceLexeme()
	{
		preserveSourceLexeme = true ;
	}
	public boolean isHexLiteral()
	{
		return preserveSourceLexeme && csValue.startsWith("0x") ;
	}
	public String getHexDigits()
	{
		return isHexLiteral() ? csValue.substring(2) : csValue ;
	}
	public void setValueNeeded(boolean valueNeeded)
	{
		this.valueNeeded = valueNeeded ;
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
		return valueNeeded;
	}
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		return null ;
	}
}
