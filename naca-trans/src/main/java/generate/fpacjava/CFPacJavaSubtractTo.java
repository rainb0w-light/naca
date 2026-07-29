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
		generate.LegacyLanguageRenderer.bind(this, out);
	}
	protected void DoExport() {
		generate.LegacyLanguageRenderer.writeWord(this, "subtract(") ;
		generate.LegacyLanguageRenderer.writeWord(this, generate.LegacyDataRenderer.renderReference(this.variable, getLine())) ;
		for(CDataEntity value : values)
		{
			generate.LegacyLanguageRenderer.writeWord(this, ", ") ;
			generate.LegacyLanguageRenderer.writeWord(this, generate.LegacyDataRenderer.renderReference(value, getLine())) ;
		}
		generate.LegacyLanguageRenderer.writeWord(this, ")") ;
		for(CDataEntity value : this.destination)
		{
			generate.LegacyLanguageRenderer.writeWord(this, ".to(");
			generate.LegacyLanguageRenderer.writeWord(this, generate.LegacyDataRenderer.renderReference(value, getLine())) ;
			generate.LegacyLanguageRenderer.writeWord(this, ")");
		}
		generate.LegacyLanguageRenderer.writeWord(this, " ;") ;
		generate.LegacyLanguageRenderer.writeEol(this) ;
	}
}
