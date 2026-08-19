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
 * Semantic node for {@code EXEC SQL COMMIT END-EXEC}.
 *
 * <p>Target-neutral: exposes a read-only getter for the recursive ST4 assembler
 * (template {@code recursiveSQLCommitEntity}). The SQLWARNING/SQLERROR clause is
 * read from the catalog (registered there by the WHENEVER statement's Stage-1
 * side effect) so the template can chain {@code .onErrorGoto(...)}/
 * {@code .onErrorContinue()} onto the {@code sqlCommit()} runtime call.
 *
 * @author sly
 */
public class CEntitySQLCommit extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntitySQLCommit(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }
    public boolean ignore()
    {
        return false ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveSQLCommitEntity). No formatting/output happens here: formatting
    // the sqlCommit() runtime call and chaining the optional WHENEVER clause is
    // done by the template, never here.

    /**
     * The SQLWARNING/SQLERROR clause to chain onto {@code sqlCommit()} (e.g.
     * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
     * effect. Read from the catalog where the WHENEVER statement registered it.
     */
    public String getSqlWarningErrorStatement()
    {
        return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
    }
}
