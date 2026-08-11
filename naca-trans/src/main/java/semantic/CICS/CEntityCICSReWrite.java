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
public class CEntityCICSReWrite extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSReWrite(int line, CObjectCatalog cat)
	{
		super(line, cat);
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}

	public void WriteFile(CDataEntity filename)
	{
		iswriteToFile = true ;
		iswritetoDataSet = false ;
		name = filename;
	}

	public void WriteDataSet(CDataEntity filename)
	{
		iswritetoDataSet = true ;
		iswriteToFile = false ;
		name = filename;
	}

	public void SetDataFrom(CDataEntity edata, CDataEntity eLen)
	{
		dataFrom = edata ;
		dataLength = eLen ;
	}

	protected CDataEntity dataLength = null ;
	protected CDataEntity dataFrom = null ;
	protected CDataEntity name ;
	protected boolean iswriteToFile = false ;
	protected boolean iswritetoDataSet = false ;
	public void Clear()
	{
		super.Clear();
		dataLength = null ;
		dataFrom = null;
		name = null ;
	}
	public boolean ignore()
	{
		return false;
	}

	public CDataEntity getDataLength()
	{
		return dataLength;
	}

	public CDataEntity getDataFrom()
	{
		return dataFrom;
	}

	public CDataEntity getName()
	{
		return name;
	}

	public boolean isWriteToFile()
	{
		return iswriteToFile;
	}

	public boolean isWriteToDataSet()
	{
		return iswritetoDataSet;
	}

}
