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
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSStart;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSStart extends CEntityCICSStart
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 * @param TID
	 */
	public CJavaCICSStart(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity TID)
	{
		super(line, cat, out, TID);
	}
	protected void DoExport()
	{
		String tid ;
		if (isverified)
		{
			tid = transID.GetConstantValue() + ".class" ;
		}
		else
		{
			tid = transID.ExportReference(getLine()) ;
		}
		WriteWord("CESM.start(" + tid + ")");
		if (interval != null)
		{
			WriteWord(".interval(" + interval.ExportReference(getLine()) + ")");
		}
		else if (time != null)
		{
			WriteWord(".time(" + time.ExportReference(getLine()) + ")");
		}
		
		if (termID != null)
		{
			WriteWord(".termID(" + termID.ExportReference(getLine()) + ")");
		}
		
		if (sysID != null)
		{
			WriteWord(".sysID(" + sysID.ExportReference(getLine()) + ")") ;
		}
		
		if (dataFrom!= null)
		{
			String cs = ".dataFrom(" + dataFrom.ExportReference(getLine());
			if (dataLength != null)
			{
				cs += ", " + dataLength.ExportReference(getLine());
			}
			WriteWord(cs + ")");
		}
		WriteWord(".doStart() ;");
		WriteEOL() ;
	}
}
