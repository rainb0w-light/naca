/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntitySort;
import utils.CObjectCatalog;

public class CJavaSort extends CEntitySort
{

	public CJavaSort(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	@Override
	protected void DoExport()
	{
		String cs = fileDescriptor.ExportReference(getLine()) ;
		WriteWord("sort("+cs+")") ;
		
		for (int i=0; i<arrSortKey.size(); i++) 
		{
			CEntitySortKey key = arrSortKey.get(i) ;
			if (key.bAscending)
			{
				cs = ".ascKey(" ;
			}
			else
			{
				cs = ".descKey(" ;
			}
			if (key.key != null)
			{
				cs += key.key.ExportReference(getLine()) ;
			}
			else
			{
				cs += "[Undefined]" ;
			}
			cs += ")" ;
			WriteWord(cs) ;
		}
		
		if (fdInputFile != null)
		{
			WriteWord(".using("+fdInputFile.ExportReference(getLine())+")") ;
		}
		else if (pInputProcedure != null)
		{
			WriteWord(".usingInput("+pInputProcedure.ExportReference(getLine())+")") ;
		}
		else if (csInputProcedureName != null)
		{
			pInputProcedure = programCatalog.GetProcedure(csInputProcedureName, "") ;
			if (pInputProcedure != null)
			{
				WriteWord(".usingInput("+pInputProcedure.ExportReference(getLine())+")") ;
			}
			else
			{
				WriteWord(".usingInput(["+csInputProcedureName+"])") ;
			}
		}

		if (fdOutputFile != null)
		{
			WriteWord(".giving("+fdOutputFile.ExportReference(getLine())+")") ;
		}
		else if (pOutputProcedure != null)
		{
			WriteWord(".usingOutput("+pOutputProcedure.ExportReference(getLine())+")") ;
		}
		else if (csOutputProcedureName != null)
		{
			pOutputProcedure = programCatalog.GetProcedure(csOutputProcedureName, "") ;
			if (pOutputProcedure != null)
			{
				WriteWord(".usingOutput("+pOutputProcedure.ExportReference(getLine())+")") ;
			}
			else
			{
				WriteWord(".usingOutput(["+csOutputProcedureName+"])") ;
			}
		}

		WriteWord(".exec() ;") ;
		WriteEOL() ;
	}

}
