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
public class CEntityCICSWriteQ extends CBaseActionEntity
{

	public CEntityCICSWriteQ(int line, CObjectCatalog cat, boolean bPersistant)
	{
		super(line, cat);
		ispersistant = bPersistant ;
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}

	protected boolean ispersistant = false ;
	protected CDataEntity queueName = null ;
	protected CDataEntity dataRef = null ;
	protected CDataEntity dataLength = null ;
	protected CDataEntity numItem = null ;
	protected CDataEntity item = null ;
	protected CDataEntity sysID = null ;
	protected boolean bAuxiliary = false ;
	protected boolean bMain = false ;
	protected boolean bRewrite = false ;
	public void Clear()
	{
		super.Clear();
		queueName = null ;
		dataLength = null ;
		dataRef = null ;
		numItem = null ;
		item = null ;
		sysID = null ;
		bAuxiliary = false ;
		bMain = false ;
		bRewrite = false ;
	}

	public void SetName(CDataEntity entity)
	{
		queueName = entity ;
	}
	public void SetDataRef(CDataEntity entity, CDataEntity len)
	{
		dataRef = entity ;
		dataLength = len ;
	}

	public void WriteNumItem(CDataEntity entity)
	{
		numItem = entity ;
	}

	public void WriteItem(CDataEntity entity)
	{
		item = entity ;
	}

	public void SetSysID(CDataEntity entity)
	{
		sysID = entity ;
	}

	public void SetRewrite()
	{
		bRewrite = true ;
	}

	public void SetMain()
	{
		bMain = true ;
	}

	public void SetAuxiliary()
	{
		bAuxiliary = true ;
	}
	public boolean ignore()
	{
		return false;
	}

	public boolean isPersistent() { return ispersistant; }
	public CDataEntity getQueueName() { return queueName; }
	public CDataEntity getDataRef() { return dataRef; }
	public CDataEntity getDataLength() { return dataLength; }
	public CDataEntity getNumItem() { return numItem; }
	public CDataEntity getItem() { return item; }
	public CDataEntity getSysID() { return sysID; }
	public boolean isAuxiliary() { return bAuxiliary; }
	public boolean isMain() { return bMain; }
	public boolean isRewrite() { return bRewrite; }
	public boolean isItemOutput() { return item != null && !bRewrite; }

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (dataRef == field)
		{
			dataRef = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
}
