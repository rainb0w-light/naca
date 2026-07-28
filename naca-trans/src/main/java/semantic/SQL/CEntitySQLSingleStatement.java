/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for a raw embedded SQL single statement (an {@code EXEC SQL
 * <text> END-EXEC} that has no dedicated statement entity).
 *
 * <p>Target-neutral: carries the raw SQL text and exposes a read-only getter
 * for the recursive ST4 assembler (template
 * {@code recursiveSQLSingleStatementEntity}). The template wraps the text as a
 * Java string literal inside the legacy {@code getDBConnection().execSQL(...)}
 * runtime call; no formatting/output happens here. Unlike the cursor-bound SQL
 * statement entities, this one carries no WHENEVER SQLWARNING/SQLERROR clause:
 * the retired direct backend never read the policy, and the template mirrors
 * that exactly. Replaces the retired
 * {@code generate.java.SQL.CJavaSQLSingleStatement} direct backend.
 *
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntitySQLSingleStatement extends CBaseActionEntity
{
	protected String csStatement = "" ;

	/**
	 * @param line
	 * @param cat
	 * @param st
	 */
	public CEntitySQLSingleStatement(int line, CObjectCatalog cat, String st)
	{
		super(line, cat);
		csStatement = st ;
	}

	public boolean ignore()
	{
		return false ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getter for the recursive ST4 assembler (template
	// recursiveSQLSingleStatementEntity). No formatting/output happens here:
	// wrapping the statement in the getDBConnection().execSQL("...") runtime
	// call is done by the template, never here.

	/**
	 * The raw SQL statement text carried by this entity, read by
	 * {@code <entity.statement>}. The template wraps it verbatim as a Java
	 * string literal inside the legacy {@code getDBConnection().execSQL(...)}
	 * call, exactly what the retired backend's
	 * {@code WriteLine("getDBConnection().execSQL(\"" + csStatement + "\") ;")}
	 * emitted.
	 */
	public String getStatement()
	{
		return csStatement ;
	}
}
