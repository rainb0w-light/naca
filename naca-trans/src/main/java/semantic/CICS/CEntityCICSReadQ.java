/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 oct. 2004
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
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCICSReadQ extends CBaseActionEntity
{

	public CEntityCICSReadQ(int line, CObjectCatalog cat, CBaseLanguageExporter out, boolean bPersistant)
	{
		super(line, cat, out);
		bPesistant = bPersistant ;
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}

	protected boolean bPesistant = false ;
	protected CDataEntity queueName = null ;
	protected CDataEntity dataRef = null ;
	protected CDataEntity dataLength = null ;
	protected boolean bReadNext = false ;
	protected CDataEntity numItem = null ;
	protected CDataEntity item = null ;

	public void Clear()
	{
		super.Clear();
		queueName = null ;
		dataRef = null ;
		dataLength = null ;
		numItem = null;
		item = null;
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

	public void ReadNext()
	{
		bReadNext = true ; 		
	}

	public void ReadNumItem(CDataEntity entity)
	{
		numItem = entity ;		
	}

	public void ReadItem(CDataEntity entity)
	{
		item = entity ;
	}
	public boolean ignore()
	{
		return false; 
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (dataRef == field)
		{
			dataRef = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}
}
