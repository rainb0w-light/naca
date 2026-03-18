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
public abstract class CEntityCondIsAll extends CBaseEntityCondition
{

	public void SetCondition(CBaseEntityExpression data, CBaseEntityExpression tok)
	{
		exprData = data ;
		exprToken = tok ;
	}
	
	protected CBaseEntityExpression exprData = null ;
	protected CBaseEntityExpression exprToken = null ;
	protected boolean bIsOpposite = false ;
	public void setOpposite()
	{
		bIsOpposite = ! bIsOpposite ;
	}
	public void Clear()
	{
		super.Clear() ;
		exprData.Clear() ;
		exprToken.Clear() ;
		exprData = null ;
		exprToken = null ;
	}
	public boolean ignore()
	{
		return exprData.ignore() ; 
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return null;
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
