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
public abstract class CEntityIsFieldAttribute extends CUnitaryEntityCondition
{
	protected CDataEntity varValue = null ;
	public void Clear()
	{
		super.Clear();
		varValue = null ;
	}
	
	public void IsAttribute(CDataEntity data, CDataEntity var)
	{
		varValue = data ;
		reference = var ;
	}
	public void IsAutoSkip()
	{
		isisAutoSkip = true ;
		nbConditions ++ ;		
	}
	protected boolean isisAutoSkip = false ;

	public void IsBright()
	{
		isisBright = true ;
		nbConditions ++ ;		
	}
	protected boolean isisBright = false ;

	public void IsNumeric()
	{
		isisNumeric = true ;
		nbConditions ++ ;		
	}
	protected boolean isisNumeric = false ;

	public void IsProtected()
	{
		isisProtected = true;
		nbConditions ++ ;		
	}
	protected boolean isisProtected = false ;

	public void IsUnprotected()
	{
		isisUnprotected = true ;
		nbConditions ++ ;		
	}
	protected boolean isisUnprotected = false ;

	public void IsModified()
	{
		isisModified = true ;
		nbConditions ++ ;		
	}
	public void IsUnmodified()
	{
		isisUnmodified = true ;
		nbConditions ++ ;		
	}
	public void IsCleared()
	{
		isisCleared = true ;
		nbConditions ++ ;		
	}
	protected boolean isisModified = false ;
	protected boolean isisUnmodified = false ;
	protected boolean isisCleared = false ;
	
	public void IsDark()
	{
		isisDark = true ;
		nbConditions ++ ;		
	}
	protected boolean isisDark = false ;
	protected int nbConditions = 0;

	public void SetVariable(CDataEntity field)
	{
		reference = field ;		
	}
	public boolean ignore()
	{
		return reference.ignore() ;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String, semantic.CBaseEntityFactory)
	 */
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		CBaseEntityCondition cond = reference.GetSpecialCondition(getLine(), val, EConditionType.IS_FIELD_ATTRIBUTE, fact);
		if (isopposite && cond!=null)
		{
			return cond.GetOppositeCondition() ;
		}
		return cond ;
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
		if (varValue == field)
		{
			varValue = var ;
			field.UnRegisterValueAccess(this) ;
			var.RegisterValueAccess(this) ;
			return true ;
		}
		return false  ;
	}
	
	protected void SetOpposite()
	{
		isopposite = !isopposite;
	}
	protected boolean isopposite = false ;
	public boolean isBinaryCondition()
	{
		return true;
	}

}
