/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java;

import generate.CBaseLanguageExporter;
import semantic.CEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCondition extends CEntityCondition
{

	/**
	 * @param cat
	 * @param out
	 */
	public CJavaCondition(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseSemanticEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (condition == null)
		{
			return ;
		}
		if (condition.ignore())
		{
			if (elseBloc != null && !elseBloc.ignore())
			{
				WriteLine("{", elseBloc.getLine()) ;
				DoExport(elseBloc) ;
				WriteLine("}") ;
			}
			return ; 
		}
		WriteWord("if (");
		condition.SetLine(getLine());
		String cs = condition.Export() ;
		cs += ") {" ;
		WriteWord(cs) ;
		WriteEOL() ;
		DoExport(thenBloc) ;
		int n = thenBloc.GetEndLine() ;
		if (n == 0 && elseBloc != null)
		{
			n = elseBloc.getLine() -1 ;
		}
		WriteLine("}", n) ;
		if (elseBloc != null)
		{
			WriteLine("else {", elseBloc.getLine()) ;
			DoExport(elseBloc) ;
			WriteLine("}", elseBloc.GetEndLine()) ;
		}
		

	}

}
