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
import semantic.CICS.CEntityCICSWriteQ;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSWriteQ extends CEntityCICSWriteQ
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 * @param bPersistant
	 */
	public CJavaCICSWriteQ(int line,CObjectCatalog cat, CBaseLanguageExporter out, boolean bPersistant)
	{
		super(line, cat, out, bPersistant);
	}

	protected void DoExport()
	{
		String cs = "CESM.write" ;
		if (ispersistant)
		{
			cs += "TransiantQueue(" ; 
		}
		else
		{
			cs += "TempQueue(" ;
		}
		//WriteWord(title) ;
		cs += queueName.ExportReference(getLine());
		if (bRewrite && item != null)
		{
			cs += ", " + item.ExportReference(getLine()) ;
		}
		cs += ")" ;
		WriteWord(cs) ;
		if (dataRef != null)
		{
			cs = ".from(" + dataRef.ExportReference(getLine());
			if (dataLength != null)
			{
				cs += ", " + dataLength.ExportReference(getLine()) ;
			}
			cs += ")" ;
			WriteWord(cs);
			if (item != null && !bRewrite)
			{
				WriteWord(".item(" + item.ExportReference(getLine()) + ")");
			}
		}
//		if (numItem != null)
//		{
//			WriteWord(".writeNumItem(" + numItem.ExportReference(getLine()) + ")");
//		}
//		if (bAuxiliary)
//		{
//			WriteWord(".auxiliary()");
//		}
//		if (bMain)
//		{
//			WriteWord(".main()");
//		}
//		if (bRewrite)
//		{
//			WriteWord(".rewrite()");
//		}
		WriteWord(" ;");
		WriteEOL() ;
	}

}
