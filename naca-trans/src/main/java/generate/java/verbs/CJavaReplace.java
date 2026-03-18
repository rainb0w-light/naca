/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityReplace;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaReplace extends CEntityReplace
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaReplace(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		String begin = "inspectReplacing("+ variable.ExportReference(getLine()) + ")";
		//WriteWord(cs);
		for (int i=0; i<arrItemsToReplace.size(); i++)
		{
			WriteWord(begin) ;
			String cs = "" ;
			CReplaceItem item = arrItemsToReplace.get(i);
			if (item.mode == CReplaceMode.ALL)
			{
				cs = ".all" ;
			}
			else if (item.mode == CReplaceMode.FIRST)
			{
				cs = ".first" ;
			}
			else if (item.mode == CReplaceMode.LEADING)
			{
				cs = ".leading" ;
			}
			
			if (item.replaceDataType == CReplaceType.SPACES)
			{
				cs += "Spaces(" ;
			}
			else if (item.replaceDataType == CReplaceType.ZEROS)
			{
				cs += "Zeros(" ;
			}
			else if (item.replaceDataType == CReplaceType.LOW_VALUES)
			{
				cs += "LowValues(" ;
			}
			else if (item.replaceDataType == CReplaceType.HIGH_VALUES)
			{
				cs += "HighValues(" ;
			}
			else if (item.replaceDataType == CReplaceType.CUSTOM)
			{
				cs += "(" ;
			}
			if (item.replaceData != null)
			{
				cs += item.replaceData.ExportReference(getLine()) ;
			}
			WriteWord(cs + ")");
			
			cs = ".by" ;
			if (item.byDataType == CReplaceType.SPACES)
			{
				cs += "Spaces(" ;
			}
			else if (item.byDataType == CReplaceType.ZEROS)
			{
				cs += "Zero(" ;
			}
			else if (item.byDataType == CReplaceType.LOW_VALUES)
			{
				cs += "LowValues(" ;
			}
			else if (item.byDataType == CReplaceType.HIGH_VALUES)
			{
				cs += "HighValues(" ;
			}
			else if (item.byDataType == CReplaceType.CUSTOM)
			{
				cs += "(" ;
			}
			if (item.byData != null)
			{
				cs += item.byData.ExportReference(getLine());
			}
			WriteWord(cs + ")");
			WriteWord(" ;");
			WriteEOL();
		}
	}
}
