/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CEntityUnknownReference;
import utils.CObjectCatalog;

public class CFPacJavaUnknownReference extends CEntityUnknownReference
{
	public CFPacJavaUnknownReference(int nLine, String csName, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(nLine, csName, cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	public String ExportReference(int nLine)
	{
		return "[UNDEFINED]";
	}

	@Override
	public boolean HasAccessors()
	{
		return false;
	}

	public String ExportWriteAccessorTo(String value)
	{
		return null;
	}

	@Override
	public boolean isValNeeded()
	{
		return false;
	}
	protected void DoExport()
	{
		// nothing

	}

}
