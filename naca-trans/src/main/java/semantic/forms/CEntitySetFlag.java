/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 11 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntitySetFlag extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntitySetFlag(int line, CObjectCatalog cat, CDataEntity field)
	{
		super(line, cat);
		refField = field ;
	}
	
	public void SetFlag(String cs)
	{
		flagValue = cs ;
	}
	protected String flagValue = null ;
	protected CDataEntity refField = null ;
	public void Clear()
	{
		super.Clear();
		refField = null ;
	}
	public boolean ignore()
	{
		return refField == null ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (data == refField)
		{
			refField = null ;
			data.UnRegisterWritingAction(this) ;
			
			return true ;
		}
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (refField == field)
		{
			refField = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}

	/**
	 * 
	 */
	public void ResetFlag()
	{
		flagValue = null ;
	}
}
