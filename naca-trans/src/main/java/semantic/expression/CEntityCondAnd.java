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
public abstract class CEntityCondAnd extends CBaseEntityCondition
{
	public void SetCondition(CBaseEntityCondition op1, CBaseEntityCondition op2)	{
		op1 = op1 ;
		op1.SetParent(this); 
		op2 = op2 ; 
		op2.SetParent(this) ;
	}
	protected CBaseEntityCondition op1 = null ;
	protected CBaseEntityCondition op2 = null ;
	public void Clear()
	{
		super.Clear() ;
		op1.Clear() ;
		op2.Clear() ;
		op1 =null ;
		op2 = null ;
	}
	protected int GetLevelPriority()
	{
		return  1;
	}
	public boolean ignore()
	{
		return op1.ignore() && op2.ignore() ; 
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		boolean b1 = op1.ReplaceVariable(field, var) ;
		boolean b2 = op2.ReplaceVariable(field, var) ;
		return b1 || b2 ;
	}
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
