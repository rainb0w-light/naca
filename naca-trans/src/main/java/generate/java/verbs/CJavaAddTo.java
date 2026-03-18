/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 9 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.CDataEntity.CDataEntityType;
import semantic.Verbs.CEntityAddTo;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaAddTo extends CEntityAddTo
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaAddTo(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
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
				line = "inc(" ;
			}
			else if (value.GetDataType() == CDataEntityType.NUMBER && value.GetConstantValue().equals("-1"))
			{
				line = "dec(" ;
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
						String add = "add(" + dest.ExportReference(getLine()) + ", " + value.ExportReference(getLine()) + ")" ;
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
					line = "add(" + line + ", " + val2.ExportReference(getLine()) + ")" ;
				}
				else
				{
					line = "add(" + line + ", [Undefined])" ;
				}
			}
			for (int i=0; i<arrDest.size(); i++)
			{
				WriteWord(line) ;
	
				String cs = "." ;
				if (bRounded)
				{
					cs += "toRounded(" ;
				}
				else
				{
					cs += "to(" ;
				}
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
