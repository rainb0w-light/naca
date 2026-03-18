/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 sept. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;

import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityStringConcat extends CBaseActionEntity
{

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (eVariable == field)
		{
			eVariable = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		if (arrItems.contains(field))
		{
			int pos;
			while ((pos = arrItems.indexOf(field)) != -1)
			{
				arrItems.set(pos, var) ;
				field.UnRegisterReadingAction(this) ;
				var.RegisterReadingAction(this) ;
			}	
			return true ;
		}
		return false ;
	}
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityStringConcat(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	protected Vector<CDataEntity> arrItems = new Vector<CDataEntity>() ; 
	protected Vector<CDataEntity> arrItemsDelimiters = new Vector<CDataEntity>() ; 
	protected CDataEntity eVariable = null ;
	protected CDataEntity eStartIndex = null ;
	public void Clear()
	{
		super.Clear() ;
		arrItems.clear();
		arrItemsDelimiters.clear() ;
		eStartIndex = null ;
		eVariable = null;
	}
	public void SetVariable(CDataEntity e)
	{
		eVariable = e ;
	}
	public void SetVariable(CDataEntity e, CDataEntity s)
	{
		eVariable = e ;
		eStartIndex = s ;
	}
	public void AddItem(CDataEntity eItem, CDataEntity eUntil)
	{
		arrItems.add(eItem);
		arrItemsDelimiters.add(eUntil);
	}
	public void AddItem(CDataEntity eItem)
	{
		arrItems.add(eItem);
		arrItemsDelimiters.add(null);
	}
	public boolean ignore()
	{
		boolean ignore = eVariable.ignore();
		return ignore ;
	}
}
