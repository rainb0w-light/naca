/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 août 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.SQL;
import java.util.Vector;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLUpdateStatement;
import utils.CObjectCatalog;


/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSQLUpdateStatement extends CEntitySQLUpdateStatement
{
	public CJavaSQLUpdateStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out, String csStatement, Vector<CDataEntity> arrSets, Vector<CDataEntity> arrParameters)
	{
		super(line, cat, out, csStatement, arrSets, arrParameters);
	}
	
	protected void DoExport()
	{
		if (cursor == null)
		{
			WriteWord("sql(");
			WriteLongString(csStatement.trim());
			WriteWord(")");
		}
		else
		{
			WriteWord("cursorUpdateCurrent(");
			WriteWord(cursor.ExportReference(getLine())+ ", ");
			WriteLongString(csStatement.trim());
			WriteWord(")");
		}
		for(int i=0; i<arrSets.size(); i++)
		{
			WriteEOL();
			CDataEntity e = arrSets.get(i);
			WriteWord(".value("+(i+1) +", " + e.ExportReference(getLine())+ ")");
		}
		for(int i=0; i<arrParameters.size(); i++)
		{
			CDataEntity e = arrParameters.get(i);
			if (e != null)
			{
				WriteEOL();
				WriteWord(".param("+(i+1+arrSets.size()) + ", " + e.ExportReference(getLine())+ ")");
			}
		}
		String csSQLErrorWarningStatement = programCatalog.getSQLWarningErrorStatement();
		if(csSQLErrorWarningStatement != null)
		{
			WriteEOL();
			WriteWord(csSQLErrorWarningStatement);
		}
		WriteWord(";") ;
		WriteEOL();
	}	

}

