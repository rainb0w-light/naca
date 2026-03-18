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
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCondNot extends CBaseEntityCondition
{
	
	protected CBaseEntityCondition cond ;
	public void Clear()
	{
		super.Clear() ;
		cond.Clear() ;
		cond = null ;
	}

	public void SetCondition(CBaseEntityCondition cond)
	{
		ASSERT(cond);
		cond = cond ;
		cond.SetParent(this);
	}
	public boolean ignore()
	{
		return cond.ignore();
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		return cond.ReplaceVariable(field, var) ;
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		CBaseEntityCondition condNew = cond.GetSpecialConditionReplacing(val, fact, replace);
		CBaseEntityCondition notcond = condNew.GetOppositeCondition() ;
		if (notcond == null)
		{
			CEntityCondNot notCond = fact.NewEntityCondNot() ;
			notCond.SetCondition(condNew);
		}
		return notcond ;
	}
//	public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//	{
//		CEntityCondNot not = factory.NewEntityCondNot() ;
//		CBaseEntityCondition cond = cond.getSimilarCondition(factory, term) ;
//		not.SetCondition(cond);
//		return not ;
//	}
	public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
	{
		if (cond == condition)
		{
			cond = newCond ;
		}
	}
	public boolean isBinaryCondition()
	{
		return cond.isBinaryCondition() ;
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
