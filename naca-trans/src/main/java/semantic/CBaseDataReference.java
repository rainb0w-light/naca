/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 24 nov. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import generate.CBaseLanguageExporter;
import semantic.expression.CBaseEntityCondExpr;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityResourceForm;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CBaseDataReference extends CGenericDataEntityReference
{
	public CBaseDataReference(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat, out);
	}
	protected CDataEntity reference = null ;
	
	public int getNbDimOccurs()
	{
		return reference.getNbDimOccurs();
	}
	
	public void RegisterReadingAction(CBaseActionEntity act)
	{
		reference.RegisterReadReference(this) ;
		super.RegisterReadingAction(act);
	}
	public void RegisterValueAccess(CBaseEntityCondExpr cond)
	{
		super.RegisterValueAccess(cond) ;
		reference.RegisterReadReference(this) ;
	}
	public void RegisterVarTesting(CBaseEntityCondition cond)
	{
		super.RegisterVarTesting(cond) ;
		reference.RegisterReadReference(this);
	}
	public void RegisterWritingAction(CBaseActionEntity act)
	{
		reference.RegisterWriteReference(this);
		super.RegisterWritingAction(act);
	}

	public void IgnoreReadingActions(CDataEntity field)
	{
		if (field == reference)
		{
			for (int i=0; i<arrActionsReading.size(); i++)
			{
				CBaseActionEntity act = arrActionsReading.get(i);
				act.IgnoreVariable(this);
			}
		}
	}

	public void IgnoreWritingActions(CDataEntity field)
	{
		if (field == reference)
		{
			for (int i=0; i<arrActionsWriting.size(); i++)
			{
				CBaseActionEntity act = arrActionsWriting.get(i);
				act.IgnoreVariable(this);
			}
		}		
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var, boolean bRead)
	{
		if (field == reference)
		{
			reference = var ;
			if (bRead)
			{
				field.UnRegisterReadReference(this) ;
				var.RegisterReadReference(this) ;
			}
			else
			{
				field.UnRegisterWriteReference(this) ;
				var.RegisterWriteReference(this) ;
			}
			return true ;
		}		
		return false ;
	}
	public boolean IgnoreVariable(CEntityResourceForm sav)
	{
		if (sav == reference)
		{
			reference = null ;
			sav.UnRegisterReadReference(this) ;
			sav.UnRegisterWriteReference(this) ;
			return true ;
		}
		return false ;
	}
	public boolean ignore()
	{
		return reference == null ;
	} 
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#Clear()
	 */
	public void Clear()
	{
		super.Clear();
		if (reference != null)
			reference.Clear() ;
		reference = null ;
	}
	

//	/**
//	 * @see semantic.CBaseLanguageEntity#GetHierarchy()
//	 */
//	@Override
//	public CEntityHierarchy GetHierarchy()
//	{
//		if (reference != null)
//		{
//			return reference.GetHierarchy();
//		}
//		return null ;
//	}	
}
