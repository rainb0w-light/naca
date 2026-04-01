/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
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
public abstract class CEntityProcedure extends CBaseLanguageEntity
{

	/**
	 * @param name
	 * @param cat
	 */
	protected CEntityProcedure(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out, CEntityProcedureSection section)
	{
		super(l, "", cat, out);
		sectionContainer = section ;
		SetName(name);
	}
	protected CEntityProcedureSection sectionContainer = null ;
	/* (non-Javadoc)
	 * @see semantic.CBaseSemanticEntity#RegisterMySelfToCatalog()
	 */
	protected void RegisterMySelfToCatalog()
	{
		programCatalog.RegisterProcedure(GetName(), this, sectionContainer) ;
		programCatalog.getCallTree().RegisterProcedure(this) ;
	}

	public void setFullName()
	{
		if (sectionContainer == null)
		{
			String fullName = GetName() + "$" + ms_nLastProcedureIndex ;
			ms_nLastProcedureIndex ++ ;
			SetName(fullName) ;
		}
		else if (GetName().indexOf('$')>0)
		{
			String fullName = GetName() + "$" + ms_nLastProcedureIndex ;
			ms_nLastProcedureIndex ++ ;
			Rename(fullName) ;
		}
		else
		{
			String fullName = GetName() + "$" + sectionContainer.GetName() ;
			Rename(fullName) ;
		}
	}
	
	protected static int ms_nLastProcedureIndex = 0 ;
	public abstract String ExportReference(int nLine) ;
	/* (non-Javadoc)
	 * @see semantic.CBaseSemanticEntity#DoExport()
	 */
	public boolean ignore()
	{
		if (isignore)
		{
			int n=0;
		}
		return isignore;
	}

	public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
	{
//		return sectionContainer.UpdateAction(entity, newCond);
		for (int i=0; i<lstChildren.size(); i++)
		{
			CBaseActionEntity act = (CBaseActionEntity)lstChildren.get(i) ;
			if (act == entity)
			{
				lstChildren.set(i, newCond) ;
				return true ;
			}
		}
		return false ;
	}
	public void Clear()
	{
		super.Clear();
		sectionContainer = null ;
	}

	/**
	 * @return
	 */
	public boolean hasExplicitGetOut()
	{
		if (lstChildren.isEmpty())
		{
			return false ;
		}
		CBaseActionEntity le = (CBaseActionEntity)lstChildren.getLast() ;
		return le.hasExplicitGetOut() ;
	}

	/**
	 * 
	 */
	public void setIgnore()
	{
		isignore = true ;
	}
	/**
	 * @return
	 */
	public boolean isEmpty()
	{
		boolean isisEmpty = true ;
		for (int i = 0; i<lstChildren.size() && isisEmpty; i++)
		{
			CBaseActionEntity act = (CBaseActionEntity)lstChildren.get(i) ;
			isisEmpty &= act.ignore() ;
		}
		return isisEmpty;
		
	}

	/**
	 * @param endLine
	 */
	public void SetEndLine(int endLine)
	{
		nEndLine = endLine ;
	}

	protected int nEndLine = 0 ;

}
