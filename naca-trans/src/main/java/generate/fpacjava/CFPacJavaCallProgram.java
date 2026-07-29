/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntityCallProgram;
import utils.CObjectCatalog;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaCallProgram.java,v 1.2 2007/06/28 06:19:46 u930bm Exp $
 */
public class CFPacJavaCallProgram extends CEntityCallProgram
{

	/**
	 * @param l
	 * @param cat
	 * @param out
	 * @param Reference
	 */
	public CFPacJavaCallProgram(int l, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity Reference)
	{
		super(l, cat, Reference);
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	/**
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		String name = generate.LegacyDataRenderer.renderReference(reference, getLine());
		if (name.startsWith("\""))
		{
			name = name.subSequence(1, name.length()-1) + ".class";	
		}
		if (ischecked)
		{
			generate.LegacyLanguageRenderer.writeWord(this, "call(" +  name + ")") ;
		}
		else
		{
			generate.LegacyLanguageRenderer.writeWord(this, "call(\"" +  name + "\")") ;
		}
		if (parameters.size()>0)
		{
			for (int i = 0; i< parameters.size(); i++)
			{
				CCallParameter p = parameters.get(i) ;
				if (!p.reference.ignore())
				{
					String cs = "" ;
					if (p.methode == CCallParameterMethode.BY_REFERENCE)
					{
						cs = ".using(";
					}
					else if (p.methode == CCallParameterMethode.LENGTH_OF)
					{
						cs = ".usingLengthOf(";
					}
					else if (p.methode == CCallParameterMethode.BY_VALUE)
					{
						cs = ".usingValue(";
					}
					else if (p.methode == CCallParameterMethode.BY_CONTENT)
					{
						cs = ".usingContent(";
					}
					else  
					{
						cs = ".using(";
					}			
					if (p.reference != null)
					{
						cs += generate.LegacyDataRenderer.renderReference(p.reference, getLine());
					}
					else
					{
						cs += "[UNDEFINED]";
					}
					generate.LegacyLanguageRenderer.writeWord(this, cs + ")");
					
				}
			}
		}
		generate.LegacyLanguageRenderer.writeWord(this, ".executeCall() ;");
		generate.LegacyLanguageRenderer.writeEol(this);
	}

}
