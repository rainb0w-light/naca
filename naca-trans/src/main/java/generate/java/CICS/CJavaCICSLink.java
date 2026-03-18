/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.CICS;

import generate.CBaseLanguageExporter;
import semantic.CICS.CEntityCICSLink;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCICSLink extends CEntityCICSLink
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaCICSLink(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		String name = refProgram.ExportReference(getLine());
		if (name.startsWith("\"") && bChecked)
		{
			name = name.subSequence(1, name.length()-1) + ".class";	
		}
		WriteWord("CESM.link(" + name + ")");
		String cs = "" ;
		if (refCommArea != null)
		{
			cs = ".commarea("  + refCommArea.ExportReference(getLine());
			if (commAreaLength != null)
			{
				cs += ", " + commAreaLength.ExportReference(getLine()); 
			}
			else
			{
				cs += ", -1";
			}
			if (commAreaDataLength != null)
			{
				cs += ", " + commAreaDataLength.ExportReference(getLine()); 
			}
			else
			{
				cs += ", -1";
			}
			cs += ")";
		}
		else
		{
			cs = ".go()" ;
		}
		WriteWord(cs + " ;");
		WriteEOL();
	}
}
