/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.Vector;


import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondIsConstant;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityEnvironmentVariable extends CDataEntity
{
	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntityEnvironmentVariable(int l, String name, CObjectCatalog cat, String accessor, String writer, boolean bNumericVar)
	{
		super(l, name, cat);
		csAccessor = accessor ;
		csWriteAccessor = writer ;
		isnumericVariable = bNumericVar ;
	}

	protected String csAccessor = "" ;
	protected String csWriteAccessor = "" ;
	protected boolean isnumericVariable = false ;

	public String getReadAccessor()
	{
		return csAccessor;
	}

	public String getWriteAccessor()
	{
		return csWriteAccessor;
	}

	public boolean isNumericVariable()
	{
		return isnumericVariable;
	}

	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		CEntityCondIsConstant eCond = factory.NewEntityCondIsConstant() ;
		if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eCond.SetIsZero(this);
		}
		else if (value.equals("SPACE") || value.equals("SPACES"))
		{
			eCond.SetIsSpace(this);
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{
			eCond.SetIsLowValue(this);
		}
		else if (value.equals("HIGH-VALUE") || value.equals("HIGH-VALUES"))
		{
			eCond.SetIsHighValue(this);
		}
		else
		{
			return null ;
		}
		if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
		{
			eCond.SetOpposite() ;
			return eCond ;
		}
		else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
		{
			return eCond ;
		}
		else
		{
			return null ;
		}
	}
	public boolean ignore()
	{
		return false ;
	}
	public String GetConstantValue()
	{
		return "" ;
	}
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
	{
//		CEntityArrayReference e = factory.NewEntityArrayReference(getLine()) ;
//		e.SetReference(this) ;
//		for (int i=0; i<v.size(); i++)
//		{
//			CExpression expr = (CExpression)v.get(i);
//			CBaseEntityExpression exp = expr.AnalyseExpression(factory);
//			e.AddIndex(exp);
//		}
//		return e ;
		return this ;
	};
	public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory)
	{
		CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
		ref.SetReference(this, start, length) ;
		return ref ;
	};
	@Override
	public CDataEntityType GetDataType()
	{
		if (isnumericVariable)
			return CDataEntityType.NUMERIC_VAR ;
		else
			return CDataEntityType.VAR;
	}

	public boolean HasAccessors()
	{
		return !csWriteAccessor.isEmpty();
	}

	public boolean isValNeeded()
	{
		return false;
	}
}
