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
package generate.java.CICS;

import generate.CBaseLanguageExporter;
import semantic.CICS.CEntityCICSAddress;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSAddress extends CEntityCICSAddress
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCICSAddress(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		String cs = "CESM" ;
		if (refCWA != null && !refCWA.ignore())
		{
			cs += ".getAddressOfCWA(" + refCWA.ExportReference(getLine()) + ")";
			WriteWord(cs);
			cs = "" ;
		}
		if (refTCTUA != null && !refTCTUA.ignore())
		{
			cs += ".getAddressOfTCTUA(" + refTCTUA.ExportReference(getLine()) + ")";
			WriteWord(cs);
			cs = "" ;
		}
		if (refTWA != null && !refTWA.ignore())
		{
			cs += ".getAddressOfTWA(" + refTWA.ExportReference(getLine()) + ")";
			WriteWord(cs);
			cs = "" ;
		}
		WriteWord(" ;");
		WriteEOL() ;		
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#ignore()
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
}
