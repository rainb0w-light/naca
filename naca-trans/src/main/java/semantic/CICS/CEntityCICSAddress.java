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
public class CEntityCICSAddress extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSAddress(int line, CObjectCatalog cat)
	{
		super(line, cat);
		// The catalog notification is a production-only side effect; the ST4 render
		// tests instantiate this entity directly with a null catalog (like the READ
		// and CICS RETURN/XCTL/LINK/ABEND exemplars), so guard it instead of
		// dereferencing unconditionally.
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}
	public void SetRefForCWA(CDataEntity e)
	{
		refCWA = e ;
	}
	public void SetRefForTCTUA(CDataEntity e)
	{
		refTCTUA = e ;
	} 
	public void SetRefForTWA(CDataEntity e)
	{
		refTWA = e ;
	}
	
	protected CDataEntity refCWA = null;
	protected CDataEntity refTCTUA = null;
	protected CDataEntity refTWA = null;
	public void Clear()
	{
		super.Clear();
		refCWA = null ;
		refTCTUA = null ;
		refTWA = null ;
	}

	/**
	 * The ADDRESS verb carries no executable children of its own; it is ignored
	 * (renders nothing) when none of its CWA/TCTUA/TWA references is present and
	 * active. This preserves the retired backend's filtering behavior without
	 * coupling the semantic model to that target-specific implementation.
	 */
	public boolean ignore()
	{
		boolean ignore = true ;
		if (refCWA != null)
		{
			ignore &= refCWA.ignore() ;
		}
		if (refTCTUA != null)
		{
			ignore &= refTCTUA.ignore() ;
		}
		if (refTWA != null)
		{
			ignore &= refTWA.ignore() ;
		}
		return ignore ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCICSAddressEntity). They expose the already-resolved semantic
	// sub-entities; rendering is done by the template, never here. Each getter
	// returns null when its reference is absent or ignored, mirroring the
	// retired backend's per-reference guard, so the template selects exactly the
	// active references.

	public CDataEntity getCwa()
	{
		return (refCWA != null && !refCWA.ignore()) ? refCWA : null;
	}

	public CDataEntity getTctua()
	{
		return (refTCTUA != null && !refTCTUA.ignore()) ? refTCTUA : null;
	}

	public CDataEntity getTwa()
	{
		return (refTWA != null && !refTWA.ignore()) ? refTWA : null;
	}
}
