/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Oct 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import java.util.Vector;

import generate.CBaseLanguageExporter;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityProcedureDivision extends CBaseLanguageEntity
{
	/**
	 * @param line
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CEntityProcedureDivision(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, "", cat, out);
		cat.RegisterProcedureDivision(this) ;
	}

	protected void RegisterMySelfToCatalog()
	{
		// nothing
	}

	protected Vector<CDataEntity> callParameters = new Vector<CDataEntity>();
	public void AddCallParameter(CDataEntity e)
	{
		callParameters.add(e) ;
	}
	public Vector<CDataEntity> getCallParameters()
	{
		return callParameters;
	}
	
	protected CEntityBloc procedureBloc =null ;
	public void SetProcedureBloc(CEntityBloc b)
	{
		procedureBloc = b ;
	}
	public CEntityBloc getProcedureBloc()
	{
		return procedureBloc ;
	}
	public CEntityProcedureSection getSectionContainer()
	{
		return null ;
	} 
	public boolean ignore()
	{
		return false ;
	}
	public void Clear()
	{
		super.Clear();
		callParameters.clear() ;
		if (procedureBloc!=null)
		{
			procedureBloc.Clear() ;
		}
		procedureBloc = null ;
	}

	/**
	 * @return
	 */
	public boolean hasExplicitGetout()
	{
		if (procedureBloc == null)
		{
			return false;
		}
		else
		{
			return procedureBloc.hasExplicitGetOut() ;
		}
	}
}