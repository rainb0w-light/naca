/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.CICS;

import generate.CBaseLanguageExporter;
import semantic.CICS.CEntityCICSHandleCondition;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSHandleCondition extends CEntityCICSHandleCondition
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCICSHandleCondition(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	protected void DoExport()
	{
		WriteWord("CESM");
		for (int i=0;i<arrHandledConditions.size();i++)
		{
			String cond = arrHandledConditions.get(i);
			String label = arrHandledConditionLabels.get(i);
			String cs = ".handleCondition(\"" + cond + "\", " + FormatIdentifier(label) + ")" ;
			WriteWord(cs);
		}
		for (int i=0;i<arrUnhandledConditions.size();i++)
		{
			String cond = arrUnhandledConditions.get(i);
			String cs = ".unhandleCondition(\"" + cond + "\")" ;
			WriteWord(cs);
		}
		WriteWord(" ;");
		WriteEOL() ;		
	}
}
