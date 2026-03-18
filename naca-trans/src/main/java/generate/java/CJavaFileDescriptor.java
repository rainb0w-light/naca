/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java;

import generate.CBaseLanguageExporter;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

public class CJavaFileDescriptor extends CEntityFileDescriptor
{

	public CJavaFileDescriptor(int line, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, name, cat, out);
	}

	@Override
	protected void DoExport()
	{
		String file = fileSelect.GetFileName().ExportReference(getLine()) ;
		String cs = "FileDescriptor " + FormatIdentifier(GetDisplayName()) + " = declare.file("+file+")";
		WriteWord(cs) ;
		if (fileSelect.getFileStatus() != null)
		{
			WriteWord(".status("+fileSelect.getFileStatus().ExportReference(getLine())+")");
		}
//		if (recSizeDependingOn != null)
//		{
//			WriteWord(".lengthDependingOn("+recSizeDependingOn.ExportReference(getLine())+")") ;
//		}
		WriteWord(" ;") ;
		WriteEOL() ;
		ExportChildren() ;
//		CBaseLanguageEntity child = lstChildren.getFirst() ; 
//		DoExport(child);
		
	}

}
