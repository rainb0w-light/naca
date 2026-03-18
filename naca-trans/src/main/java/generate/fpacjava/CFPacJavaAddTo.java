/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.CDataEntity.CDataEntityType;
import semantic.Verbs.CEntityAddTo;
import utils.CObjectCatalog;

public class CFPacJavaAddTo extends CEntityAddTo
{

	public CFPacJavaAddTo(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	@Override
	protected void DoExport()
	{
		if (arrValues.size() == 0)
		{
			return ;
		}
		else if (arrValues.size() == 1)
		{
			String line = "" ;
			CDataEntity value = arrValues.get(0);
			if (value.GetDataType() == CDataEntityType.NUMBER && value.GetConstantValue().equals("1"))
			{
				line = "inc(1, " ;
			}
			else if (value.GetDataType() == CDataEntityType.NUMBER && value.GetConstantValue().equals("-1"))
			{
				line = "dec(1, " ;
			}
			else
			{
				line = "inc(" + value.ExportReference(getLine()) + ", " ;
			}
			for (int i=0; i<arrDest.size(); i++)
			{
				String cs = line ;
				CDataEntity dest = arrDest.get(i);
				if (dest != null)
				{
					if (dest.HasAccessors())
					{
						String add = "inc(" + dest.ExportReference(getLine()) + ", " + value.ExportReference(getLine()) + ")" ;
						cs = dest.ExportWriteAccessorTo(add)  ;
					}
					else
					{
						cs += dest.ExportReference(getLine()) + ") ;";
					}
				}
				else
				{
					cs += "[Undefined]);" ;
				}
				WriteLine(cs) ;
			}
		}
		else
		{
			CDataEntity val1 = arrValues.get(0) ;
			String line ;
			if (val1 == null)
			{
				line = "[Undefined]" ;
			}
			else
			{
				line = val1.ExportReference(getLine()) ;
			}
			
			for (int j=1; j<arrValues.size(); j++)
			{
				CDataEntity val2 = arrValues.get(j);
				if (val2 != null)
				{
					line = "inc(" + line + ", " + val2.ExportReference(getLine()) + ")" ;
				}
				else
				{
					line = "inc(" + line + ", [Undefined])" ;
				}
			}
			for (int i=0; i<arrDest.size(); i++)
			{
				WriteWord(line) ;
	
				String cs = "." ;
				cs += "to(" ;
				CDataEntity dest = arrDest.get(i);
				if (dest != null)
				{
					cs += dest.ExportReference(getLine()) ;
				}
				else
				{
					cs += "[Undefined]" ;
				}
				WriteWord(cs + ") ;") ;
				WriteEOL() ;
			}
		}
	}

}
