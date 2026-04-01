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
import semantic.CICS.CEntityCICSWrite;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSWrite extends CEntityCICSWrite
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCICSWrite(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	protected void DoExport()
	{
		if (iswritetoDataSet && dataFrom.GetName().equals("CUM-COLL"))
		{
			if (dataFrom.of != null)
			{
				String cs = "Pub2000Routines.writeStatistics(getProgramManager(), " + dataFrom.of.ExportReference(getLine()) + ") ;" ;
				WriteLine(cs);
				return ;
			}
			else
			{
				ASSERT(null);
			}
		}
		else
		{	
			String title = "CESM.write" ;
			if (iswritetoDataSet)
			{
				title += "DataSet(" ;
			}
			else if (iswriteToFile)
			{
				title += "File(" ;
			}
			title += name.ExportReference(getLine()) + ").from(" + dataFrom.ExportReference(getLine()) + ")" ;
			WriteWord(title);
			if (recIDField != null)
			{
				WriteWord(".recIDField(" + recIDField.ExportReference(getLine()) + ")" );
			}
			WriteWord(" ;");
			WriteEOL();
		}
	}
}
