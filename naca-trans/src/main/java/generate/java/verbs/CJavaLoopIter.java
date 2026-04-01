/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

//import parser.expression.CBaseExpressionExporter;

import generate.*;
import semantic.CDataEntity;
import semantic.Verbs.*;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaLoopIter extends CEntityLoopIter
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaLoopIter(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (istestBefore)
		{
			_for(initialValue, variable, whileCondition, increment, isincrementByOne);
			for (CEntityAfter a : afters)
			{
				_for(a.varFromValueAfter, a.variableAfter, a.condUntilAfter, a.varByValueAfter, true);
			}
			ExportChildren() ;
			for (@SuppressWarnings("unused") CEntityAfter a : afters)
			{
				_EndBlock();
			}
			_EndBlock();
		}
		else
		{
			WriteLine("move(" + initialValue.ExportReference(getLine()) + ", " + variable.ExportReference(getLine()) + ");");
			WriteLine("while (true) {");
			StartOutputBloc() ;
			ExportChildren() ;

			WriteLine("if (" + whileCondition.Export() + ") {") ;
			StartOutputBloc() ;
			String cs = "" ;
			if (increment != null)
			{
				cs = "add(" + increment.ExportReference(getLine()) + ").to(" ;
			}
			else
			{
				if(isincrementByOne)
				{
					cs = "inc(" ;
				}
				else if(isdecrementByOne)
				{
					cs = "dec(" ;
				}
			}
			cs += variable.ExportReference(getLine()) ;
			WriteWord(cs+") ;") ;
			WriteEOL();
			_EndBlock();
			WriteLine("else {");
			StartOutputBloc() ;
			WriteLine("break ;");
			_EndBlock();
			
			_EndBlock();
		}
	}

	private void _for(CDataEntity initialValue, CDataEntity variable,
			CBaseEntityCondition whileCondition, CDataEntity increment, boolean bIncrementByOne)
	{
		String cs = "for (move(" + initialValue.ExportReference(getLine()) + ", " + variable.ExportReference(getLine()) + "); " ;
		WriteWord(cs);
		WriteWord(whileCondition.Export() + "; ") ;

		cs = "" ;
		if (increment != null)
		{
			cs = "inc(" + increment.ExportReference(getLine()) + ", " ;
		}
		else
		{
			if(bIncrementByOne)
			{
				cs = "inc(" ;
			}
			else
			{
				cs = "dec(" ;
			}
		}
		cs += variable.ExportReference(getLine()) ;
		WriteWord(cs+")) {") ;
		WriteEOL() ;
		StartOutputBloc() ;
	}

	private void _EndBlock() {
		EndOutputBloc() ;
		WriteLine("}") ;
	}

}
