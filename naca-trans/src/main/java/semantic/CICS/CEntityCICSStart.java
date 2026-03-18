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

import generate.CBaseLanguageExporter;
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
public abstract class CEntityCICSStart extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSStart(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity TID)
	{
		super(line, cat, out);
		transID = TID ;
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	
	public void SetInterval(CDataEntity inter)
	{
		interval = inter ;
	}
	public void SetTime(CDataEntity time)
	{
		time = time ;
	}
	public void SetDataFrom(CDataEntity from, CDataEntity len)
	{
		dataFrom = from ;
		dataLength = len ;
	}
	public void SetSysID(CDataEntity sys)
	{
		sysID = sys ;
	}
	public void SetTermID(CDataEntity term)
	{
		termID = term ;
	}
	
	protected CDataEntity transID = null ;
	protected CDataEntity termID = null ;
	protected CDataEntity sysID = null ;
	
	protected CDataEntity interval = null ;
	protected CDataEntity time = null ;
	
	protected CDataEntity dataFrom = null ;
	protected CDataEntity dataLength = null ;
	public void Clear()
	{
		super.Clear();
		transID = null ;
		termID = null ;
		sysID = null ;
		interval = null ;
		time = null ;
		dataFrom = null ;
		dataLength = null ;
	}
	public boolean ignore()
	{
		return false; 
	}

	/**
	 * @param checked
	 */
	public void setVerified(boolean checked)
	{
		bVerified = checked ;
	}
	protected boolean bVerified = false ;
}
