/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 sept. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityStringConcat;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaStringConcat extends CEntityStringConcat
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaStringConcat(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat);
		setLanguageExporter(out);
	}
	protected void DoExport()
	{
		String cs = "" ;
		boolean isonError = lstChildren.size() > 0 ;
		if (isonError)
		{
			cs = "if (";
		}
		for (ConcatItem item : getConcatItems())
		{
			var eItem = item.getValue();
			var eUntil = item.getDelimiter();
			if (eUntil != null)
			{
				cs += "concatDelimitedBy(" + eItem.ExportReference(getLine()) + ", " + eUntil.ExportReference(getLine()) + ").";
			}
			else
			{
				cs += "concat(" + eItem.ExportReference(getLine()) + ")." ;
			}
			WriteWord(cs);
			cs ="" ;
		}
		cs = "" ;
		if (eStartIndex != null)
		{
			cs += "withPointer(" + eStartIndex.ExportReference(getLine()) + ")." ;
		}
		cs += "into(" + eVariable.ExportReference(getLine()) + ")";
		WriteWord(cs);

		if (isonError)
		{
			WriteWord(".failed()) {");
			WriteEOL() ;
			StartOutputBloc();
			ExportChildren();
			EndOutputBloc();
			WriteLine("}");			
		}
		else
		{
			WriteWord(" ;");
			WriteEOL();
		}
	}

}
