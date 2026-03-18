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
public abstract class CEntityCICSReturn extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSReturn(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void SetTransID(CDataEntity TID, CDataEntity comma, CDataEntity comlen, boolean bChecked)
	{
		transID = TID;
		commArea = comma ;
		commLenght = comlen ;
		bChecked = bChecked ;
	}
	
	protected boolean bChecked = false ;
	protected CDataEntity transID = null ;
	protected CDataEntity commArea = null ;
	protected CDataEntity commLenght = null ;
	public void Clear()
	{
		super.Clear();
		transID = null ;
		commArea = null ;
		commLenght = null ;
	}
	public boolean ignore()
	{
		return false; 
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (transID == field)
		{
			transID = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		else if (commArea == field)
		{
			commArea = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}
	public boolean hasExplicitGetOut()
	{
		return true ;
	}
}
