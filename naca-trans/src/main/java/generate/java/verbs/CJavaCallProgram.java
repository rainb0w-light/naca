/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntityCallProgram;
import utils.CObjectCatalog;
import utils.CobolNameUtil;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCallProgram extends CEntityCallProgram
{

	/**
	 * @param cat
	 * @param out
	 */
	public CJavaCallProgram(int l, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity ref)
	{
		super(l, cat, out, ref);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (onErrorBloc != null)
		{
			WriteWord("if (") ;
		}
		String name = reference.ExportReference(getLine());
		if (name.startsWith("\"") && bChecked)
		{
			name = name.substring(1, name.length()-1);
			name = CobolNameUtil.fixJavaName(name);
			name += ".class";
		}
		WriteWord("call(" +  name + ")") ;
		if (arrParameters.size()>0)
		{
			for (int i=0; i<arrParameters.size(); i++)
			{
				CCallParameter p = arrParameters.get(i) ;
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
						cs += p.reference.ExportReference(getLine());
					}
					else
					{
						cs += "[UNDEFINED]";
					}
					WriteWord(cs + ")");
					
				}
			}
		}
		if (onErrorBloc != null)
		{
			WriteWord(".executeCallSafe()") ;
			WriteWord(") {") ;
			WriteEOL() ;
			DoExport(onErrorBloc) ;
			WriteLine("}");
			return;
		}
		WriteWord(".executeCall() ;");
		WriteEOL();
	}

}
