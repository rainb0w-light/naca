/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 */
public class CEntityCICSStart extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSStart(int line, CObjectCatalog cat, CDataEntity TID)
	{
		super(line, cat);
		transID = TID ;
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}

	public void SetInterval(CDataEntity inter)
	{
		interval = inter ;
	}
	public void SetTime(CDataEntity time)
	{
		this.time = time ;
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
		isverified = checked ;
	}
	protected boolean isverified = false ;

	public CDataEntity getTransID() { return transID; }
	public CDataEntity getTermID() { return termID; }
	public CDataEntity getSysID() { return sysID; }
	public CDataEntity getInterval() { return interval; }
	public CDataEntity getTime() { return time; }
	public CDataEntity getDataFrom() { return dataFrom; }
	public CDataEntity getDataLength() { return dataLength; }
	public boolean isVerified() { return isverified; }
	public String getTransIDConstantValue()
	{
		return transID == null ? null : transID.GetConstantValue();
	}
}
