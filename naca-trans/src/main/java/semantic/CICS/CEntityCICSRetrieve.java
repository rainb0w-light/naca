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
public abstract class CEntityCICSRetrieve extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSRetrieve(int line,CObjectCatalog cat, CBaseLanguageExporter out, boolean bPointer)
	{
		super(line, cat, out);
		bPointer = bPointer ;
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	
	public void SetRetrieve(CDataEntity into, CDataEntity length)
	{
		refInto = into ;
		dataLength = length ;
	}
	
	protected CDataEntity refInto = null;
	protected CDataEntity dataLength = null ;
	protected boolean bPointer = false ; 
	public void Clear()
	{
		super.Clear();
		refInto = null ;
		dataLength = null ;
	}
	public boolean ignore()
	{
		return false; 
	}
}
