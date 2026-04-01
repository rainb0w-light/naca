/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*/*
 * Created on 20 ao�t 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.SQL;
import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLInsertStatement;
import utils.CObjectCatalog;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSQLInsertStatement extends CEntitySQLInsertStatement
{
	public CJavaSQLInsertStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	protected void DoExport()
	{
		String statement = "INSERT INTO " ;
		if (issessionTable)
		{
			statement += "SESSION.";
		}
		if (table != null)
		{
			statement += table.GetTableName() + " (" + table.ExportColReferences() + ")";
		}
		else
		{
			statement += csTable;
			if (collumns != null)
			{
				statement += " (" ; 
				for (int i = 0; i< collumns.size(); i++)
				{
					if (i>0)
					{
						statement += ", " ;
					}
					statement += collumns.get(i) ;
				}
				statement += ")" ;
			}
		}
		if (selectClause.equals(""))
		{
			statement += " VALUES (" ;
			for (int i = 0; i< values.size(); i++)
			{
				CDataEntity e = (CDataEntity) values.get(i);
				if (i>0)
				{
					statement += ", " ;
				}
				if (e.GetDataType() == CDataEntity.CDataEntityType.NUMBER || e.GetDataType() == CDataEntity.CDataEntityType.STRING)
				{
					statement += e.ExportReference(getLine()).replace('"', '\'') ; 
				}
				else
				{ 
					statement += "#"+(i+1) ;
				}
			}
			statement += ")" ;
		}
		else
		{
			statement += " " + selectClause ;
		}
		WriteWord("sql(");
		WriteLongString(statement);
		WriteWord(")");
		if (values != null)
		{
			for (int i = 0; i< values.size(); i++)
			{
				CDataEntity e = (CDataEntity) values.get(i);
				if (e.GetDataType() == CDataEntity.CDataEntityType.NUMBER || e.GetDataType() == CDataEntity.CDataEntityType.STRING)
				{
				}
				else
				{
					WriteEOL();
					WriteWord(".value("+(i+1)+", "+ e.ExportReference(getLine()) +")");
				}
			}
		}
		if (selectParameters != null)
		{
			for(int i = 0; i< selectParameters.size(); i++)
			{
				CDataEntity cs = (CDataEntity) selectParameters.get(i);
				if (cs != null)
				{
					WriteEOL();
					WriteWord(".value("+ (i+1) + ", " + cs.ExportReference(getLine()) + ")");
				}
			}
		}
		String csSQLErrorWarningStatement = programCatalog.getSQLWarningErrorStatement();
		if(csSQLErrorWarningStatement != null)
		{
			WriteEOL();
			WriteWord(csSQLErrorWarningStatement);
		}
		WriteWord(" ;");
		WriteEOL();
	}
}
