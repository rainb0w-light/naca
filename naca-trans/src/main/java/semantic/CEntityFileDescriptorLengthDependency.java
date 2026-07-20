/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author S. Charton
 * @version $Id: CEntityFileDescriptorLengthDependency.java,v 1.1 2006/05/23 11:08:18 u930cv Exp $
 */
public abstract class CEntityFileDescriptorLengthDependency extends
				CBaseLanguageEntity
{

	protected CEntityFileDescriptor fileDescriptor = null ;
	protected CDataEntity lenghtDep = null ;

	/**
	 * @param line
	 * @param name
	 * @param cat
	 */
	public CEntityFileDescriptorLengthDependency(String name, CObjectCatalog cat)
	{
		super(0, name, cat);
	}

	public void setDependency(CEntityFileDescriptor desc, CDataEntity var)
	{
		fileDescriptor = desc ;
		lenghtDep = var ;
	}
	

	/**
	 * @see semantic.CBaseLanguageEntity#RegisterMySelfToCatalog()
	 */
	@Override
	protected void RegisterMySelfToCatalog()
	{
		// nothing
	}	
}
