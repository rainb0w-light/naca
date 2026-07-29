/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL LOCK TABLE <table> IN EXCLUSIVE MODE END-EXEC}.
 *
 * <p>Target-neutral: carries the locked table name and exposes read-only getters
 * for the recursive ST4 assembler (template {@code recursiveSQLLockEntity}). The
 * full {@code LOCK TABLE <table> IN EXCLUSIVE MODE} statement text is assembled in
 * Stage 1 so the template only wraps it as a Java string literal inside the
 * {@code sql(...)} runtime call. The SQLWARNING/SQLERROR clause is read from the
 * catalog (registered there by the WHENEVER statement's Stage-1 side effect) so
 * the template can chain {@code .onErrorGoto(...)}/{@code .onErrorContinue()} onto
 * the {@code sql(...)} call.
 *
 * @author S. Charton
 * @version $Id: CEntitySQLLock.java,v 1.1 2006/03/01 22:47:49 U930CV Exp $
 */
public class CEntitySQLLock extends CBaseActionEntity
{

	protected String csTableName = "" ;

	/**
	 * @param line
	 * @param cat
	 */
	public CEntitySQLLock(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	/**
	 * @param string
	 */
	public void setTable(String string)
	{
		csTableName = string ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveSQLLockEntity). No formatting/output happens here: wrapping the
	// statement in the sql("...") runtime call and chaining the optional WHENEVER
	// clause is done by the template, never here.

	/**
	 * The full {@code LOCK TABLE <table> IN EXCLUSIVE MODE} statement text,
	 * assembled in Stage 1 and read by {@code <entity.statement>}. The template
	 * wraps it as a Java string literal inside {@code sql(...)}.
	 */
	public String getStatement()
	{
		return "LOCK TABLE " + csTableName + " IN EXCLUSIVE MODE" ;
	}

	/**
	 * The SQLWARNING/SQLERROR clause to chain onto {@code sql(...)} (e.g.
	 * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
	 * effect. Read from the catalog where the WHENEVER statement registered it.
	 */
	public String getSqlWarningErrorStatement()
	{
		return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
	}
}
