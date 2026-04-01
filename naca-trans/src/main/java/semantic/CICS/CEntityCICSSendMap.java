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
import semantic.forms.CEntityResourceForm;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCICSSendMap extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSSendMap(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void SetName(CDataEntity name)
	{
		this.name = name ;
	}
	public void SetMapSet(CDataEntity name)
	{
		setName = name ;
	}
	
	public void SetDataFrom(CDataEntity from, CDataEntity len, boolean b)
	{
		if (from.GetDataType() == CDataEntity.CDataEntityType.FORM)
		{
			CEntityResourceForm form = (CEntityResourceForm)from ;
			if (form.getSaveCopy() != null)
			{
				form.UnRegisterReadingAction(this) ;
				dataFrom = form.getSaveCopy() ;
				dataFrom.RegisterReadingAction(this) ;
			}
			else
			{
				dataFrom = from ;
			}
		}
		else
		{
			dataFrom = from ;
		}
		dataLength = len ;
		isdataOnly = b ;
	}
	
	public void SetAccum(boolean b)
	{
		isaccum = b ;
	}
	
	public void SetAlarm(boolean b)
	{
		isalarm = b ;
	}
	
	public void SetErase(boolean b)
	{
		iserase = b ;
	}
	
	public void SetFreeKB(boolean b)
	{
		isfreeKB = b ;
	}
	
	public void SetPaging(boolean b)
	{
		ispaging = b ;
	}
	
	public void SetWait(boolean b)
	{
		iswait = b ;
	}
	
	public void SetCursor(CDataEntity e)
	{
		bCursor = true ;
		cursorValue = e ;
	}
	
	protected CDataEntity name = null ;
	protected CDataEntity setName = null ;
	protected CDataEntity dataFrom = null ;
	protected CDataEntity dataLength = null ;
	protected CDataEntity cursorValue = null ;
	protected boolean isfreeKB = false ;
	protected boolean isdataOnly = false ;
	protected boolean bCursor = false ;
	protected boolean iserase = false ;
	protected boolean isalarm = false ;
	protected boolean iswait = false ;
	protected boolean isaccum = false ;
	protected boolean ispaging = false ;
	public void Clear()
	{
		super.Clear();
		name = null ;
		setName = null ;
		dataFrom = null ;
		dataLength = null ;
		cursorValue = null ;
	}
	public boolean ignore()
	{
		return false; 
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (dataFrom == field)
		{
			field.UnRegisterReadingAction(this) ;
			dataFrom = var ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}
	
}
