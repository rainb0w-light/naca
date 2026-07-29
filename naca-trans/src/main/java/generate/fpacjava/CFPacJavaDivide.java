/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityDivide;
import utils.CObjectCatalog;

public class CFPacJavaDivide extends CEntityDivide {

	public CFPacJavaDivide(int line, CObjectCatalog cat, CBaseLanguageExporter out) {
		super(line, cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}
	protected void DoExport() {
		generate.LegacyLanguageRenderer.writeWord(this, "divide(") ;
		generate.LegacyLanguageRenderer.writeWord(this, generate.LegacyDataRenderer.renderReference(this.what, getLine())) ;
		generate.LegacyLanguageRenderer.writeWord(this, ", ") ;
		generate.LegacyLanguageRenderer.writeWord(this, generate.LegacyDataRenderer.renderReference(this.by, getLine())) ;
		generate.LegacyLanguageRenderer.writeWord(this, ").to(") ;
		generate.LegacyLanguageRenderer.writeWord(this, generate.LegacyDataRenderer.renderReference(this.result, getLine())) ;
		
		generate.LegacyLanguageRenderer.writeWord(this, ") ;") ;
		generate.LegacyLanguageRenderer.writeEol(this) ;
	}

}
