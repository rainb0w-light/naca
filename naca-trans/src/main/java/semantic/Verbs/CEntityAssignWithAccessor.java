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
package semantic.Verbs;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityAssignWithAccessor extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityAssignWithAccessor(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	public void SetAssign(CDataEntity e, CDataEntity val)
	{
		reference = e ;
		value = val ;
	}
	protected CDataEntity reference = null ;
	protected CDataEntity value = null ;
	protected boolean bFillAll = false ;
	public void Clear()
	{
		super.Clear();
		reference= null ;
		value = null ;
	}
	public boolean ignore()
	{
		if (reference == null || value == null)
		{
			return true ;
		}
		return reference.ignore() || value.ignore() ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (reference == data)
		{
			reference = null ;
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		else if (value == data)
		{
			value = null ;
			data.UnRegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (reference == field)
		{
			reference = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		if (value == field)
		{
			value = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			return true ;
		} 
		return false ;
	}

	/**
	 * @param e1
	 */
	public void SetValue(CDataEntity e1)
	{
		value = e1 ;
	}

	/**
	 * @param e2
	 */
	public void SetRefTo(CDataEntity e2)
	{
		reference = e2 ;
	}

	/**
	 * @param fillAll
	 */
	public void SetFillAll(boolean fillAll)
	{
		bFillAll = fillAll;
	}

}
