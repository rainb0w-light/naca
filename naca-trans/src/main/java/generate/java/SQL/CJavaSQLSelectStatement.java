/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 août 04
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
		boolean bBloc = false ;
		WriteWord("sql(") ;
		WriteLongString(csStatement.trim()) ;
		WriteWord(")");
		for(int i=0; i<arrInto.size(); i++)
		{
			WriteEOL();
			if (!bBloc)
			{
				StartOutputBloc() ;
				bBloc = true ;
			}
			CDataEntity cs = arrInto.get(i);
			String out = ".into(" + cs.ExportReference(getLine()) ;
			if (i<arrInd.size())
			{
				CDataEntity e = arrInd.get(i) ;
				if (e != null)
				{
					out += ", "+e.ExportReference(getLine()) ;
				}
			}
			WriteWord(out + ")");
		}
		for(int i=0; i<arrParameters.size(); i++)
		{
			CDataEntity cs = arrParameters.get(i);
			if (cs != null)
			{
				WriteEOL();
				if (!bBloc)
				{
					StartOutputBloc() ;
					bBloc = true ;
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
		if (bBloc)
		{
			EndOutputBloc() ;
		}
	}	
}
