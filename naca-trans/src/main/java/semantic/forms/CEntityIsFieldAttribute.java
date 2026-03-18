/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 août 2004
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
		bIsAutoSkip = true ;		
		nbConditions ++ ;		
	}
	protected boolean bIsAutoSkip = false ;

	public void IsBright()
	{
		bIsBright = true ;		
		nbConditions ++ ;		
	}
	protected boolean bIsBright = false ;

	public void IsNumeric()
	{
		bIsNumeric = true ;		
		nbConditions ++ ;		
	}
	protected boolean bIsNumeric = false ;

	public void IsProtected()
	{
		bIsProtected = true;		
		nbConditions ++ ;		
	}
	protected boolean bIsProtected = false ;

	public void IsUnprotected()
	{
		bIsUnprotected = true ;		
		nbConditions ++ ;		
	}
	protected boolean bIsUnprotected = false ;

	public void IsModified()
	{
		bIsModified = true ;		
		nbConditions ++ ;		
	}
	public void IsUnmodified()
	{
		bIsUnmodified = true ;		
		nbConditions ++ ;		
	}
	public void IsCleared()
	{
		bIsCleared = true ;		
		nbConditions ++ ;		
	}
	protected boolean bIsModified = false ;
	protected boolean bIsUnmodified = false ;		
	protected boolean bIsCleared = false ;
	
	public void IsDark()
	{
		bIsDark = true ;
		nbConditions ++ ;		
	}
	protected boolean bIsDark = false ;
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
		if (bOpposite && cond!=null)
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
		bOpposite = !bOpposite ;
	}
	protected boolean bOpposite = false ;
	public boolean isBinaryCondition()
	{
		return true;
	}

}
