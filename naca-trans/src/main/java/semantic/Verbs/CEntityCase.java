/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCase extends CBaseActionEntity
{

	/**
	 * @param cat
	 * @param out
	 */
	public CEntityCase(int l, CObjectCatalog cat, CBaseLanguageExporter out, int nEndLine)
	{
		super(l, cat, out);
		nEndBlocLine = nEndLine ;
	}
	public void SetCondition(CBaseEntityCondition exp)
	{
		//ASSERT(exp);
		condition = exp ;
	}
	
	protected CBaseEntityCondition condition = null  ; 
	protected int nEndBlocLine = 0 ;
	public void Clear()
	{
		super.Clear() ;
		if (condition!=null)
		{
			condition.Clear() ;
		}
		condition = null ;
	}

	public boolean ignore()
	{
		if (condition != null)
		{
			boolean ignore = condition.ignore() ;
			//ignore |= isChildrenIgnored() ;
			return ignore ;
		}
		else
		{
			return isChildrenIgnored() ;
		}
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		return false ;
	}

	public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
	{
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

}