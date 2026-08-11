/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CEntityBloc;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntitySearch extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntitySearch(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	/**
	 * @param var
	 * @param index
	 */
	public void setVariable(CDataEntity var, CDataEntity index)
	{
		eVariable = var ;
		eIndex = index ;
	}
	protected CDataEntity eVariable = null ;
	protected CDataEntity eIndex = null ;
	protected CEntityBloc blocElse = null;
	/**
	 * @param bloc
	 */
	public void setElseBloc(CEntityBloc bloc)
	{
		blocElse = bloc ;
	}

	public CDataEntity getVariable()
	{
		return eVariable;
	}

	public CDataEntity getIndex()
	{
		return eIndex;
	}

	public CEntityBloc getElseBloc()
	{
		return blocElse;
	}


}
