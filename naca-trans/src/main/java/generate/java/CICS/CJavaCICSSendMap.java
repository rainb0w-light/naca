/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.CICS;

import generate.CBaseLanguageExporter;
import semantic.CICS.CEntityCICSSendMap;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSSendMap extends CEntityCICSSendMap
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCICSSendMap(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	protected void DoExport()
	{
		WriteWord("CESM.sendMap(" + mapName.ExportReference(getLine()) + ")");
		if (mapSetName != null)
		{
			WriteWord(".mapSet(" + mapSetName.ExportReference(getLine()) + ")");
		}
		if (dataFrom != null)
		{
			String cs = "";
			if (bDataOnly)
			{
				cs = ".dataOnlyFrom(";
			}
			else
			{
				cs = ".dataFrom(";
			}
			cs += dataFrom.ExportReference(getLine());
			if (dataLength != null)
			{
				cs += ", " + dataLength.ExportReference(getLine()) ;
			}
			cs += ")" ;
			WriteWord(cs);
		}
		if (bCursor)
		{
			String cs = ".cursor(";
			if (cursorValue != null)
			{
				cs += cursorValue.ExportReference(getLine());
			}
			WriteWord(cs+")");
		}
		if (bAccum)
		{
			WriteWord(".accum()");
		}
		if (bAlarm)
		{
			WriteWord(".alarm()");
		}
		if (bErase)
		{
			WriteWord(".erase()");
		}
		if (bFreeKB)
		{
			WriteWord(".freeKB()");
		}
		if (bPaging)
		{
			WriteWord(".paging()");
		}
		if (bWait)
		{
			WriteWord(".wait()");
		}
		WriteWord(" ;");
		WriteEOL() ;
	}
}
