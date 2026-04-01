/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 oct. 2004
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
public abstract class CEntityCICSReWrite extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSReWrite(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
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

}
