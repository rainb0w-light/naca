/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CEntityClass;
import utils.CObjectCatalog;

public class CFPacJavaClass extends CEntityClass
{

	public CFPacJavaClass(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}
	protected void DoExport()
	{
		String name = GetName().replace('-', '_').toUpperCase();
		generate.LegacyLanguageRenderer.writeEol(this) ;

		generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.fpacPrgEnv.* ;", 0) ;
//		generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.batchPrgEnv.* ;", 0) ;
		generate.LegacyLanguageRenderer.writeEol(this) ;
		
		String line = "public class " + name + " extends FPacProgram" ;
		generate.LegacyLanguageRenderer.writeLine(this, line);
		generate.LegacyLanguageRenderer.writeLine(this, "{") ;
		generate.LegacyLanguageRenderer.startBlock(this);

//		generate.LegacyLanguageRenderer.writeLine(this, "public "+name+"(BatchProgramManagerFactory batchProgramManagerFactory) {");
//		generate.LegacyLanguageRenderer.startBlock(this) ;
//		generate.LegacyLanguageRenderer.writeLine(this, "super(batchProgramManagerFactory);");
//		generate.LegacyLanguageRenderer.endBlock(this) ;
//		generate.LegacyLanguageRenderer.writeLine(this, "}") ;
		
		generate.LegacyLanguageRenderer.exportChildren(this, false) ;

		generate.LegacyLanguageRenderer.endBlock(this);
		generate.LegacyLanguageRenderer.writeLine(this, "}") ;
		
	}

}
