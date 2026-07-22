/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 27, 2004
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
public abstract class CEntityCICSAddress extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSAddress(int line, CObjectCatalog cat)
	{
		super(line, cat);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void SetRefForCWA(CDataEntity e)
	{
		refCWA = e ;
	}
	public void SetRefForTCTUA(CDataEntity e)
	{
		refTCTUA = e ;
	} 
	public void SetRefForTWA(CDataEntity e)
	{
		refTWA = e ;
	}
	
	protected CDataEntity refCWA = null;
	protected CDataEntity refTCTUA = null;
	protected CDataEntity refTWA = null;
	public void Clear()
	{
		super.Clear();
		refCWA = null ;
		refTCTUA = null ;
		refTWA = null ;
	}
}
