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
 * @author sly
 *
 */
public class CEntityCICSStartBrowse extends CBaseActionEntity
{
	protected boolean isgTEQ = false ;
	protected CDataEntity dataSet = null ;
	protected CDataEntity recIDField = null ;
	protected CDataEntity keyLength = null ;
	public void Clear()
	{
		super.Clear();
		dataSet = null ;
		recIDField = null ;
		keyLength = null ;
	}

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSStartBrowse(int line, CObjectCatalog cat)
	{
		super(line, cat);
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}
	public void SetGTEQ()
	{
		isgTEQ = true ;
	}
	public void BrowseDataSet(CDataEntity entity)
	{
		dataSet = entity ;
	}
	public void SetRecIDField(CDataEntity entity)
	{
		recIDField = entity ;
	}
	public boolean ignore()
	{
		return false;
	}
	/**
	 * @param entity
	 */
	public void SetKeyLength(CDataEntity entity)
	{
		keyLength = entity ;
	}
	public boolean isGTEQ() { return isgTEQ; }
	public CDataEntity getDataSet() { return dataSet; }
	public CDataEntity getRecIDField() { return recIDField; }
	public CDataEntity getKeyLength() { return keyLength; }
}
