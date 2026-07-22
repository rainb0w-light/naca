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

/**
 * @author S. Charton
 * @version $Id: CEntityInc.java,v 1.1 2006/03/07 15:31:58 U930CV Exp $
 */
public class CEntityInc extends CBaseActionEntity
{

	protected CDataEntity addDest;
	protected CDataEntity addValue;

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityInc(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	public CDataEntity getAddDest() { return addDest; }
	public CDataEntity getAddValue() { return addValue; }

	/**
	 * @param dest
	 */
	public void SetAddDest(CDataEntity dest)
	{
		addDest = dest ;		
	}

	/**
	 * @param val
	 */
	public void SetAddValue(CDataEntity val)
	{
		addValue = val ;
	}


}
