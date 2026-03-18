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
import semantic.CICS.CEntityCICSReadQ;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSReadQ extends CEntityCICSReadQ
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 * @param bPersistant
	 */
	public CJavaCICSReadQ(int line, CObjectCatalog cat, CBaseLanguageExporter out, boolean bPersistant)
	{
		super(line, cat, out, bPersistant);
	}

	protected void DoExport()
	{
		String title = "CESM.read" ;
//		if (bReadNext)
//		{
//			title += "Next" ;
//		}
		if (bPesistant)
		{
			title += "TransiantQueue(" ; 
		}
		else
		{
			title += "TempQueue(" ;
		}
		title += queueName.ExportReference(getLine()) + ")";
		WriteWord(title) ;
		if (dataRef != null)
		{
			String cs = "" ;
			if (item != null)
			{
				cs = ".itemInto(" + item.ExportReference(getLine()) +", " ;
			}
			else
			{
				cs = ".nextInto(";
			}
			cs += dataRef.ExportReference(getLine()) ;
			if (dataLength != null)
			{
				cs += ", " + dataLength.ExportReference(getLine());
			}
			WriteWord(cs + ")") ;
		}
		if (numItem != null)
		{
			WriteWord(".numItem(" + numItem.ExportReference(getLine()) + ")");
		}
		WriteWord(" ;");
		WriteEOL() ;
	}

}
