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
public abstract class CEntityCICSStartBrowse extends CBaseActionEntity
{
	protected boolean bGTEQ = false ;
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
	 * @param out
	 */
	public CEntityCICSStartBrowse(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void SetGTEQ()
	{
		bGTEQ = true ;		
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
}
