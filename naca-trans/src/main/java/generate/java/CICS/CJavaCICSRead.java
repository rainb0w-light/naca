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
import semantic.CICS.CEntityCICSRead;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSRead extends CEntityCICSRead
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCICSRead(int line, CObjectCatalog cat, CBaseLanguageExporter out, CEntityCICSReadMode mode)
	{
		super(line, cat, out, mode);
	}

	protected void DoExport()
	{
		if (bReadtoDataSet && dataInto.GetName().equals("PLAU-ZONE"))
		{
			if (dataInto.of != null)
			{
				String cs = "Pub2000Routines.readCode(getProgramManager(), " + dataInto.of.ExportReference(getLine()) + ") ;" ;
				WriteLine(cs);
				return ;
			}
			else
			{
				ASSERT(null);
			}
		}
		else if (bReadtoDataSet && dataInto.GetName().equals("PLAU-ZONE-ASP"))
		{
			if (dataInto.of != null)
			{
				String cs = "Pub2000Routines.readCodeMedia(getProgramManager(), " + dataInto.of.ExportReference(getLine()) + ") ;" ;
				WriteLine(cs);
				return ;
			}
			else
			{
				ASSERT(null);
			}
		}
		else if (bReadtoDataSet && dataInto.GetName().equals("MSG-ZONE"))
		{
			if (dataInto.of != null)
			{
				String cs = "Pub2000Routines.readMessage(getProgramManager(), " + dataInto.of.ExportReference(getLine()) + ") ;" ;
				WriteLine(cs);
				return ;
			}
			else
			{
				ASSERT(null);
			}
		}
		else if ((mode == CEntityCICSReadMode.PREVIOUS || mode == CEntityCICSReadMode.NEXT) &&
				dataInto.GetName().equals("CURS-ZONE"))
		{
			if (dataInto.of != null && recIDField != null)
			{
				String cs = "Pub2000Routines.readFieldInMap";
				if (mode == CEntityCICSReadMode.PREVIOUS)
				{	
					cs += "Previous";
				}
				else
				{
					cs += "Next";
				}
				cs += "(getProgramManager(), " + dataInto.of.ExportReference(getLine()) + ", " +	recIDField.ExportReference(getLine()) + ");";
				WriteLine(cs);
				return ;
			}
			else
			{
				ASSERT(null);
			}
		}
		else if (bReadtoDataSet && dataInto.GetName().equals("CURS-ZONE"))
		{
			if (dataInto.of != null && recIDField != null)
			{
				String cs = "Pub2000Routines.readFieldInMap(getProgramManager(), " +
							dataInto.of.ExportReference(getLine()) + ", " +
							recIDField.ExportReference(getLine()) + ");";
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
			String title = "" ;
			if (mode == CEntityCICSReadMode.NORMAL)
			{
				title = "CESM.read" ;
			}
			else if (mode == CEntityCICSReadMode.PREVIOUS)
			{
				title = "CESM.readPrevious" ;
			}
			else if (mode == CEntityCICSReadMode.NEXT)
			{
				title = "CESM.readNext" ;
			}
			if (bReadtoDataSet)
			{
				title += "DataSet(" ;
			}
			else if (bReadToFile)
			{
				title += "File(" ;
			}
			title += name.ExportReference(getLine()) + ")" ;
			WriteWord(title) ;
			WriteWord(".into(" + dataInto.ExportReference(getLine()) + ")") ;
			if (recIDField != null)
			{
				WriteWord(".recIDField(" + recIDField.ExportReference(getLine()) + ")");
			}
			if (keyLength != null)
			{
				WriteWord(".keyLength(" + keyLength.ExportReference(getLine()) + ")");
			}
			if (bEqual)
			{
				WriteWord(".equal()") ;
			}
			WriteWord(" ;");
			WriteEOL();		
		}
	}
}
