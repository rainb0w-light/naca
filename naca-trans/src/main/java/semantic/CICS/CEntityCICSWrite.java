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
public abstract class CEntityCICSWrite extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSWrite(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void WriteFile(CDataEntity name)
	{
		name = name ;
		bWritetoDataSet = false ;
		bWriteToFile = true ;
	}
	public void WriteDataSet(CDataEntity name)
	{
		name = name ;
		bWritetoDataSet = true ;
		bWriteToFile = false ;
	}
	public void SetDataFrom(CDataEntity from)
	{
		dataFrom = from ;
	}
	public void SetRecIDField(CDataEntity rec)
	{
		recIDField = rec ;
	}
	
	protected CDataEntity recIDField = null ;
	protected CDataEntity dataFrom = null ;
	protected CDataEntity name ;
	protected boolean bWriteToFile = false ;
	protected boolean bWritetoDataSet = false ;
	public void Clear()
	{
		super.Clear();
		recIDField = null ;
		dataFrom = null ;
		name = null ;
	}
	public boolean ignore()
	{
		return false; 
	}
}
