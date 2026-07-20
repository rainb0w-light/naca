/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

public class CEntityAccept extends CBaseActionEntity
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
	
	public CEntityAccept(int line, CObjectCatalog cat)
	{
		super(line, cat);
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

	public CDataEntity getVariable()
	{
		return eVariable;
	}

	public CDataEntity getSource()
	{
		return eSource;
	}

	public boolean isFromDate()
	{
		return eMode == AcceptMode.FROM_DATE;
	}

	public boolean isFromDay()
	{
		return eMode == AcceptMode.FROM_DAY;
	}

	public boolean isFromDayOfWeek()
	{
		return eMode == AcceptMode.FROM_DAYOFWEEK;
	}

	public boolean isFromInput()
	{
		return eMode == AcceptMode.FROM_INPUT;
	}

	public boolean isFromTime()
	{
		return eMode == AcceptMode.FROM_TIME;
	}

	public boolean isFromEnvironmentValue()
	{
		return eMode == AcceptMode.FROM_ENVIRONMENT_VALUE;
	}

	public boolean isFromVariable()
	{
		return eMode == AcceptMode.FROM_VARIABLE;
	}
	

}
