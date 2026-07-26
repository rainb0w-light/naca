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
public class CEntityCICSLink extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSLink(int line, CObjectCatalog cat)
	{
		super(line, cat);
		// The catalog notifications are production-only side effects; the ST4 render
		// tests instantiate this entity directly with a null catalog (like the READ
		// and CICS XCTL exemplars), so guard them instead of dereferencing unconditionally.
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
			cat.RegisterCICSLink(this);
		}
	}
	
	public void SetProgramName(CDataEntity prgm, boolean bChecked)
	{
		refProgram = prgm ;
		bChecked = bChecked ;
	}
	
	protected boolean ischecked = false ;
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
		return ischecked;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCICSLinkEntity). They expose the already-resolved semantic
	// sub-entities; rendering is done by the template, never here.

	public CDataEntity getProgram()
	{
		return refProgram;
	}

	public CDataEntity getCommArea()
	{
		return refCommArea;
	}

	public CDataEntity getCommLength()
	{
		return commAreaLength;
	}

	public CDataEntity getCommDataLength()
	{
		return commAreaDataLength;
	}

	public boolean isChecked()
	{
		return ischecked;
	}

	public String getProgramConstantValue()
	{
		return refProgram == null ? null : refProgram.GetConstantValue();
	}
}
