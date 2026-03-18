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
		super(l, cat, out);
	}

	@Override
	public CDataEntityType GetDataType()
	{
		return reference.GetDataType() ;
	}

	@Override
	public String ExportReference(int nLine)
	{
		if (reference.HasAccessors())
		{
			String cs = reference.ExportReference(getLine()) ;
			if (!cs.contains("("))
				cs += "(" ;
			else
				cs += ", " ;
			cs += start.ExportReference(getLine()) ;
			if (length != null)
			{
				cs += ", " + length.ExportReference(getLine()) ;
			}
			cs += ")" ;
			return cs ;
		}
		else
		{
			String cs = "buffer(";
			cs += reference.ExportReference(getLine()) + ", " ;
			cs += start.ExportReference(getLine()) ;
			if (length != null)
			{
				cs += ", " + length.ExportReference(getLine()) ;
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

	@Override
	public String ExportWriteAccessorTo(String value)
	{
		String cs = "move("+value+", "+reference.ExportReference(getLine())+"("+start.ExportReference(getLine())+", "+length.ExportReference(getLine())+")) ;" ;
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
