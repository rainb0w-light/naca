/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 4 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import java.util.ArrayList;
import java.util.List;
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
public class CEntityDisplay extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityDisplay(int line, CObjectCatalog cat, Upon t)
	{
		super(line, cat);
		upon = t ;
	}
	public void AddItemToDisplay(CDataEntity e)
	{
		itemsToDisplay.add(e) ;
	}
	
	protected Vector<CDataEntity> itemsToDisplay = new Vector<CDataEntity>();
	protected Upon upon = Upon.DEFAULT ;
	public void Clear()
	{
		super.Clear() ;
		itemsToDisplay.clear();
	}
	public boolean ignore()
	{
		boolean ignore = true ;
		for (int i = 0; i< itemsToDisplay.size(); i++)
		{
			CDataEntity e = itemsToDisplay.get(i);
			ignore &= e.ignore() ;
		}
		return ignore ;
	}
	
	public static enum Upon
	{
		DEFAULT, CONSOLE, ENVINONMENT,
	}

	public boolean isConsole()
	{
		return upon == Upon.CONSOLE;
	}

	public boolean isEnvironment()
	{
		return upon == Upon.ENVINONMENT;
	}

	public List<CDisplayItemView> getDisplayItems()
	{
		List<CDisplayItemView> values = new ArrayList<CDisplayItemView>();
		boolean hasMultipleItems = itemsToDisplay.size() > 1;
		for (int i = 0; i < itemsToDisplay.size(); i++)
		{
			CDataEntity item = itemsToDisplay.get(i);
			values.add(new CDisplayItemView(
				item, hasMultipleItems && item.isValNeeded()));
		}
		return values;
	}

	public static class CDisplayItemView
	{
		private final CDataEntity reference;
		private final boolean valueNeeded;

		public CDisplayItemView(CDataEntity reference, boolean valueNeeded)
		{
			this.reference = reference;
			this.valueNeeded = valueNeeded;
		}

		public CDataEntity getReference()
		{
			return reference;
		}

		public boolean isValueNeeded()
		{
			return valueNeeded;
		}
	}

}
