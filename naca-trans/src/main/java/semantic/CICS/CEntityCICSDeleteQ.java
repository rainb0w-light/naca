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
public class CEntityCICSDeleteQ extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSDeleteQ(int line, CObjectCatalog cat, boolean ispersistent)
	{
		super(line, cat);
		this.ispersistent = ispersistent;
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}

	public void SetName(CDataEntity name)
	{
		this.name = name ;
	}
	public void SetSysID(CDataEntity sys)
	{
		sysID = sys ;
	}

	protected boolean ispersistent = false ;
	protected CDataEntity name = null ;
	protected CDataEntity sysID = null ;

	public boolean ignore()
	{
		return false ;
	}
	public void Clear()
	{
		super.Clear();
		name = null ;
		sysID = null ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCICSDeleteQEntity). They expose the already-resolved semantic
	// sub-entities; rendering is done by the template, never here.

	public CDataEntity getName()
	{
		return name;
	}

	public CDataEntity getSysID()
	{
		return sysID;
	}

	public boolean isPersistent()
	{
		return ispersistent;
	}

}
