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
public abstract class CEntitySetColor extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntitySetColor(int line, CObjectCatalog cat, CDataEntity field)
	{
		super(line, cat);
		field = field ;
	}
	
	public void SetColor(CEntityFieldColor.CFieldColor c)
	{
		color = c ;
	}
	protected CEntityFieldColor.CFieldColor color = null ;
	protected CDataEntity field = null ;
	public void Clear()
	{
		super.Clear();
		field = null ;
	}
	public boolean ignore()
	{
		if (field == null || field.ignore())
		{
			return true ;
		}
		else if (colorVariable != null && colorVariable.ignore())
		{
			return true ;
		}
		return false ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (data == field)
		{
			field = null ;
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (field == field)
		{
			field = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}

	/**
	 * @param term
	 */
	public void SetColor(CDataEntity term)
	{
		color = null ;
		colorVariable = term ;		
	}
	protected CDataEntity colorVariable = null ;
}
