/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 ao�t 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL OPEN <cursor> END-EXEC}.
 *
 * <p>Target-neutral: carries the opened cursor and the optional USING
 * descriptor/host variable and exposes read-only getters for the recursive ST4
 * assembler (template {@code recursiveSQLOpenStatementEntity}). The cursor and
 * the USING variable remain semantic children and are recursively rendered
 * through their reference bindings. When the cursor has a bound SELECT (the
 * {@code SELECT} of its {@code DECLARE CURSOR}), that SELECT is rendered in
 * place of the OPEN statement, so the template picks the cursor's
 * {@code select} child when present and the
 * {@code cursorOpen(<ref>[, <variable>])} runtime call otherwise. The
 * SQLWARNING/SQLERROR clause is read from the catalog (registered there by the
 * WHENEVER statement's Stage-1 side effect) so the template can chain it onto
 * the {@code cursorOpen(...)} runtime call.
 */
public class CEntitySQLOpenStatement extends CBaseActionEntity
{
	public CEntitySQLOpenStatement(int line, CObjectCatalog cat, CEntitySQLCursor cur)
	{
		super(line, cat);
		cursor = cur ;
	}
	protected CEntitySQLCursor cursor = null ;
	protected CDataEntity variableStatement = null ;
	public boolean ignore()
	{
		return false ;
	}
	/**
	 * @param var
	 */
	public void setVariableStatement(CDataEntity var)
	{
		variableStatement  = var ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler. No formatting/output
	// happens here: the adaptor recursively renders the semantic cursor and
	// variable children, and the bound SELECT child when present.

	/** The opened cursor semantic child, read by {@code <entity.cursor>}. */
	public CEntitySQLCursor getCursor()
	{
		return cursor ;
	}

	/**
	 * The SELECT bound to the cursor's {@code DECLARE CURSOR}, read by
	 * {@code <entity.select>}; when non-null the template renders it in place of
	 * the OPEN statement (the {@code cursorOpen(<ref>, "<select>")...} runtime
	 * call).
	 */
	public CEntitySQLCursorSelectStatement getSelect()
	{
		return cursor == null ? null : cursor.getSelect() ;
	}

	/**
	 * The optional USING descriptor/host variable semantic child, read by
	 * {@code <entity.variableStatement>}; when present the template renders it as
	 * the second argument of the {@code cursorOpen(<ref>, <variable>)} runtime
	 * call.
	 */
	public CDataEntity getVariableStatement()
	{
		return variableStatement ;
	}

	/**
	 * The SQLWARNING/SQLERROR clause to chain onto {@code cursorOpen(...)} (e.g.
	 * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
	 * effect. Read from the catalog where the WHENEVER statement registered it.
	 */
	public String getSqlWarningErrorStatement()
	{
		return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
	}
}
