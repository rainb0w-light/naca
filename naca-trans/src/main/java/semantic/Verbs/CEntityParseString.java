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

public abstract class CEntityParseString extends CBaseActionEntity
{
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		boolean bReplace = false;
		if (variable == field)
		{
			variable = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			bReplace = true;
		}
		for (int i=0; i < arrDestinations.size(); i++)
		{
			CDataEntity[] entities = arrDestinations.get(i);
			for (int j=0; j < entities.length; j++)
			{
				CDataEntity entity = entities[j];
				if (entity != null && entity == field)
				{
					entity = var ;
					field.UnRegisterReadingAction(this) ;
					var.RegisterReadingAction(this) ;
					bReplace = true;
				}
			}
		}
		if (arrDelimitersMulti.contains(field))
		{
			int pos = arrDelimitersMulti.indexOf(field) ;
			arrDelimitersMulti.set(pos, var) ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			bReplace = true ;
		}
		if (arrDelimitersSingle.contains(field))
		{
			int pos = arrDelimitersSingle.indexOf(field) ;
			arrDelimitersSingle.set(pos, var) ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			bReplace = true ;
		}
		if (tallying == field)
		{
			tallying = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			bReplace = true;
		}
		if (withPointer == field)
		{
			withPointer = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			bReplace = true;
		}
		return bReplace;
	}

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityParseString(int line,	CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	public void ParseString(CDataEntity e)
	{
		variable = e ;
	}
	public void AddDelimiterSingle(CDataEntity e)
	{
		arrDelimitersSingle.add(e);
	}
	public void AddDelimiterMulti(CDataEntity e)
	{
		arrDelimitersMulti.add(e);
	}
	public void AddDestination(CDataEntity[] e)
	{
		arrDestinations.add(e);
	}
	public void setTallying(CDataEntity e)
	{
		tallying = e;
	}
	public void setWithPointer(CDataEntity e)
	{
		withPointer = e;
	}
	protected CDataEntity variable = null ;
	protected Vector<CDataEntity[]> arrDestinations = new Vector<CDataEntity[]>() ;
	protected Vector<CDataEntity> arrDelimitersMulti = new Vector<CDataEntity>() ;
	protected Vector<CDataEntity> arrDelimitersSingle = new Vector<CDataEntity>() ;
	protected CDataEntity tallying = null ;
	protected CDataEntity withPointer = null ;
	
	public void Clear()
	{
		super.Clear() ;
		variable = null ;
		arrDelimitersMulti.clear() ;
		arrDelimitersSingle.clear() ;
		arrDestinations.clear();
	}
	public boolean ignore()
	{
		return variable.ignore();
	}
}
