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
public abstract class CEntityCondIsKindOf extends CUnitaryEntityCondition
{
	public void SetIsNumeric(CDataEntity data)
	{
		SetConditonReference(data) ;
		bIsNumeric = true ;
		bIsAlphabetic = false ;
		bIsLower = false ;
		bIsUpper = false ;
	}
	public void SetIsAlphabetic(CDataEntity data)
	{
		SetConditonReference(data) ;
		bIsNumeric = false ;
		bIsAlphabetic = true ;
		bIsLower = false ;
		bIsUpper = false ;
	}
	public void SetIsLower(CDataEntity data)
	{
		SetConditonReference(data) ;
		bIsNumeric = false ;
		bIsAlphabetic = false ;
		bIsLower = true ;
		bIsUpper = false ;
	}
	public void SetIsUpper(CDataEntity data)
	{
		SetConditonReference(data) ;
		bIsNumeric = false ;
		bIsAlphabetic = false ;
		bIsLower = false ;
		bIsUpper = true ;
	}

	protected boolean bIsNumeric = false ;
	protected boolean bIsLower = false ;
	protected boolean bIsUpper = false ;
	protected boolean bIsAlphabetic = false ;
	protected boolean bOpposite = false ;
	public boolean ignore()
	{
		return reference.ignore();
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return null;
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
	public void setOpposite()
	{
		bOpposite = !bOpposite ;
	}
	public boolean isBinaryCondition()
	{
		return false;
	}
}
