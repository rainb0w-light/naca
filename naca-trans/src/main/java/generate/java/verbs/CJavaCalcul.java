/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntityCalcul;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCalcul extends CEntityCalcul
{

	/**
	 * @param cat
	 * @param out
	 */
	public CJavaCalcul(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseSemanticEntity#DoExport()
	 */
	protected void DoExport()
	{
		String csOperation = "compute" ;
		if (roundedDestinations.size() > 0)
		{
			csOperation += "Rounded";
		}
		csOperation += "(";
		String csExpression = expression.Export() ;
		
		boolean isonError = false ;
		if (onErrorBloc != null)
		{
			WriteWord("if (") ;
			isonError = true ;
		}
		boolean isfound = false ;
		for (int i = 0; i< destinations.size(); i++)
		{
			CDataEntity destination = destinations.get(i);
			String line = "" ;
			if (isfound && isonError)
			{
				line = " || " ;
			}
			else
			{
				isfound = true ;
			}
			String csDest = destination.ExportReference(getLine());
			if (csDest.equals("getSQLCode()"))
			{
				line += "resetSQLCode(" + csExpression + ")" ;
			}
			else
			{
				line += csOperation + csExpression + ", " + csDest + ")" ;
			}
			if (isonError)
			{
				line += ".isError()" ;
				WriteWord(line);
			}
			else 
			{
				line += "; " ;
				WriteLine(line) ;
			}
		}
		for (int i = 0; i< roundedDestinations.size(); i++)
		{
			CDataEntity destination = roundedDestinations.get(i);
			String line = "" ;
			if (isfound && isonError)
			{
				line = " || " ;
			}
			else
			{
				isfound = true ;
			}
			String csDest = destination.ExportReference(getLine());
			if (csDest.equals("getSQLCode()"))
			{
				line += "resetSQLCode(" + csExpression + ")" ;
			}
			else
			{
				line += csOperation + csExpression + ", " + csDest + ")" ;
			}
			if (isonError)
			{
				line += ".isError()" ;
				WriteWord(line);
			}
			else 
			{
				line += "; " ;
				WriteLine(line) ;
			}
		}
		if (isonError)
		{
			WriteWord(") {") ;
			WriteEOL() ;
			DoExport(onErrorBloc) ;
			WriteLine("}");
		}
	}

}
