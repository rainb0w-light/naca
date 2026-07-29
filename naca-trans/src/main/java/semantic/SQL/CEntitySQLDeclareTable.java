/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 ao�t 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

import java.util.ArrayList;

import parser.Cobol.elements.SQL.CSQLTableColDescriptor;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL DECLARE TABLE ...} (the embedded-SQL table
 * declaration that describes a DB2 table's columns to the transpiler).
 *
 * <p>Target-neutral: this statement emits NO code. Its entire effect is a
 * Stage-1 catalog side effect — registering the table (by view name) into
 * {@link CObjectCatalog} via {@code RegisterSQLTable(csViewName, this)} in the
 * constructor, so the other SQL backends (SELECT/INSERT/UPDATE/DELETE) can resolve
 * the table's column references. That registration happens during semantic
 * analysis (entity construction), never at render time; this class only carries
 * the parsed declaration and exposes read-only getters for the recursive ST4
 * assembler (template {@code recursiveSQLDeclareTableEntity}, which renders
 * nothing). Mirrors the READ exemplar ({@code semantic.Verbs.CEntityReadFile})
 * and the WHENEVER node ({@code semantic.SQL.CEntitySqlOnErrorGoto}).
 *
 * @author U930DI
 */

public class CEntitySQLDeclareTable extends CBaseActionEntity
{
	public CEntitySQLDeclareTable(int line, CObjectCatalog cat, String csTableName, String csViewName, ArrayList arrTableColDescription)
	{
		super(line, cat);
		this.csViewName = csViewName ;
		this.csTableName = csTableName;
		this.arrTableColDescription = arrTableColDescription;
		programCatalog.RegisterSQLTable(csViewName, this);
	}
	protected String csTableName = "";
	protected String csViewName = "" ;
	protected ArrayList arrTableColDescription = null;
	public void Clear()
	{
		super.Clear();
		arrTableColDescription.clear() ;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#RegisterMySelfToCatalog()
	 */

	public String getColumnReferences()
	{
		String out = "" ;
		for (int i=0; i<arrTableColDescription.size();i++)
		{
			CSQLTableColDescriptor desc = (CSQLTableColDescriptor)arrTableColDescription.get(i);
			if (!out.equals(""))
			{
				out += ", " ;
			}
			out += desc.GetName();
		}
		return out;
	}
	public String getColumnReferences(String alias)
	{
		String out = "" ;
		for (int i=0; i<arrTableColDescription.size();i++)
		{
			CSQLTableColDescriptor desc = (CSQLTableColDescriptor)arrTableColDescription.get(i);
			if (!out.equals(""))
			{
				out += ", " ;
			}
			out += alias+"."+desc.GetName();
		}
		return out;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#GetName()
	 */
	public String GetTableName()
	{
		return csTableName ; 
	}

	public int GetNbCols()
	{
		return arrTableColDescription.size();
	}
	public boolean ignore()
	{
		return false ;
	}
	/**
	 * @return
	 */
	public String GetViewName()
	{
		return csViewName ;
	}
	public String GetName()
	{
		return csViewName ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler. The DECLARE TABLE
	// statement emits no code, so the bound template (recursiveSQLDeclareTableEntity)
	// renders empty; these accessors expose the parsed declaration for completeness
	// and for the render test, and never perform formatting/output here (the
	// RegisterSQLTable catalog side effect is a Stage-1 constructor concern). They
	// mirror the existing GetTableName()/GetViewName() accessors exactly.

	public String getTableName()
	{
		return csTableName ;
	}

	public String getViewName()
	{
		return csViewName ;
	}
}
