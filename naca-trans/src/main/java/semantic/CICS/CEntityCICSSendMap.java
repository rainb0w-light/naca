/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.forms.CEntityResourceForm;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 */
public class CEntityCICSSendMap extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSSendMap(int line, CObjectCatalog cat)
	{
		super(line, cat);
		// The catalog notification is a production-only side effect; the ST4 render
		// tests instantiate this entity directly with a null catalog (like the READ
		// and CICS RECEIVE exemplars), so guard it instead of dereferencing unconditionally.
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
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

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCICSSendMapEntity). They expose the already-resolved semantic
	// sub-entities and option flags; rendering is done by the template, never here.
	// The reference getters return null when absent so the template's <if(entity.xxx)>
	// selects exactly the active parts, mirroring the per-part guard the retired
	// direct SEND MAP backend applied.

	public CDataEntity getName()
	{
		return name;
	}

	public CDataEntity getSetName()
	{
		return setName;
	}

	public CDataEntity getDataFrom()
	{
		return dataFrom;
	}

	public CDataEntity getDataLength()
	{
		return dataLength;
	}

	public CDataEntity getCursorValue()
	{
		return cursorValue;
	}

	public boolean isCursor()
	{
		return bCursor;
	}

	public boolean isDataOnly()
	{
		return isdataOnly;
	}

	public boolean isAccum()
	{
		return isaccum;
	}

	public boolean isAlarm()
	{
		return isalarm;
	}

	public boolean isErase()
	{
		return iserase;
	}

	public boolean isFreeKB()
	{
		return isfreeKB;
	}

	public boolean isPaging()
	{
		return ispaging;
	}

	public boolean isWait()
	{
		return iswait;
	}
}
