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
		super(line, cat, out);
	}

	@Override
	protected void DoExport()
	{
		String val = "" ;
		if (value != null)
		{
			val = value.ExportReference(getLine()) ;
		}
		if (value.GetDataType() == CDataEntityType.VAR && reference.GetDataType() == CDataEntityType.NUMERIC_VAR)
		{
			val += ".getInt()" ;
		}
		String out = reference.ExportWriteAccessorTo(val) ;
		if (out == null)
		{
			WriteLine("");
		}
		else
		{
			WriteLine(out);
		}
	}

}
