/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 août 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.SQL;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import generate.CBaseLanguageExporter;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLCursorSelectStatement;
import semantic.SQL.CEntitySQLOpenStatement;
import utils.CObjectCatalog;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSQLOpenStatement extends CEntitySQLOpenStatement
{
	public CJavaSQLOpenStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out, CEntitySQLCursor csCursorName)
	{
		super(line, cat, out, csCursorName);
	}
   
	protected void DoExport()
	{
		CEntitySQLCursorSelectStatement select = cursor.getSelect() ;
		if (select != null)
		{
			DoExport(select) ;
		}
		else
		{
			String s = "cursorOpen(" + cursor.ExportReference(getLine()) ;
			if (variableStatement != null)
			{
				s += ", " + variableStatement.ExportReference(getLine()) ;
			}
			s += ")";
			WriteWord(s);
			String csSQLErrorWarningStatement = programCatalog.getSQLWarningErrorStatement();
			if(csSQLErrorWarningStatement != null)
			{
				WriteWord(csSQLErrorWarningStatement);
			}
			WriteWord(" ;") ;
		}
		WriteEOL() ;
   }	
}
