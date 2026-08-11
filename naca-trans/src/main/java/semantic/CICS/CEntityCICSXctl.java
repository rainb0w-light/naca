/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 */
public class CEntityCICSXctl extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSXctl(int line, CObjectCatalog cat)
	{
		super(line, cat);
		// The catalog notification is a production-only side effect; the ST4 render
		// tests instantiate this entity directly with a null catalog (like the READ
		// and CICS RETURN exemplars), so guard it instead of dereferencing unconditionally.
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}

	public void SetProgramName(CDataEntity prgm, boolean bChecked)
	{
		refProgram = prgm ;
		ischecked = bChecked ;
	}

	protected boolean ischecked = false ;
	protected CDataEntity refProgram = null ;
	protected CDataEntity refCommArea = null ;
	protected CDataEntity commAreaLength = null ;
	public void Clear()
	{
		super.Clear();
		refCommArea = null ;
		refProgram = null ;
		commAreaLength = null ;
	}
	//protected CBaseDataEntity commAreaDataLength = null ;

	public void SetCommArea(CDataEntity eCommArea, CDataEntity eCALength)
	{
		refCommArea = eCommArea ;
		commAreaLength = eCALength ;
		//commAreaDataLength = eCADataLength ;
	}
	public boolean ignore()
	{
		return false;
	}
	public boolean hasExplicitGetOut()
	{
		return true ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCICSXctlEntity). They expose the already-resolved semantic
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

	public boolean isChecked()
	{
		return ischecked;
	}

	public String getProgramConstantValue()
	{
		return refProgram == null ? null : refProgram.GetConstantValue();
	}
}
