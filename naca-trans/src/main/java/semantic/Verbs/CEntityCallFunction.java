/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

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
public class CEntityCallFunction extends CBaseActionEntity
{
	/**
	 * @param cat
	 */
	public CEntityCallFunction(int l, CObjectCatalog cat, String ref, String refThru, CEntityProcedureSection sectionContainer)
	{
		super(l, cat);
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

	// ==================== ST4 recursive accessors ====================

	/** PERFORM A THRU B form. */
	public boolean isPerformThrough()
	{
		return referenceThru != null ;
	}

	/** PERFORM N TIMES repetition operand (a data reference), or null. */
	public CDataEntity getRepetitions()
	{
		return refRepetitions ;
	}

	private String repetitionIndex = null ;

	/** Unique loop index identifier for the PERFORM N TIMES form. */
	public String getRepetitionIndex()
	{
		if (repetitionIndex == null)
		{
			String index = "loop_index" ;
			while (programCatalog != null && programCatalog.IsExistingDataEntity(index, ""))
			{
				index += "$" ;
			}
			repetitionIndex = index ;
		}
		return repetitionIndex ;
	}

	/** Target paragraph/section name of a simple PERFORM, or null when absent. */
	public String getPerformedProcedureName()
	{
		if (reference != null)
		{
			CEntityProcedure procedure = reference.getProcedure() ;
			if (procedure != null)
			{
				return procedure.GetDisplayName() ;
			}
		}
		return null ;
	}

	/** THRU target paragraph/section name, or null when absent. */
	public String getPerformedThroughName()
	{
		if (referenceThru != null)
		{
			CEntityProcedure procedure = referenceThru.getProcedure() ;
			if (procedure != null)
			{
				return procedure.GetDisplayName() ;
			}
		}
		return null ;
	}

	/**
	 * FPac DOSUBR target: the resolved called procedure's raw name, or null when the
	 * reference is absent/unresolved. The FPac template formats it with the same
	 * identifier rules the generated paragraph method is named with, so this getter only
	 * reads the already-resolved reference built during semantic analysis — no lowering,
	 * formatting or catalog mutation happens when the template accesses it.
	 */
	public String getCalledProcedureName()
	{
		if (reference != null)
		{
			CEntityProcedure procedure = reference.getProcedure() ;
			if (procedure != null)
			{
				return procedure.GetName() ;
			}
		}
		return null ;
	}
}
