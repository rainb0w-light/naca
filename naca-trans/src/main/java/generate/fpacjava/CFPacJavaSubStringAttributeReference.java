/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CSubStringAttributReference;
import utils.CObjectCatalog;

public class CFPacJavaSubStringAttributeReference extends
				CSubStringAttributReference
{

	public CFPacJavaSubStringAttributeReference(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat);
		setLanguageExporter(out);
	}

	@Override
	public CDataEntityType GetDataType()
	{
		return reference.GetDataType() ;
	}

	public String ExportReference(int nLine)
	{
		if (reference.HasAccessors())
		{
			String cs = generate.LegacyDataRenderer.renderReference(reference, getLine()) ;
			if (!cs.contains("("))
				cs += "(" ;
			else
				cs += ", " ;
			cs += generate.LegacyDataRenderer.renderReference(start, getLine()) ;
			if (length != null)
			{
				cs += ", " + generate.LegacyDataRenderer.renderReference(length, getLine()) ;
			}
			cs += ")" ;
			return cs ;
		}
		else
		{
			String cs = "buffer(";
			cs += generate.LegacyDataRenderer.renderReference(reference, getLine()) + ", " ;
			cs += generate.LegacyDataRenderer.renderReference(start, getLine()) ;
			if (length != null)
			{
				cs += ", " + generate.LegacyDataRenderer.renderReference(length, getLine()) ;
			}
			cs += ")" ;
			return cs ;
		}
	}

	@Override
	public boolean HasAccessors()
	{
		return false;
	}

	public String ExportWriteAccessorTo(String value)
	{
		String cs = "move("+value+", "+generate.LegacyDataRenderer.renderReference(reference, getLine())+"("+generate.LegacyDataRenderer.renderReference(start, getLine())+", "+generate.LegacyDataRenderer.renderReference(length, getLine())+")) ;" ;
		return cs ;
	}

	@Override
	public boolean isValNeeded()
	{
		return false;
	}

	@Override
	protected void DoExport()
	{
		// unused

	}

}
