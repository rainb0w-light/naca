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
public abstract class CEntityCondEquals extends CBinaryEntityCondition
{
	
	public void SetEqualCondition(CBaseEntityExpression op1, CBaseEntityExpression op2)
	{
		ASSERT(op1);
		ASSERT(op2);
		op1 = op1; 
		op2 = op2 ;
		bIsDifferent = false ;
	}
	public void SetDifferentCondition(CBaseEntityExpression op1, CBaseEntityExpression op2) 
	{
		ASSERT(op1);
		ASSERT(op2);
		op1 = op1;
		op2 = op2 ;
		bIsDifferent = true ;
	}
	protected boolean bIsDifferent = false ;
	protected CBaseEntityExpression op1 = null ;
	protected CBaseEntityExpression op2 = null;
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
//	public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//	{
//		CEntityCondEquals eq = factory.NewEntityCondEquals() ;
//		eq.op1 = op1 ;
//		if (term.IsReference())
//		{
//			CDataEntity e = term.GetDataEntity(factory);
//			eq.op2 = factory.NewEntityExprTerminal(e);
//			eq.bIsDifferent = bIsDifferent ;
//			return eq;
//		}
//		else
//		{
//			CDataEntity eOP = op1.GetSingleOperator() ;
//			if (eOP != null)
//			{
//				CBaseEntityCondition.ConditionType type = CBaseEntityCondition.ConditionType.IS_EQUAL ;
//				if (bIsDifferent)
//				{
//					type = CBaseEntityCondition.ConditionType.IS_DIFFERENT ;
//				} 
//				CBaseEntityCondition eCond = eOP.GetSpecialCondition(term.GetValue(), type, factory);
//				if (eCond != null)
//				{
//					return eCond ;
//				}
//			}
//			// else
//			CDataEntity e = term.GetDataEntity(factory);
//			eq.op2 = factory.NewEntityExprTerminal(e);
//			eq.bIsDifferent = bIsDifferent ;
//			return eq;
//		}
//	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		EConditionType type = null ;
		if (bIsDifferent)
		{
			type = EConditionType.IS_DIFFERENT ; 
		}
		else
		{
			type = EConditionType.IS_EQUAL ; 
		}
		CDataEntity op1New = op1.GetSingleOperator() ;
		CDataEntity op2New = op2.GetSingleOperator() ;
		if (op1New != null && (replace==null || replace==op2New))
		{
			CBaseEntityCondition cond = op1New.GetSpecialCondition(getLine(), val, type, fact) ;
			if (cond != null)
			{
				return cond ;
			}
		}
		else if (op2New != null && (replace==null || replace==op1New)
				&& op2New.GetDataType() != CDataEntity.CDataEntityType.NUMBER
				&& op2New.GetDataType() != CDataEntity.CDataEntityType.STRING
				&& op2New.GetDataType() != CDataEntity.CDataEntityType.CONSTANT)
		{
			CBaseEntityCondition cond = op2New.GetSpecialCondition(getLine(), val, type, fact) ;
			if (cond != null)
			{
				return cond ;
			}
		}
		else
		{
			return  null ;
		}
		return null ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		boolean b1 = op1.ReplaceVariable(field, var) ;
		boolean b2 = op2.ReplaceVariable(field, var) ;
		return b1 || b2 ;
	}

}
