/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import generate.*;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityMoveReference extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityMoveReference(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	public void SetMoveReference(CEntityAddressReference from, CEntityAddressReference to)
	{
		from = from ;
		to = to;
	}
	protected CEntityAddressReference from = null ;
	protected CEntityAddressReference to = null ;
	public boolean ignore()
	{
		return from.ignore() || to.ignore();
	}
	public void Clear()
	{
		super.Clear();
		from.Clear() ;
		to.Clear() ;
		from = null ;
		to = null ;
	}
}
