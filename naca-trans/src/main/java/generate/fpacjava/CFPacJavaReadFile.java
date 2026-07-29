/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityReadFile;
import utils.CObjectCatalog;

public class CFPacJavaReadFile extends CEntityReadFile
{

	public CFPacJavaReadFile(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}
	protected void DoExport()
	{
		String cs = eFileDescriptor.getFormattedName() + ".read()" ;
		if (eAtEndBloc != null)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "if ("+cs+".atEnd()) {") ;
			generate.LegacyLanguageRenderer.invokeExport(eAtEndBloc) ;
			generate.LegacyLanguageRenderer.writeLine(this, "}") ;
		}
		else
		{
			generate.LegacyLanguageRenderer.writeLine(this, cs + " ;") ;
		}
	}

}
