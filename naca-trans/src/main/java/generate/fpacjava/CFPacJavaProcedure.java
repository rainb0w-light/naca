/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureSection;
import utils.CObjectCatalog;

public class CFPacJavaProcedure extends CEntityProcedure
{

	public CFPacJavaProcedure(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out, CEntityProcedureSection section)
	{
		super(l, name, cat, section);
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	public String ExportReference(int nLine)
	{
		return generate.LegacyLanguageRenderer.formatIdentifier(this, GetName());
	}
	protected void DoExport()
	{
		String cs = "protected int " + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName()) + "() {" ;
		generate.LegacyLanguageRenderer.writeLine(this, cs) ;
		generate.LegacyLanguageRenderer.startBlock(this) ;
		
		generate.LegacyLanguageRenderer.exportChildren(this, false) ;
		if (!this.hasExplicitGetOut())
		{
			generate.LegacyLanguageRenderer.writeLine(this, "return NEXT ;") ;
		}
		
		generate.LegacyLanguageRenderer.endBlock(this) ;
		generate.LegacyLanguageRenderer.writeLine(this, "}", nEndLine) ;
	}

}
