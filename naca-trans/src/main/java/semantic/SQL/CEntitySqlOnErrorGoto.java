/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 4 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

import semantic.CBaseActionEntity;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntitySqlOnErrorGoto extends CBaseActionEntity
{

	/**
	 * @param cat
	 */
	public CEntitySqlOnErrorGoto(int l, CObjectCatalog cat, String Reference, boolean OnWarning)
	{
		super(l, cat);
		csRef = Reference ; 
		isonWarning = OnWarning ;
	}
	
	protected String csRef = "" ;
	protected boolean isonWarning = false ;
	public boolean ignore()
	{
		return false ;
	}
}
