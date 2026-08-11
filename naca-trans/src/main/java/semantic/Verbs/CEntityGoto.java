/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CEntityProcedureSection;
import semantic.CProcedureReference;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityGoto extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityGoto(int line, CObjectCatalog cat, String ref, CEntityProcedureSection sectionContainer)
	{
		super(line, cat);
		String sec= "";
		if (sectionContainer != null)
		{
			sec = sectionContainer.GetName();
		}
		reference = new CProcedureReference(ref, sec, cat) ;
		cat.getCallTree().RegisterGoto(this) ;
	}

	/**
	 * Target-neutral GO TO target: the referenced procedure's formatted name,
	 * matching the procedure method declaration rendered by the ST4 templates.
	 */
	public String getGoToTarget()
	{
		semantic.CEntityProcedure e = reference.getProcedure();
		return e != null ? e.GetDisplayName() : "[UNDEFINED]";
	}

	/**
	 * Raw control token captured by semantic analysis. COBOL resolves this to a
	 * paragraph for {@link #getGoToTarget()}, while FPac uses the reserved
	 * {@code NEXT}/{@code LAST}/{@code END} integer control values directly.
	 */
	public String getTargetName()
	{
		return reference != null ? reference.getProcedureName() : "[UNDEFINED]" ;
	}

	protected CProcedureReference reference = null;
	public void Clear()
	{
		super.Clear() ;
		reference.Clear() ;
		reference= null ;
	}
	public boolean ignore()
	{
		return false ;
	}
	public boolean hasExplicitGetOut()
	{
		return true ;
	}
	/**
	 * @return
	 */
	public CProcedureReference getReference()
	{
		return reference ;
	}
}
