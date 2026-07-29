/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CEntityProcedureSection;
import semantic.Verbs.CEntityGoto;
import utils.CObjectCatalog;

public class CFPacJavaGoto extends CEntityGoto
{

	public CFPacJavaGoto(int line, CObjectCatalog cat, CBaseLanguageExporter out, String ref, CEntityProcedureSection sectionContainer)
	{
		super(line, cat, ref, sectionContainer);
		generate.LegacyLanguageRenderer.bind(this, out);
	}
	protected void DoExport()
	{
		generate.LegacyLanguageRenderer.writeLine(this, "return " + reference.getProcedureName() + " ;") ;
	}

}
