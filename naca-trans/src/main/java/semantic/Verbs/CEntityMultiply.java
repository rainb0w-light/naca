/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 sept. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityMultiply extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityMultiply(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	protected CDataEntity what = null ;
	protected CDataEntity by = null ;
	protected CDataEntity to = null ;
	protected boolean isisRounded = false ;
	public void Clear()
	{
		super.Clear() ;
		what = null ;
		by = null ;
		to = null ;
	}
	
	public void SetMultiply(CDataEntity what, CDataEntity by, CDataEntity to, boolean isRounded)
	{
		what = what ;
		by = by ;
		to = to ; 
		isisRounded = isRounded ;
	}
	public void SetMultiply(CDataEntity what, CDataEntity by, boolean isRounded)
	{
		what = what ;
		by = by ;
		to = by ; 
		isisRounded = isRounded ;
	}
	public boolean ignore()
	{
		boolean ignore = what.ignore();
		ignore |= by.ignore();
		ignore |= to.ignore() ;
		return ignore ; 
	}
}
