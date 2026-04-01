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
		for (int i = 0; i< countAllToken.size(); i++)
		{
			CDataEntity eTok = countAllToken.get(i);
			WriteWord(".countAll(" + eTok.ExportReference(getLine()) + ")");
		}
		for (int i = 0; i< countLeadingToken.size(); i++)
		{
			CDataEntity eTok = countLeadingToken.get(i);
			WriteWord(".countLeading(" + eTok.ExportReference(getLine()) + ")");
		}
		if(countAllToken.isEmpty() && countLeadingToken.isEmpty() &&
				(!countBeforeToken.isEmpty() || !countAfterToken.isEmpty()))
		{
			WriteWord(".forChars()");
		}
		for (int i = 0; i< countAfterToken.size(); i++)
		{
			CDataEntity eTok = countAfterToken.get(i);
			WriteWord(".after(" + eTok.ExportReference(getLine()) + ")");
		}
		for (int i = 0; i< countBeforeToken.size(); i++)
		{
			CDataEntity eTok = countBeforeToken.get(i);
			WriteWord(".before(" + eTok.ExportReference(getLine()) + ")");
		}
		WriteWord(".to(" + toVariable.ExportReference(getLine()) + ")");
		WriteWord(";");
		WriteEOL();		
	}

}
