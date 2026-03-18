/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 27 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CUnitaryEntityCondition;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityIsFieldHighlight extends CUnitaryEntityCondition
{

	public CEntityIsFieldHighlight(CDataEntity ref)
	{
		reference = ref ;
	}
	
	public void IsBlink()
	{
		bIsBlink = true ;
	}
	public void IsReverse()
	{
		bIsReverse = true ;
	}
	public void IsUnderlined()
	{
		bIsUnderlined = true ;
	}
	//protected CFieldHighligh m_highlight = null ;
	protected boolean bIsBlink = false ;
	protected boolean bIsReverse = false ;
	protected boolean bIsUnderlined = false ;
	protected boolean bOpposite = false ;
	
	public boolean ignore()
	{
		return reference.ignore() ;
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_EQUAL, fact);
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (reference == field)
		{
			reference = var ;
			field.UnRegisterVarTesting(this) ;
			var.RegisterVarTesting(this) ;
			return true ;
		}
		return false ;
	}

	/**
	 * 
	 */
	public void IsNormal()
	{
		bIsBlink = false ;
		bIsReverse = false ;
		bIsUnderlined = false ;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}
	
	public void setOpposite() 
	{
		bOpposite = !bOpposite ;
	}
}
