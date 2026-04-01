/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 ao�t 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.SQL;

import java.util.Vector;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLSelectStatement;
import utils.CObjectCatalog;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSQLSelectStatement extends CEntitySQLSelectStatement
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 * @param st
	 */
	public CJavaSQLSelectStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out, String csStatement, Vector<CDataEntity> arrParameters, Vector<CDataEntity> arrInto, Vector<CDataEntity> arrInd)
	{
		super(line, cat, out, csStatement, arrParameters, arrInto, arrInd);
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		boolean isbloc = false ;
		WriteWord("sql(") ;
		WriteLongString(csStatement.trim()) ;
		WriteWord(")");
		for(int i = 0; i< into.size(); i++)
		{
			WriteEOL();
			if (!isbloc)
			{
				StartOutputBloc() ;
				isbloc = true ;
			}
			CDataEntity cs = into.get(i);
			String out = ".into(" + cs.ExportReference(getLine()) ;
			if (i< ind.size())
			{
				CDataEntity e = ind.get(i) ;
				if (e != null)
				{
					out += ", "+e.ExportReference(getLine()) ;
				}
			}
			WriteWord(out + ")");
		}
		for(int i = 0; i< parameters.size(); i++)
		{
			CDataEntity cs = parameters.get(i);
			if (cs != null)
			{
				WriteEOL();
				if (!isbloc)
				{
					StartOutputBloc() ;
					isbloc = true ;
				}
				WriteWord(".param("+ (i+1) + ", " + cs.ExportReference(getLine()) + ")");
			}
		}
		String csSQLErrorWarningStatement = programCatalog.getSQLWarningErrorStatement();
		if(csSQLErrorWarningStatement != null)
		{
			WriteEOL();
			WriteWord(csSQLErrorWarningStatement);
		}
		WriteWord(" ;") ;
		WriteEOL() ;
		if (isbloc)
		{
			EndOutputBloc() ;
		}
	}	
}
