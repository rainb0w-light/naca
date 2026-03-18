/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityWriteFile;
import utils.CObjectCatalog;

public class CJavaWriteFile extends CEntityWriteFile
{

	public CJavaWriteFile(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	@Override
	protected void DoExport()
	{
		String cs = "";
		String csFile = "[UnknownReference]" ;
		if (eFileDescriptor != null)
		{
			csFile = eFileDescriptor.ExportReference(getLine()) ;
		}
		if (eDataFrom != null)
			cs = "writeFrom(" + csFile + ", " + eDataFrom.ExportReference(getLine()) + ") ;";
		else if (after != null)
			cs = "writeAfter(" + csFile + ", " + after.ExportReference(getLine()) + ") ;";
		else
			cs = "write(" + csFile + ") ;";
		WriteLine(cs) ;
	}

}
