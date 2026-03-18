/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntityCount;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCount extends CEntityCount
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCount(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	protected void DoExport()
	{
		WriteWord("inspectTallying(" + variable.ExportReference(getLine()) + ")");
		for (int i =0; i<arrCountAllToken.size();i++)
		{
			CDataEntity eTok = arrCountAllToken.get(i);
			WriteWord(".countAll(" + eTok.ExportReference(getLine()) + ")");
		}
		for (int i =0; i<arrCountLeadingToken.size();i++)
		{
			CDataEntity eTok = arrCountLeadingToken.get(i);
			WriteWord(".countLeading(" + eTok.ExportReference(getLine()) + ")");
		}
		if(arrCountAllToken.isEmpty() && arrCountLeadingToken.isEmpty() && 
				(!arrCountBeforeToken.isEmpty() || !arrCountAfterToken.isEmpty()))
		{
			WriteWord(".forChars()");
		}
		for (int i =0; i<arrCountAfterToken.size();i++)
		{
			CDataEntity eTok = arrCountAfterToken.get(i);
			WriteWord(".after(" + eTok.ExportReference(getLine()) + ")");
		}
		for (int i =0; i<arrCountBeforeToken.size();i++)
		{
			CDataEntity eTok = arrCountBeforeToken.get(i);
			WriteWord(".before(" + eTok.ExportReference(getLine()) + ")");
		}
		WriteWord(".to(" + toVariable.ExportReference(getLine()) + ")");
		WriteWord(";");
		WriteEOL();		
	}

}
