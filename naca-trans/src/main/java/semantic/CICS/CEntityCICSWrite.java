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
public class CEntityCICSWrite extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSWrite(int line, CObjectCatalog cat)
	{
		super(line, cat);
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}
	public void WriteFile(CDataEntity dataName)
	{
		name = dataName ;
		iswritetoDataSet = false ;
		iswriteToFile = true ;
	}
	public void WriteDataSet(CDataEntity dataName)
	{
		name = dataName ;
		iswritetoDataSet = true ;
		iswriteToFile = false ;
	}
	public void SetDataFrom(CDataEntity from, CDataEntity length)
	{
		dataFrom = from ;
		dataLength = length ;
	}
	public void SetRecIDField(CDataEntity rec)
	{
		recIDField = rec ;
	}
	public void SetKeyLength(CDataEntity length)
	{
		keyLength = length ;
	}
	
	protected CDataEntity recIDField = null ;
	protected CDataEntity dataFrom = null ;
	protected CDataEntity dataLength = null ;
	protected CDataEntity keyLength = null ;
	protected CDataEntity name ;
	protected boolean iswriteToFile = false ;
	protected boolean iswritetoDataSet = false ;
	public void Clear()
	{
		super.Clear();
		recIDField = null ;
		dataFrom = null ;
		dataLength = null ;
		keyLength = null ;
		name = null ;
		iswriteToFile = false ;
		iswritetoDataSet = false ;
	}
	public boolean ignore()
	{
		return false; 
	}
	public CDataEntity getName() { return name; }
	public CDataEntity getDataFrom() { return dataFrom; }
	public CDataEntity getDataLength() { return dataLength; }
	public CDataEntity getRecIDField() { return recIDField; }
	public CDataEntity getKeyLength() { return keyLength; }
	public CDataEntity getDataFromOwner()
	{
		return dataFrom == null ? null : dataFrom.of;
	}
	public boolean isWriteToFile() { return iswriteToFile; }
	public boolean isWriteToDataSet() { return iswritetoDataSet; }
	public boolean isWriteStatistics()
	{
		return iswritetoDataSet && dataFrom != null
			&& "CUM-COLL".equals(dataFrom.GetName()) && dataFrom.of != null;
	}
}
