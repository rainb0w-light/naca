/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 29 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CProcedureReference
{

	public CProcedureReference(String csProcedureName, String csSectionName, CObjectCatalog programCatalog)
	{
		csProcedureName = csProcedureName ;	
		csSectionName = csSectionName ;
		programCatalog = programCatalog ;		
	}
	public CEntityProcedure getProcedure()
	{
		return programCatalog.GetProcedure(csProcedureName, csSectionName) ;
	}
	protected String csProcedureName = "" ;
	protected String csSectionName = "" ;
	protected CObjectCatalog programCatalog = null ;
	public void Clear()
	{
		programCatalog = null ;
	}
	public String getProcedureName()
	{
		return csProcedureName;
	}
	
}
