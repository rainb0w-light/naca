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
public abstract class CEntityCICSReceiveMap extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSReceiveMap(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity name)
	{
		super(line, cat, out);
		mapName = name ;
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	
	public void SetMapSet(CDataEntity name)
	{
		mapSetName = name ;
	}
	public void SetDataInto(CDataEntity name)
	{
		dataInto = name ;
	}
	
	
	protected CDataEntity mapName = null ;
	protected CDataEntity mapSetName = null ;
	protected CDataEntity dataInto = null ;
	public void Clear()
	{
		super.Clear();
		mapName = null ;
		mapSetName = null ;
		dataInto = null ;
	}
	public boolean ignore()
	{
		return false; 
	}
}
