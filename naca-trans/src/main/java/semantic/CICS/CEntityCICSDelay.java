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
public class CEntityCICSDelay extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSDelay(int line, CObjectCatalog cat)
	{
		super(line, cat);
		// The catalog notification is a production-only side effect; the ST4 render
		// tests instantiate this entity directly with a null catalog (like the READ
		// and CICS ABEND exemplars), so guard it instead of dereferencing
		// unconditionally.
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}

	public void SetSeconds(CDataEntity entity)
	{
		seconds = entity;
	}

	public void SetInterval(CDataEntity entity)
	{
		interval = entity ;
	}

	protected CDataEntity interval = null ;
	protected CDataEntity seconds = null ;

	public boolean ignore()
	{
		return false ;
	}
	public void Clear()
	{
		super.Clear();
		if (interval != null)
		{
			interval.Clear() ;
		}
		if (seconds!=null)
		{
			seconds.Clear() ;
			seconds = null ;
		}
		interval = null ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCICSDelayEntity). They expose the already-resolved semantic
	// sub-entities; rendering is done by the template, never here.

	public CDataEntity getInterval()
	{
		return interval;
	}

	public CDataEntity getSeconds()
	{
		return seconds;
	}
}
