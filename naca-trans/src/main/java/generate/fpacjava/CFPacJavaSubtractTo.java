/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntitySubtractTo;
import utils.CObjectCatalog;

public class CFPacJavaSubtractTo extends CEntitySubtractTo
{

	public CFPacJavaSubtractTo(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat);
		setLanguageExporter(out);
	}

	@Override
	protected void DoExport() {
		WriteWord("subtract(") ;
		WriteWord(generate.LegacyDataRenderer.renderReference(this.variable, getLine())) ;
		for(CDataEntity value : values)
		{
			WriteWord(", ") ;
			WriteWord(generate.LegacyDataRenderer.renderReference(value, getLine())) ;
		}
		WriteWord(")") ;
		for(CDataEntity value : this.destination)
		{
			WriteWord(".to(");
			WriteWord(generate.LegacyDataRenderer.renderReference(value, getLine())) ;
			WriteWord(")");
		}
		WriteWord(" ;") ;
		WriteEOL() ;
	}
}
