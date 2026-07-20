/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityExec extends CBaseActionEntity
{

	/**
	 * @param cat
	 */
	public CEntityExec(int l, CObjectCatalog cat, String statement)
	{
		super(l, cat);
		csStatement = statement ;
	}

	protected String csStatement = "" ;
	public boolean ignore()
	{
		return false ;
	}

	public String getStatement() {
		return csStatement;
	}
}
