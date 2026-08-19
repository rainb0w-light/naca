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
 * Semantic node for the embedded SQL session DROP statement (the
 * {@code DROP ...} text the parser assembles token-by-token in
 * {@code CExecSQLSessionDrop}).
 *
 * <p>Target-neutral: carries the raw statement text and exposes read-only
 * getters for the recursive ST4 assembler (template
 * {@code recursiveSQLSessionDropEntity}). The template only wraps the
 * statement text as a Java string literal inside the {@code sql(...)} runtime
 * call. The SQLWARNING/SQLERROR clause is read from the catalog (registered
 * there by the WHENEVER statement's Stage-1 side effect) so the template can
 * chain {@code .onErrorGoto(...)}/{@code .onErrorContinue()} onto the
 * {@code sql(...)} call.
 */
public class CEntitySQLSessionDrop extends CBaseActionEntity
{
    protected String csSql = "" ;

    /**
     * @param line
     * @param cat
     */
    public CEntitySQLSessionDrop(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /**
     * @param string
     */
    public void setSql(String string)
    {
        csSql = string ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveSQLSessionDropEntity). No formatting/output happens here:
    // wrapping the statement in the sql("...") runtime call and chaining the
    // optional WHENEVER clause is done by the template, never here.

    /**
     * The full {@code DROP ...} statement text, assembled token-by-token by
     * the parser and read by {@code <entity.statement>}. The template wraps it
     * as a Java string literal inside {@code sql(...)}.
     */
    public String getStatement()
    {
        return csSql ;
    }

    /**
     * The SQLWARNING/SQLERROR clause to chain onto {@code sql(...)} (e.g.
     * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is
     * in effect. Read from the catalog where the WHENEVER statement registered
     * it.
     */
    public String getSqlWarningErrorStatement()
    {
        return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
    }
}
