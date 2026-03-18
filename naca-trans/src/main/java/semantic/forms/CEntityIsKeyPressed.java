/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Oct 18, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityIsKeyPressed extends CBaseEntityCondition
{

	public void isKeyPressed(CDataEntity key)
	{
		keyPressed = key ;
		bIsNot = false ;
	}
	public void isNotKeyPressed(CDataEntity key)
	{
		keyPressed = key ;
		bIsNot = true ;
	}
	protected CDataEntity keyPressed = null ;
	protected boolean bIsNot = false ;
	public void Clear()
	{
		super.Clear();
		keyPressed = null ;
	}
	
//	public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//	{
//		CEntityIsKeyPressed ikp = factory.NewEntityIsKeyPressed() ;
//		ikp.bIsNot = bIsNot ;
//		ikp.keyPressed = term.GetDataEntity(factory);
//		return ikp ;
//	}
	public boolean ignore()
	{
		return false ;
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return null;
	}
	public boolean isBinaryCondition()
	{
		return true;
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
