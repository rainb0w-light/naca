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
public abstract class CEntityCICSLink extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSLink(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		cat.RegisterCICSLink(this);
	}
	
	public void SetProgramName(CDataEntity prgm, boolean bChecked)
	{
		refProgram = prgm ;
		bChecked = bChecked ;
	}
	
	protected boolean bChecked = false ;
	protected CDataEntity refProgram = null ;
	protected CDataEntity refCommArea = null ;
	protected CDataEntity commAreaLength = null ;
	protected CDataEntity commAreaDataLength = null ;
	
	public void SetCommArea(CDataEntity eCommArea, CDataEntity eCALength, CDataEntity eCADataLength)
	{
		refCommArea = eCommArea ;
		commAreaLength = eCALength ;
		commAreaDataLength = eCADataLength ;
	}
	public boolean ignore()
	{
		return false; 
	}
	public void Clear()
	{
		super.Clear();
		refProgram = null ;
		refCommArea = null ;
		commAreaDataLength = null ;
		commAreaLength = null ;
	}

	/**
	 * @return
	 */
	public CDataEntity GetProgramReference()
	{
		return refProgram ;
	}

	/**
	 * @return
	 */
	public CDataEntity GetCommareaParameter()
	{
		return refCommArea ;
	}

	/**
	 * @return
	 */
	public boolean isReferenceChecked()
	{
		return bChecked ;
	}
}
