/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntitySortRelease;
import utils.CObjectCatalog;

public class CJavaSortRelease extends CEntitySortRelease
{

	public CJavaSortRelease(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	@Override
	protected void DoExport()
	{
		if (eSortField != null)
		{
			WriteWord("release(" + eSortField.ExportReference(getLine())) ;
		}
		else
		{
			WriteWord("release([Undefined]") ;
		}
		if (eDatReference != null)
		{
			WriteWord(", "+eDatReference.ExportReference(getLine())+") ;") ;
		}
		else
		{
			WriteWord(") ;") ;
		}
		WriteEOL() ;
	}

}
