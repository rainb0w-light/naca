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
public abstract class CEntityCICSWriteQ extends CBaseActionEntity
{

	public CEntityCICSWriteQ(int line, CObjectCatalog cat, CBaseLanguageExporter out, boolean bPersistant)
	{
		super(line, cat, out);
		bPersistant = bPersistant ;
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}

	protected boolean ispersistant = false ;
	protected CDataEntity queueName = null ;
	protected CDataEntity dataRef = null ;
	protected CDataEntity dataLength = null ;
	protected CDataEntity numItem = null ;
	protected CDataEntity item = null ;
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
