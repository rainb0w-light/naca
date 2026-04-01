/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;

import java.util.Vector;

import parser.expression.CStringTerminal;
import parser.expression.CTerminal;

import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityAssign extends CBaseActionEntity
{

	/**
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CEntityAssign(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseSemanticEntity#DoExport()
	 */
	public boolean SetValue(CDataEntity e)
	{
		value = e ;
		return true ;
	}
	
	public void AddRefTo(CDataEntity id)
	{
		refTo.add(id) ;
	}
	protected CDataEntity GetRefTo(int i)
	{
		if (i >= refTo.size())
		{
			return null ;
		}
		else
		{
			return refTo.get(i) ;
		}
	}
	protected int GetNbRefTo()
	{
		return refTo.size() ;
	}
	
	protected CDataEntity value = null ;
	protected boolean isfillAll = false ;
	protected boolean ismoveCorresponding = false ;
	private Vector<CDataEntity> refTo = new Vector<CDataEntity>() ;
	public void Clear()
	{
		super.Clear();
		refTo.clear() ;
	}

	public void SetFillAll(boolean bFillAll)
	{
		bFillAll = bFillAll ;
	}

	public void SetAssignCorresponding(boolean bCorr)
	{
		ismoveCorresponding = bCorr ;
	}
	
	public boolean ignore()
	{
		if (value == null || value.ignore())
		{
			return true ;
		}
		else
		{
			boolean ignore = true ;
			for (int i = 0; i< refTo.size(); i++)
			{
				CDataEntity e = refTo.get(i);
				ignore &= e.ignore() ;
			}
			if (ignore)
			{
				int n=0;
			}
			return ignore ;
		}
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (value == data)
		{
			value = null ;
			data.UnRegisterReadingAction(this) ;
			return true ;
		}
		else
		{
			if (refTo.remove(data))
			{
				data.UnRegisterWritingAction(this) ;
				return true ;
			}
		}
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (value == field)
		{
			value.UnRegisterReadingAction(this) ;
			if (refTo.contains(var))
			{
				value = null ;
			}
			else
			{
				value = var ;
				var.RegisterReadingAction(this) ;
			}
			return true ;
		} 
		else if (refTo.contains(field))
		{
			field.UnRegisterWritingAction(this) ;
			if (value == var || refTo.contains(var))
			{
				refTo.remove(field);
			}
			else
			{
				int n = refTo.indexOf(field) ;
				refTo.set(n, var) ;
				var.RegisterWritingAction(this) ;
			}
			return true ;
		}
		return false ;
	}

	public CBaseActionEntity GetSpecialAssignement(String val, CBaseEntityFactory factory)
	{
		if (refTo.size() == 1)
		{
			CDataEntity ref = refTo.get(0) ;
			CTerminal term = new CStringTerminal(val) ;
			CBaseActionEntity act = ref.GetSpecialAssignment(term, factory, getLine()) ;
			return act ;
		}
		return null;
	}
	public CDataEntity getValueAssigned()
	{
		return value ;
	}
	public Vector getVarsAssigned()
	{
		return refTo;
	}

	// ==================== ST4 Template Accessors ====================

	public CDataEntity getValue()
	{
		return value;
	}

	public Vector<CDataEntity> getDestinations()
	{
		return refTo;
	}

	public boolean isFillAll()
	{
		return isfillAll;
	}

	public boolean isMoveCorresponding()
	{
		return ismoveCorresponding;
	}

}
