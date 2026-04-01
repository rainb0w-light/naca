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
package semantic;

import java.util.Vector;

import generate.*;
import semantic.expression.CBaseEntityCondition;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCondition extends CBaseActionEntity
{

	/**
	 * @param cat
	 * @param out
	 */
	public CEntityCondition(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat, out);
	}
	
	public void SetCondition(CBaseEntityCondition exp, CEntityBloc ifyes, CEntityBloc ifnot)
	{
		condition = exp ;
		if (exp != null)
			condition.SetParent(this);
		elseBloc = ifnot ;
		thenBloc = ifyes ;
	} 
	protected CBaseEntityCondition condition = null ;
	protected CEntityBloc elseBloc = null ;
	protected CEntityBloc thenBloc = null ;
	protected boolean isalternativeCondition = false ;
	
	public boolean ignore()
	{
		return condition == null || condition.ignore() || ((elseBloc == null || elseBloc.ignore()) && thenBloc.ignore()) ;
	}
	public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
	{
		if (condition == condition)
		{
			condition = newCond ;
		}
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#Clear()
	 */
	public void Clear()
	{
		super.Clear();
		if (condition != null)
			condition.Clear() ;
		if (elseBloc != null)
		{
			elseBloc.Clear() ;
		}
		thenBloc.Clear() ;
		condition = null ;
		elseBloc = null ;
		thenBloc = null ;
	}
	public boolean hasExplicitGetOut()
	{
		boolean isexplicit = thenBloc.hasExplicitGetOut() ;
		isexplicit &= elseBloc != null && elseBloc.hasExplicitGetOut() ;
		return isexplicit;
	}

	/**
	 * @param exp
	 * @param blocthen
	 */
	public void SetAlternativeCondition(CBaseEntityCondition exp, CEntityBloc blocthen)
	{
		condition = exp ;
		if (exp != null)
			condition.SetParent(this);
		elseBloc = null ;
		thenBloc = blocthen ;
		isalternativeCondition = true ;
	}

	/**
	 * @param e
	 */
	public void addAlternativeCondition(CBaseLanguageEntity e)
	{
		if (alternativeConditions == null)
			alternativeConditions = new Vector<CBaseLanguageEntity>() ;
		alternativeConditions.add(e) ;
	}
	protected Vector<CBaseLanguageEntity> alternativeConditions = null;

	// ==================== ST4 Template Accessors ====================

	public CBaseEntityCondition getCondition()
	{
		return condition;
	}

	public CEntityBloc getThenBloc()
	{
		return thenBloc;
	}

	public CEntityBloc getElseBloc()
	{
		return elseBloc;
	}

	public boolean isAlternativeCondition()
	{
		return isalternativeCondition;
	}

	public Vector<CBaseLanguageEntity> getAlternativeConditions()
	{
		return alternativeConditions;
	}

	public boolean isConditionIgnored()
	{
		return condition == null || condition.ignore();
	}

	public boolean shouldRenderElseBlock()
	{
		return elseBloc != null && !elseBloc.ignore() && isConditionIgnored();
	}

}
