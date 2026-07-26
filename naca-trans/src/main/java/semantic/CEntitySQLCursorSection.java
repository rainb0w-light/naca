/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Oct 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import semantic.SQL.CEntitySQLCursor;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntitySQLCursorSection extends CEntityDataSection
{
	/**
	 * @param line
	 * @param name
	 * @param cat
	 */
	public CEntitySQLCursorSection(CObjectCatalog cat)
	{
		super(0, "SQLCursorSection", cat);
	}
	
	protected Vector cursors = null ;
	public void SetCursors(Vector v)
	{
		cursors = v ;
	}
	public CEntityProcedureSection getSectionContainer()
	{
		return null ;
	} 
	public void Clear()
	{
		super.Clear();
		for (int i = 0; i< cursors.size(); i++)
		{
			CEntitySQLCursor cur = (CEntitySQLCursor) cursors.get(i);
			cur.Clear() ;
		}
		cursors.clear() ;
	}
	/* (non-Javadoc)
	 * @see semantic.CEntityDataSection#ignore()
	 */
	@Override
	public boolean ignore()
	{
		return cursors.isEmpty() ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveSQLCursorSectionEntity). They expose the already-resolved semantic
	// values; rendering is done by the template, never here.

	/**
	 * The Java reference expression of every declared cursor, in declaration
	 * order. Mirrors exactly what the retired {@code CJavaSQLCursorSection.DoExport}
	 * computed per cursor ({@code cur.ExportReference(getLine())}); the template
	 * wraps each one as {@code SQLCursor <ref> = declare.cursor() ;}.
	 */
	public List<String> getCursorReferences()
	{
		List<String> references = new ArrayList<>();
		if (cursors != null)
		{
			for (int i = 0; i < cursors.size(); i++)
			{
				CDataEntity cursor = (CDataEntity) cursors.get(i);
				references.add(cursor.ExportReference(getLine()));
			}
		}
		return references;
	}
}
