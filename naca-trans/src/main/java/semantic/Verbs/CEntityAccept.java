/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

public abstract class CEntityAccept extends CBaseActionEntity
{
	public enum AcceptMode
	{
		FROM_INPUT,
		FROM_DATE,
		FROM_DAY,
		FROM_DAYOFWEEK,
		FROM_TIME,
		FROM_VARIABLE,
		FROM_ENVIRONMENT_VALUE,
	}
	
	public CEntityAccept(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	protected CDataEntity eVariable = null  ;
	protected CDataEntity eSource = null ;
	protected AcceptMode eMode ;

	public void AcceptFrom(AcceptMode mode, CDataEntity var)
	{
		eMode = mode ;
		eVariable = var ;		
	}

	public void AcceptFromVariable(CDataEntity var, CDataEntity source)
	{
		eMode = AcceptMode.FROM_VARIABLE;
		eVariable = var ;		
		eSource = source ;
	}
	
	

}
