/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityReturn extends CBaseActionEntity
{

	/**
	 * @param cat
	 * @param out
	 */
	public CEntityReturn(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat, out);
	}

	public void SetStopProgram(int returning)
	{
		isstopAllStackCalls = returning;
	}
	
	public void SetOnlyReturnFromProcedure()
	{
		bonlyLeaveParagraph = true;
	}
	
	protected int isstopAllStackCalls = -1 ;
	protected boolean bonlyLeaveParagraph = false ;
	public boolean ignore()
	{
		return false ;
	}
	public boolean hasExplicitGetOut()
	{
		return true ;
	}
}
