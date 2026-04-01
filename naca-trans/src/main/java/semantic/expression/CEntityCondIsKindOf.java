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
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCondIsKindOf extends CUnitaryEntityCondition
{
	public void SetIsNumeric(CDataEntity data)
	{
		SetConditonReference(data) ;
		isisNumeric = true ;
		isisAlphabetic = false ;
		isisLower = false ;
		isisUpper = false ;
	}
	public void SetIsAlphabetic(CDataEntity data)
	{
		SetConditonReference(data) ;
		isisNumeric = false ;
		isisAlphabetic = true ;
		isisLower = false ;
		isisUpper = false ;
	}
	public void SetIsLower(CDataEntity data)
	{
		SetConditonReference(data) ;
		isisNumeric = false ;
		isisAlphabetic = false ;
		isisLower = true ;
		isisUpper = false ;
	}
	public void SetIsUpper(CDataEntity data)
	{
		SetConditonReference(data) ;
		isisNumeric = false ;
		isisAlphabetic = false ;
		isisLower = false ;
		isisUpper = true ;
	}

	protected boolean isisNumeric = false ;
	protected boolean isisLower = false ;
	protected boolean isisUpper = false ;
	protected boolean isisAlphabetic = false ;
	protected boolean isopposite = false ;
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
		isopposite = !isopposite;
	}
	public boolean isBinaryCondition()
	{
		return false;
	}
}
