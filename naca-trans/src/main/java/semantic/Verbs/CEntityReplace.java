/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 26, 2004
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
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityReplace extends CBaseActionEntity
{
	protected static class CReplaceMode
	{
		public static CReplaceMode ALL = new CReplaceMode();
		public static CReplaceMode LEADING = new CReplaceMode();
		public static CReplaceMode FIRST = new CReplaceMode();
	}
	protected static class CReplaceType
	{
		public static CReplaceType CUSTOM = new CReplaceType() ; 
		public static CReplaceType SPACES = new CReplaceType() ; 
		public static CReplaceType ZEROS = new CReplaceType() ; 
		public static CReplaceType LOW_VALUES = new CReplaceType() ; 
		public static CReplaceType HIGH_VALUES = new CReplaceType() ; 
	}
	protected class CReplaceItem
	{
		public CReplaceMode mode = null ;
		public CReplaceType replaceDataType = null ;
		public CDataEntity replaceData = null ;
		public CReplaceType byDataType = null ;
		public CDataEntity byData = null ;
	} 
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityReplace(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	protected CDataEntity variable = null ; 
	protected Vector<CReplaceItem> arrItemsToReplace = new Vector<CReplaceItem>() ;
	private CReplaceItem curItem = null ;
	public void Clear()
	{
		super.Clear() ;
		variable = null ;
		arrItemsToReplace.clear() ;
		if (curItem != null)
		{
			curItem.byData = null ;
			curItem.byDataType = null ;
			curItem.replaceData = null ;
			curItem = null ;
		}
	}

	public void SetReplace(CDataEntity e)
	{
		variable = e ;
	}
	public void AddReplaceLeading()
	{
		curItem = new CReplaceItem() ;
		curItem.mode = CReplaceMode.LEADING;
	}
	public void AddReplaceAll()
	{
		curItem = new CReplaceItem() ;
		curItem.mode = CReplaceMode.ALL;
	}
	public void AddReplaceFirst()
	{
		curItem = new CReplaceItem() ;
		curItem.mode = CReplaceMode.FIRST;
	}
	public void ReplaceSpaces()
	{
		curItem.replaceDataType = CReplaceType.SPACES ;
		curItem.replaceData = null ;
	}
	public void ReplaceZeros()
	{
		curItem.replaceDataType = CReplaceType.ZEROS ;
		curItem.replaceData = null ;
	}
	public void ReplaceLowValues()
	{
		curItem.replaceDataType = CReplaceType.LOW_VALUES ;
		curItem.replaceData = null ;
	}
	public void ReplaceHighValues()
	{
		curItem.replaceDataType = CReplaceType.HIGH_VALUES;
		curItem.replaceData = null ;
	}
	public void BySpaces()
	{
		curItem.byDataType = CReplaceType.SPACES ;
		curItem.byData = null ;
		arrItemsToReplace.add(curItem) ;
		curItem = null ;
	}
	public void ByZeros()
	{
		curItem.byDataType = CReplaceType.ZEROS ;
		curItem.byData = null ;
		arrItemsToReplace.add(curItem) ;
		curItem = null ;
	}
	public void ByLowValues()
	{
		curItem.byDataType = CReplaceType.LOW_VALUES ;
		curItem.byData = null ;
		arrItemsToReplace.add(curItem) ;
		curItem = null ;
	}
	public void ByHighValues()
	{
		curItem.byDataType = CReplaceType.HIGH_VALUES ;
		curItem.byData = null ;
		arrItemsToReplace.add(curItem) ;
		curItem = null ;
	}
	public void ReplaceData(CDataEntity e)
	{
		curItem.replaceDataType = CReplaceType.CUSTOM ;
		curItem.replaceData = e ;
	}
	public void ByData(CDataEntity e)
	{
		curItem.byDataType = CReplaceType.CUSTOM ;
		curItem.byData = e ;
		arrItemsToReplace.add(curItem) ;
		curItem = null ;
	}
	public boolean ignore()
	{
		return variable.ignore();
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (variable == field)
		{
			variable = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
}
