/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCICSAbend extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSAbend(int line, CObjectCatalog cat)
	{
		super(line, cat);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	
	public void SetABCode(CDataEntity ab)
	{
		aBCode = ab ;
	}
	
	protected CDataEntity aBCode = null ;
	public boolean ignore()
	{
		return false; 
	}
	public void Clear()
	{
		super.Clear();
		aBCode.Clear() ;
		aBCode = null ;
	}
	public boolean hasExplicitGetOut()
	{
		return true ;
	}
}
