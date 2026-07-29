/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityMultiply;
import utils.CObjectCatalog;

public class CFPacJavaMultiply extends CEntityMultiply {

	public CFPacJavaMultiply(int line, CObjectCatalog cat, CBaseLanguageExporter out) {
		super(line, cat);
		setLanguageExporter(out);
	}

	@Override
	protected void DoExport() {
		WriteWord("multiply(") ;
		WriteWord(generate.LegacyDataRenderer.renderReference(this.what, getLine())) ;
		WriteWord(", ") ;
		WriteWord(generate.LegacyDataRenderer.renderReference(this.by, getLine())) ;
		WriteWord(").to(") ;
		WriteWord(generate.LegacyDataRenderer.renderReference(this.to, getLine())) ;
		WriteWord(") ;") ;
		WriteEOL() ;
	}

}
