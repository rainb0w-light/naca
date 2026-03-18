/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntitySubtractTo;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSubtractTo extends CEntitySubtractTo
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaSubtractTo(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	protected void DoExport()
	{
		if (onErrorBloc != null)
		{
			WriteWord("if (") ;
		}
		if (!destination.isEmpty())
		{
			String cs = "subtract(" + variable.ExportReference(getLine()) ;
			for(CDataEntity value : values)
			{
				cs += ", " + value.ExportReference(getLine()) ;
			}
			cs += ")" ;
			WriteWord(cs);
			for(CDataEntity value : destination)
			{
				WriteWord(".to(" + value.ExportReference(getLine()) + ")") ;
			}
		} 
		else if(values.size() == 1)
		{
			CDataEntity value = values.get(0);
			if (value.GetConstantValue().equals("1"))
			{
				String cs = "dec(" + variable.ExportReference(getLine()) + ")" ;
				WriteLine(cs) ;
			}
			else  if (value.GetConstantValue().equals("-1"))
			{
				String cs = "inc(" + variable.ExportReference(getLine()) + ")" ;
				WriteLine(cs) ;
			}
			else
			{
				String cs = "dec(" + value.ExportReference(getLine()) + ", " + variable.ExportReference(getLine()) +")" ;
				WriteWord(cs) ;
			}
		} 
		else
		{
			String cs = "dec(" ;
			for(CDataEntity value : values)
			{
				cs += value.ExportReference(getLine()) + ", " ;
			}
			cs += variable.ExportReference(getLine()) +")" ;
			WriteWord(cs) ;
		}
		if (onErrorBloc != null)
		{
			WriteWord(".isError()") ;
			WriteWord(") {") ;
			WriteEOL() ;
			DoExport(onErrorBloc) ;
			WriteLine("}");
		}
		else
		{
			WriteWord(" ;");
			WriteEOL() ;
		}
	}
}
