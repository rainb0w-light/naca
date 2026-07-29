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
import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for the {@code SELECT} statement bound to a declared SQL cursor
 * ({@code EXEC SQL DECLARE <cursor> CURSOR FOR SELECT ... END-EXEC}).
 *
 * <p>Target-neutral: carries the cursor handle, the SELECT text and the
 * host-variable parameters and exposes read-only getters for the recursive ST4
 * assembler (template {@code recursiveSQLCursorSelectStatementEntity}). The node
 * is {@code ignore()}d as an ordinary procedure child and is instead rendered in
 * place of the {@code OPEN <cursor>} statement; the cursor and each parameter
 * remain semantic children and are recursively rendered through their reference
 * bindings. The SQLWARNING/SQLERROR clause is read from the catalog (registered
 * there by the WHENEVER statement's Stage-1 side effect) so the template can chain
 * it onto the {@code cursorOpen(...)} runtime call.
 *
 * @author U930DI
 */
public class CEntitySQLCursorSelectStatement extends CBaseActionEntity
{
	public CEntitySQLCursorSelectStatement(int line, CObjectCatalog cat)
	{
		super(line, cat);

	}
	public void SetSelect(String csStatement, Vector<CDataEntity> arrParameters, CEntitySQLCursor cur, int nbCol, boolean bWithHold)
	{
		this.csStatement = csStatement ;
		this.parameters = arrParameters;
		cursor = cur ;
		this.nbCol = nbCol ;
		iswithHold = bWithHold ;
	}
	protected int nbCol = 0 ;
	protected String csStatement = "" ;
	protected Vector<CDataEntity> parameters = new Vector<CDataEntity>();
	protected CEntitySQLCursor cursor = null;
	protected boolean iswithHold = false ;
	public void Clear()
	{
		super.Clear();
		parameters.clear() ;
		cursor = null ;
	}

	public int GetNbColumns()
	{
		return nbCol ;
	}
	public boolean ignore()
	{
		return true ; // the SELECT declaration is ignore at this point, but exported in place of the OPEN statement
	}

	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		int i = parameters.indexOf(field) ;
		if (i>=0 && i< parameters.size())
		{
			parameters.get(i).UnRegisterReadingAction(this) ;
			parameters.set(i, var) ;
			var.RegisterReadingAction(this) ;
			return true ;
		} 
		return false ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler. No formatting/output
	// happens here: the adaptor recursively renders the cursor and each parameter
	// child through its reference binding.

	/** The cursor handle semantic child, read by {@code <entity.cursor>}. */
	public CEntitySQLCursor getCursor()
	{
		return cursor ;
	}

	/**
	 * The trimmed SELECT text, read by {@code <entity.statement>}. The template
	 * wraps it as a Java string literal.
	 */
	public String getStatement()
	{
		return csStatement == null ? "" : csStatement.trim() ;
	}

	/** The host-variable parameter children, read by {@code <entity.parameters>}. */
	public List<CDataEntity> getParameters()
	{
		return parameters ;
	}

	/**
	 * Whether the cursor was declared {@code WITH HOLD}, read by
	 * {@code <entity.withHold>}; when true the template chains
	 * {@code .setHoldability(true)} onto the {@code cursorOpen(...)} call.
	 */
	public boolean isWithHold()
	{
		return iswithHold ;
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
