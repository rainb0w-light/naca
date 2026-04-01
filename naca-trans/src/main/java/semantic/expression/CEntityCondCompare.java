/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;



/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCondCompare extends CBinaryEntityCondition
{
	
	public void SetLessThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
	{
		op1 = op1 ; 
		op2 = op2 ;
		isisOrEquals = false ;
		isisGreater = false ;
	}
	public void SetLessOrEqualThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
	{
		op1 = op1 ; 
		op2 = op2 ;
		isisOrEquals = true ;
		isisGreater = false ;
	}
	public void SetGreaterThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
	{
		op1 = op1 ; 
		op2 = op2 ;
		isisOrEquals = false ;
		isisGreater = true ;
	}
	public void SetGreaterOrEqualsThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
	{
		op1 = op1 ; 
		op2 = op2 ;
		isisOrEquals = true ;
		isisGreater = true ;
	}
	
	protected CBaseEntityExpression op1 ;
	protected CBaseEntityExpression op2 ;
	protected boolean isisGreater = false ; // true : >/>=, false : </<=
	protected boolean isisOrEquals = false ;// true : <=/>=, false : </>
	public void Clear()
	{
		super.Clear() ;
		op1.Clear() ;
		op1 = null ;
		op2.Clear() ;
		op2 = null ;
	}

	public boolean ignore()
	{
		return op1.ignore() || op2.ignore();  
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		//CBaseEntityCondition cond = op1.GetSpecialCondition(val, type, fact) ;
		CDataEntity op = op1.GetSingleOperator() ;
		if (op != null)
		{
			EConditionType type = GetType() ;
			CBaseEntityCondition cond = op.GetSpecialCondition(getLine(), val, type, fact) ;
			if (cond != null)
			{
				return cond ;
			}
		}
		return null;
	}
	public CBaseEntityCondition.EConditionType GetType()
	{
		EConditionType type = null ;
		if (isisGreater && isisOrEquals)
		{
			type = EConditionType.IS_GREATER_THAN_OR_EQUAL ; 
		}
		else if (isisGreater && !isisOrEquals)
		{
			type = EConditionType.IS_GREATER_THAN ; 
		}
		else if (!isisGreater && isisOrEquals)
		{
			type = EConditionType.IS_LESS_THAN_OR_EQUAL ; 
		}
		else if (!isisGreater && !isisOrEquals)
		{
			type = EConditionType.IS_LESS_THAN ; 
		}
		return type ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		boolean b1 = op1.ReplaceVariable(field, var) ;
		boolean b2 = op2.ReplaceVariable(field, var) ;
		return b1 || b2 ;
	}
}
