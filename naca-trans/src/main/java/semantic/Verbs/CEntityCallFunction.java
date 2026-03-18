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
import semantic.CEntityProcedure;
import semantic.CEntityProcedureSection;
import semantic.CProcedureReference;
import utils.*;


/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCallFunction extends CBaseActionEntity
{
	/**
	 * @param cat
	 * @param out
	 */
	public CEntityCallFunction(int l, CObjectCatalog cat, CBaseLanguageExporter out, String ref, String refThru, CEntityProcedureSection sectionContainer)
	{
		super(l, cat, out);
		String sec= "";
		if (sectionContainer != null)
		{
			sec = sectionContainer.GetName();
		}
		if (!ref.equals(""))
		{	
			reference = new CProcedureReference(ref, sec, cat) ;
			cat.getCallTree().RegisterProcedureCall(this) ;
		}	
		if (!refThru.equals(""))
		{
			referenceThru = new CProcedureReference(refThru, sec, cat) ;;
		}
	}
	protected CProcedureReference reference = null;
	protected CProcedureReference referenceThru = null ;
	public void Clear()
	{
		super.Clear();
		if (reference != null)
		{	
			reference.Clear() ;
		}
		if (referenceThru != null)
		{
			referenceThru.Clear() ;
		}
		reference = null;
		referenceThru = null ;
	}
	public boolean ignore()
	{
		if (referenceThru == null)
		{
			if (reference != null && reference.getProcedure() != null)
			{	
				return reference.getProcedure().ignore() ;
			}	
		}
		return false ;
	}

	public boolean IgnoreVariable(CDataEntity data)
	{
		return false ;
	}
	/**
	 * @return
	 */
	public CEntityProcedure getFirstProcedure()
	{
		return reference.getProcedure() ;
	}
	/**
	 * @return
	 */
	public CEntityProcedure getLastProcedure()
	{
		return referenceThru.getProcedure() ;
	}
	
	public boolean hasExplicitGetOut()
	{
		CEntityProcedure proc = reference.getProcedure() ;
		return proc.hasExplicitGetOut() ;
	}
	/**
	 * @return
	 */
	public CProcedureReference getReference()
	{
		return reference ;
	}
	public void SetRepetitions(CDataEntity entity)
	{
		refRepetitions = entity ;
	}
	protected CDataEntity refRepetitions = null;

}
