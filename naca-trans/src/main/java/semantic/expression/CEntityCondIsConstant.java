/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 ao�t 2004
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
public abstract class CEntityCondIsConstant extends CUnitaryEntityCondition
{
//	public CEntityCondIsConstant(int nLine)
//	{
//		super(nLine);
//	}
	
	public void SetIsZero(CDataEntity eData)
	{
		isisZero = true ;
		isisSpace = false ;
		isisLowValue = false ;
		isisHighValue = false ;
		SetConditonReference(eData) ;
	}

	public void SetIsSpace(CDataEntity eData)
	{
		isisZero = false ;
		isisSpace = true ;
		isisLowValue = false ;
		isisHighValue = false ;
		SetConditonReference(eData) ;
	}

	public void SetIsHighValue(CDataEntity eData)
	{
		isisZero = false ;
		isisSpace = false ;
		isisLowValue = false ;
		isisHighValue = true;
		SetConditonReference(eData) ;
	}
	public void SetIsLowValue(CDataEntity eData)
	{
		isisZero = false ;
		isisSpace = false ;
		isisLowValue = true ;
		isisHighValue = false ;
		SetConditonReference(eData) ;
	}
	public void SetOpposite()
	{
		bIsOpposite = true ;
	}

	protected boolean bIsOpposite = false ;
	protected boolean isisZero = false ;
	protected boolean isisSpace = false ;
	protected boolean isisLowValue = false ;
	protected boolean isisHighValue = false ;
	public boolean ignore()
	{
		return reference.ignore() ; 
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (reference == field)
		{
			field.UnRegisterVarTesting(this) ;
			var.RegisterVarTesting(this) ;
			reference = var ;
			return true ;
		}
		return false ;
	}

//	public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//	{
//		if (term.IsReference())
//		{
//			CDataEntity e = term.GetDataEntity(factory) ;
//			CEntityCondEquals eq = factory.NewEntityCondEquals() ;
//			CBaseEntityExpression op1 = factory.NewEntityExprTerminal(reference);
//			CBaseEntityExpression op2 = factory.NewEntityExprTerminal(e);
//			if (bIsOpposite)
//			{
//				eq.SetDifferentCondition(op1, op2);
//			}
//			else
//			{
//				eq.SetEqualCondition(op1, op2);
//			}
//			return eq ;
//		}
//		else
//		{
//			CBaseEntityCondition.ConditionType type = CBaseEntityCondition.ConditionType.IS_EQUAL ;
//			if (bIsOpposite)
//			{
//				type = CBaseEntityCondition.ConditionType.IS_DIFFERENT ;
//			}
//			CBaseEntityCondition cond = reference.GetSpecialCondition(term.GetValue(), type, factory) ;
//			if (cond == null)
//			{
//				CDataEntity e = term.GetDataEntity(factory) ;
//				CEntityCondEquals eq = factory.NewEntityCondEquals() ;
//				CBaseEntityExpression op1 = factory.NewEntityExprTerminal(reference);
//				CBaseEntityExpression op2 = factory.NewEntityExprTerminal(e);
//				if (bIsOpposite)
//				{
//					eq.SetDifferentCondition(op1, op2);
//				}
//				else
//				{
//					eq.SetEqualCondition(op1, op2);
//				}
//				return eq ;
//			}
//			return cond ;
//		}
//	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return null;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}

}
