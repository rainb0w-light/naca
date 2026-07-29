/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

public class CFPacJavaFileDescriptor extends CEntityFileDescriptor
{

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#ignore()
	 */
	@Override
	public boolean ignore()
	{
		return false ;
	}

	public CFPacJavaFileDescriptor(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}
	protected void DoExport()
	{
		String cs = "FPacFileDescriptor " + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName()) ;
		cs += " = declare.fpacFile(\""+GetName().toUpperCase()+"\")" ;
		generate.LegacyLanguageRenderer.writeWord(this, cs) ;
		if (eOutputBufferInitialValue != null)
		{
			generate.LegacyLanguageRenderer.writeWord(this, ".fillOutputBuffer("+generate.LegacyDataRenderer.renderReference(eOutputBufferInitialValue, getLine())+")") ;
		}
		generate.LegacyLanguageRenderer.writeWord(this, ".file() ;") ;
		generate.LegacyLanguageRenderer.writeEol(this) ;

	}
	
	

}
