/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CBaseLanguageEntity;
import semantic.CEntityCondition;
import utils.CObjectCatalog;

public class CFPacJavaCondition extends CEntityCondition
{

	public CFPacJavaCondition(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat, out);
	}

	@Override
	protected void DoExport()
	{
		if (condition == null)
		{
			return ;
		}

		if (!isalternativeCondition)
			WriteWord("if (");
		else
			WriteWord("else if (");
		String cs = condition.Export() ;
		cs += ") {" ;
		WriteWord(cs) ;
		WriteEOL() ;
		DoExport(thenBloc) ;
		int n = thenBloc.GetEndLine() ;
		if (n == 0 && elseBloc != null)
		{
			n = elseBloc.getLine() -1 ;
		}
		if (alternativeConditions != null)
		{
			if (!alternativeConditions.isEmpty())
			{
				n = lstChildren.getFirst().getLine() -1 ;
			}
			WriteLine("}", n) ;
			for (CBaseLanguageEntity e : alternativeConditions)
			{
				DoExport(e) ;
			}
		}
		else
		{
			WriteLine("}", n) ;
		}
		if (elseBloc != null)
		{
			WriteLine("else {", elseBloc.getLine()) ;
			DoExport(elseBloc) ;
			WriteLine("}", elseBloc.GetEndLine()) ;
		}	}

}
