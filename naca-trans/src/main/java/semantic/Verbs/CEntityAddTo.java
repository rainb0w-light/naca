/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 9 ao�t 2004
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
public abstract class CEntityAddTo extends CBaseActionEntity
{

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (arrDest.contains(field))
		{
			int pos = arrDest.indexOf(field) ;
			arrDest.set(pos, var) ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		if (arrValues.contains(field))
		{
			int pos = arrValues.indexOf(field) ;
			arrValues.set(pos, var) ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityAddTo(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	public void SetAddDest(CDataEntity dest)
	{
		dest.RegisterWritingAction(this);
		arrDest.add(dest);
	}
	public void SetAddValue(CDataEntity val)
	{
		val.RegisterReadingAction(this);
		arrValues.add(val);
	}
	public void SetRounded(boolean b)
	{
		bRounded = b ;
	}
	protected Vector<CDataEntity> arrValues = new Vector<CDataEntity>() ;
	protected Vector<CDataEntity> arrDest = new Vector<CDataEntity>() ;
	protected boolean bRounded = false ;
	public void Clear()
	{
		super.Clear();
		arrValues.clear() ;
		arrDest.clear();
	}
	public boolean ignore()
	{
		boolean ignore = true ;
		for (int i = 0; i<arrDest.size(); i++)
		{
			CDataEntity e = arrDest.get(i);
			ignore &= e.ignore() ; 
		}
		if (ignore)
		{
			return ignore ;
		}
		ignore = true ;
		for (int i = 0; i<arrValues.size(); i++)
		{
			CDataEntity e = arrValues.get(i);
			ignore &= e.ignore() ; 
		}
		return ignore ; 
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (arrDest.contains(data))
		{
			arrDest.remove(data);
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		if (arrValues.contains(data))
		{
			arrValues.remove(data) ;
			data.UnRegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}

	// ==================== ST4 Template Accessors ====================

	public Vector<CDataEntity> getValues()
	{
		return arrValues;
	}

	public Vector<CDataEntity> getDestinations()
	{
		return arrDest;
	}

	public boolean isRounded()
	{
		return bRounded;
	}

	public boolean hasSingleValue()
	{
		return arrValues.size() == 1;
	}

	public CDataEntity getSingleValue()
	{
		if (arrValues.size() == 1)
		{
			return arrValues.get(0);
		}
		return null;
	}

	public CDataEntity getSingleDestination()
	{
		if (arrDest.size() == 1)
		{
			return arrDest.get(0);
		}
		return null;
	}

}
