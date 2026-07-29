/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 oct. 2004
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
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityCICSSetTDQueue extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSSetTDQueue(int line, CObjectCatalog cat)
	{
		super(line, cat);
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}
	
	public void SetQueue(CDataEntity e)
	{
		queueName = e ;
	}
	public void SetOpen(boolean bOpen)
	{
		isopen = bOpen ;
		isclosed = !bOpen ;
	}
		
	protected CDataEntity queueName = null ;
	protected boolean isopen = false ;
	protected boolean isclosed = false ;
	public void Clear()
	{
		super.Clear();
		queueName = null ;
		isopen = false ;
		isclosed = false ;
	}
	public boolean ignore()
	{
		return false; 
	}
	public CDataEntity getQueueName() { return queueName; }
	public boolean isOpen() { return isopen; }
	public boolean isClosed() { return isclosed; }
}
