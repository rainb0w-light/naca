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

public abstract class CEntitySortRelease extends CBaseActionEntity
{

	public CEntitySortRelease(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	protected CDataEntity eSortField = null ;
	
	public void setDataReference(CDataEntity e)
	{
		eSortField = e ;		
	}

	protected CDataEntity eDatReference = null ;
	public void setDataReference(CDataEntity e, CDataEntity from)
	{
		eDatReference = from ;
		eSortField = e ;
	}

}
