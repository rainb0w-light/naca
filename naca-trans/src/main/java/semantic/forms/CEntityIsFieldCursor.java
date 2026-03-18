/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 14 févr. 2005
 *
 */
package semantic.forms;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CUnitaryEntityCondition;

/**
 * @author sly
 *
 */
public abstract class CEntityIsFieldCursor extends CUnitaryEntityCondition
{

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String, semantic.CBaseEntityFactory)
	 */
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		if (bHasCursor)
		{
			return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_EQUAL, fact);
		}
		else
		{
			return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_DIFFERENT, fact);
		}
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#ignore()
	 */
	public boolean ignore()
	{
		return reference.ignore();
	}
	
	protected boolean bHasCursor = true ; 
	/**
	 * @param refField
	 */
	public void SetHasCursor(CDataEntity refField)
	{
		reference = refField ;
		bHasCursor = true ;
	}

	/**
	 * @param refField
	 */
	public void SetHasNotCursor(CDataEntity refField)
	{
		reference = refField ;
		bHasCursor = false ;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}
	
}
