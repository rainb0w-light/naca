/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityLoopIter;
import utils.CObjectCatalog;
import utils.NacaTransAssertException;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaLoopIter.java,v 1.3 2007/06/28 16:33:58 u930bm Exp $
 */
public class CFPacJavaLoopIter extends CEntityLoopIter
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CFPacJavaLoopIter(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	/**
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	@Override
	protected void DoExport()
	{
		if (bTestBefore)
		{
			String cs = "for (" + variable.ExportReference(getLine()) + "=" + initialValue.ExportReference(getLine()) + "; " ;
			WriteWord(cs);
			WriteWord(whileCondition.Export() + "; ") ;
		
			cs = variable.ExportReference(getLine()) ;
			if (increment != null)
			{
				cs += "+=" + increment.ExportReference(getLine()) ;
			}
			else
			{
				if(bIncrementByOne)
				{
					cs += "++" ;
				}
				else if(bDecrementByOne)
				{
					cs += "--" ;
				}
			}
			WriteWord(cs+") {") ;
			WriteEOL() ;
			StartOutputBloc() ;
			ExportChildren() ;
			EndOutputBloc() ;
			WriteLine("}") ;
		}
		else
		{
			throw new NacaTransAssertException("Expecting not a Loop");
		}
	}

}
