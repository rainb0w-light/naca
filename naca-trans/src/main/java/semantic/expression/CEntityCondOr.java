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



/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCondOr extends CBaseEntityCondition
{
	public void SetCondition(CBaseEntityCondition op1, CBaseEntityCondition op2)	{
		op1 = op1 ;
		op1.SetParent(this) ; 
		op2 = op2 ; 
		op2.SetParent(this);
	}
	protected CBaseEntityCondition op1 = null ;
	protected CBaseEntityCondition op2 = null ;
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
		return op1.ignore() && op2.ignore() ;
	}
//	public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//	{
//		CBaseEntityCondition eCond = op1.getSimilarCondition(factory, term);
//		if (eCond == null)
//		{
//			eCond = op2.getSimilarCondition(factory, term);
//		}
//		return eCond ;
//	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return null;
	}
	public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
	{
		if (op1 == condition)
		{
			op1 = newCond ;
		}
		if (op2 == condition)
		{
			op2 = newCond ;
		}
	}
	public boolean isBinaryCondition()
	{
		return false;
	}
	/**
	 * @see semantic.expression.CBaseEntityCondition#GetConditionReference()
	 */
	@Override
	public CDataEntity GetConditionReference()
	{
		return null;
	}
	public void SetConditonReference(CDataEntity e)
	{
		ASSERT(null) ;
	}

}
