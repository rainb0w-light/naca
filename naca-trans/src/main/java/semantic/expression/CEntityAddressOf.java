/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityAddressOf extends CBaseEntityFunction
{
	/**
	 * @param cat
	 */
	public CEntityAddressOf(CObjectCatalog cat, CDataEntity data)
	{
		super(cat, data);
	}

	public boolean isValNeeded()
	{
		return false;
	}
}
