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
		if (m_arrDest.contains(field))
		{
			int pos = m_arrDest.indexOf(field) ;
			m_arrDest.set(pos, var) ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		if (m_arrValues.contains(field))
		{
			int pos = m_arrValues.indexOf(field) ;
			m_arrValues.set(pos, var) ;
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
		m_arrDest.add(dest);
	}
	public void SetAddValue(CDataEntity val)
	{
		val.RegisterReadingAction(this);
		m_arrValues.add(val);
	}
	public void SetRounded(boolean b)
	{
		m_bRounded = b ;
	}
	protected Vector<CDataEntity> m_arrValues = new Vector<CDataEntity>() ;
	protected Vector<CDataEntity> m_arrDest = new Vector<CDataEntity>() ;
	protected boolean m_bRounded = false ;
	public void Clear()
	{
		super.Clear();
		m_arrValues.clear() ;
		m_arrDest.clear();
	}
	public boolean ignore()
	{
		boolean ignore = true ;
		for (int i = 0; i<m_arrDest.size(); i++)
		{
			CDataEntity e = m_arrDest.get(i);
			ignore &= e.ignore() ; 
		}
		if (ignore)
		{
			return ignore ;
		}
		ignore = true ;
		for (int i = 0; i<m_arrValues.size(); i++)
		{
			CDataEntity e = m_arrValues.get(i);
			ignore &= e.ignore() ; 
		}
		return ignore ; 
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (m_arrDest.contains(data))
		{
			m_arrDest.remove(data);
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		if (m_arrValues.contains(data))
		{
			m_arrValues.remove(data) ;
			data.UnRegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}

	// ==================== ST4 Template Accessors ====================

	public Vector<CDataEntity> getValues()
	{
		return m_arrValues;
	}

	public Vector<CDataEntity> getDestinations()
	{
		return m_arrDest;
	}

	public boolean isRounded()
	{
		return m_bRounded;
	}

	public boolean hasSingleValue()
	{
		return m_arrValues.size() == 1;
	}

	public CDataEntity getSingleValue()
	{
		if (m_arrValues.size() == 1)
		{
			return m_arrValues.get(0);
		}
		return null;
	}

	public CDataEntity getSingleDestination()
	{
		if (m_arrDest.size() == 1)
		{
			return m_arrDest.get(0);
		}
		return null;
	}

}
