/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntitySortReturn;
import utils.CObjectCatalog;

public class CJavaSortReturn extends CEntitySortReturn
{

	public CJavaSortReturn(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	@Override
	protected void DoExport()
	{
		String cs = "" ;
		if (eFileDesc != null)
		{
			cs = "returnSort("+eFileDesc.ExportReference(getLine()) ;
		}
		else
		{
			cs = "returnSort([Undefined]" ;
		}
		if (eDataInto != null)
		{
			cs += ", "+eDataInto.ExportReference(getLine())+")" ;
		}
		else
		{
			cs += ")" ;
		}
		if (blocAtEnd != null)
		{
			WriteLine("if("+cs+".atEnd()) {") ;
			StartOutputBloc() ;
			DoExport(blocAtEnd) ;
			EndOutputBloc() ;
			WriteLine("}") ;
			
			if (blocNotAtEnd != null)
			{
				WriteLine("else {") ;
				StartOutputBloc() ;
				DoExport(blocNotAtEnd) ;
				EndOutputBloc() ;
				WriteLine("}") ;
			}
		}
		else if (blocNotAtEnd != null)
		{
			WriteLine("if(!"+cs+".atEnd()) {") ;
			StartOutputBloc() ;
			DoExport(blocNotAtEnd) ;
			EndOutputBloc() ;
			WriteLine("}") ;
		}
		else
		{
			WriteLine(cs + " ;") ;
		}
	}

}
