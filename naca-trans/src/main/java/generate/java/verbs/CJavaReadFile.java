/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityReadFile;
import utils.CObjectCatalog;

public class CJavaReadFile extends CEntityReadFile
{

	public CJavaReadFile(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	@Override
	protected void DoExport()
	{
		//String cs = eFileDescriptor.ExportReference(getLine()) + ".read" ;
		String cs = "";
		if (eDataInto != null)
			cs = "readInto(" + eFileDescriptor.ExportReference(getLine()) + ", " + eDataInto.ExportReference(getLine()) + ")";
		else
			cs = "read(" + eFileDescriptor.ExportReference(getLine()) + ")";
		//WriteLine(cs) ;
		if (eAtEndBloc != null)
		{
			WriteLine("if (" + cs + ".atEnd()) {");
			DoExport(eAtEndBloc) ;
			if (eNotAtEndBloc != null)
			{
				WriteLine("} else {", eNotAtEndBloc.getLine()-1) ;
				DoExport(eNotAtEndBloc) ;
				WriteLine("}") ;
			}
			else
			{
				WriteLine("}") ;
			}
		}
		else if (eNotAtEndBloc != null)
		{
			WriteLine("if (!" + cs + ".atEnd()) {") ;
			DoExport(eNotAtEndBloc) ;
			WriteLine("}") ;
		}
		else
			WriteLine(cs + " ;");
	}

}
