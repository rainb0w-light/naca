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
package generate.java.CICS;

import generate.CBaseLanguageExporter;
import semantic.CICS.CEntityCICSDeleteQ;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSDeleteQ extends CEntityCICSDeleteQ
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 * @param bPersistent
	 */
	public CJavaCICSDeleteQ(int line, CObjectCatalog cat, CBaseLanguageExporter out, boolean bPersistent)
	{
		super(line, cat, out, bPersistent);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		String title = "CESM.delete" ; 
		if (bPersistent)
		{
			title += "TransiantQueue(" ;
		}
		else
		{
			title += "TempQueue(" ;
		}
		title += name.ExportReference(getLine());
		if (sysID != null)
		{
			title += ").sysID(" + sysID.ExportReference(getLine());
		}
		title += (") ;");
		WriteLine(title) ;
	}

}
