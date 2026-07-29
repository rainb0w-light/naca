/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity.CDataEntityType;
import semantic.Verbs.CEntityAssignWithAccessor;
import utils.CObjectCatalog;

public class CFPacJavaAssignWithAccessor extends CEntityAssignWithAccessor
{

	public CFPacJavaAssignWithAccessor(int line, CObjectCatalog cat,
					CBaseLanguageExporter out)
	{
		super(line, cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}
	protected void DoExport()
	{
		String val = "" ;
		if (value != null)
		{
			val = generate.LegacyDataRenderer.renderReference(value, getLine()) ;
		}
		if (value.GetDataType() == CDataEntityType.VAR && reference.GetDataType() == CDataEntityType.NUMERIC_VAR)
		{
			val += ".getInt()" ;
		}
		String out = generate.LegacyDataRenderer.renderWriteAccessor(reference, val) ;
		if (out == null)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "");
		}
		else
		{
			generate.LegacyLanguageRenderer.writeLine(this, out);
		}
	}

}
