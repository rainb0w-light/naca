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

import generate.*;


import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityProcedureSection extends CEntityProcedure
{

	/**
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CEntityProcedureSection(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat, out, null);
	}

	protected CEntityBloc sectionBloc =null ;
	public void SetSectionBloc(CEntityBloc b)
	{
		sectionBloc = b ;
	}
	protected void RegisterMySelfToCatalog()
	{
		programCatalog.RegisterProcedure(GetName(), this, null) ;
		programCatalog.getCallTree().RegisterSection(this) ;
	}
	public CEntityProcedureSection getSectionContainer()
	{
		return this ;
	} 
	public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
	{
		if (sectionBloc!=null && sectionBloc.UpdateAction(entity, newCond))
		{
			return true ;
		}
		for (int i=0; i<lstChildren.size(); i++)
		{
			CBaseLanguageEntity act = lstChildren.get(i) ;
			if (act.UpdateAction(entity, newCond))
			{
				return true ;
			}
		}
		return false ;
	}
	public void Clear()
	{
		super.Clear();
		if (sectionBloc != null)
		{
			sectionBloc.Clear() ;
		}
		sectionBloc = null ;
	}
	public boolean hasExplicitGetOut()
	{
		if (sectionBloc == null)
		{
			return false ;
		}
		return sectionBloc.hasExplicitGetOut() ;
	}
	/**
	 * 
	 */
	public void ReduceToProcedure()
	{
		isreducedToProcedure = true ;
	}	
	protected boolean isreducedToProcedure = false ;
	/**
	 * @return
	 */
	public CEntityBloc getSectionBloc()
	{
		return sectionBloc ;
	}
	public boolean ignore()
	{
		if (isignore)
		{
			return true ;
		}
		if (isreducedToProcedure)
		{
			if (sectionBloc == null)
			{
				return isChildrenIgnored() ;
			}
			else
			{
				return sectionBloc.ignore() && isChildrenIgnored() ;
			}
		}
		return false ;
	}
}
