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
public abstract class CEntityCICSRead extends CBaseActionEntity
{
	public static class CEntityCICSReadMode
	{
		public static CEntityCICSReadMode NORMAL = new CEntityCICSReadMode() ;
		public static CEntityCICSReadMode PREVIOUS = new CEntityCICSReadMode() ;
		public static CEntityCICSReadMode NEXT = new CEntityCICSReadMode() ;
	}
	public CEntityCICSRead(int line, CObjectCatalog cat, CBaseLanguageExporter out, CEntityCICSReadMode mode)
	{
		super(line, cat, out);
		mode = mode ;
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void ReadFile(CDataEntity name)
	{
		name = name ;
		isreadtoDataSet = false ;
		isreadToFile = true ;
	}
	public void ReadDataSet(CDataEntity name)
	{
		name = name ;
		isreadtoDataSet = true ;
		isreadToFile = false ;
	}
	public void SetDataInto(CDataEntity from, CDataEntity length)
	{
		dataInto = from ;
		dataLength = length;
	}
	public void SetRecIDField(CDataEntity rec)
	{
		recIDField = rec ;
	}
	
	protected CDataEntity recIDField = null ;
	protected CDataEntity dataInto = null ;
	protected CDataEntity dataLength = null ;
	protected CDataEntity name ;
	protected boolean isreadToFile = false ;
	protected boolean isreadtoDataSet = false ;
	protected CEntityCICSReadMode mode = null ;
	protected CDataEntity keyLength = null ;
	protected boolean isequal = false ;

	public void SetKeyLength(CDataEntity entity)
	{
		keyLength = entity ;
	}

	public void SetEqual()
	{
		isequal = true ;
	}
	public boolean ignore()
	{
		return false; 
	}
	public void Clear()
	{
		super.Clear();
		recIDField = null ;
		dataInto = null ;
		dataLength = null ;
		name = null ;
		keyLength = null ;
		mode = null ;
	}
}
