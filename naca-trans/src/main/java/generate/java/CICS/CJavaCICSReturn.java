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
import semantic.CICS.CEntityCICSReturn;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSReturn extends CEntityCICSReturn
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCICSReturn(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	protected void DoExport()
	{
		String cs = "CESM.returnTrans(";
		if (transID != null)
		{
			String tid ;
			if (ischecked)
			{
				tid = transID.GetConstantValue() + ".class" ;
			}
			else
			{
				tid = transID.ExportReference(getLine()) ;
			}
			cs += tid ;
			if (commArea != null)
			{
				cs += ", " + commArea.ExportReference(getLine());
				if (commLenght != null)
				{
					cs += ", " + commLenght.ExportReference(getLine());
				}
			}
		}
		cs += (") ;");
		WriteLine(cs) ;
	}
}
