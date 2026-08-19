/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;

/**
 * @author U930DI
 *
 */
import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL CLOSE <cursor> END-EXEC}.
 *
 * <p>Target-neutral: carries the closed cursor and exposes read-only getters for
 * the recursive ST4 assembler (template {@code recursiveSQLCloseStatementEntity}).
 * The cursor remains a semantic child and is recursively rendered through its
 * reference binding. The SQLWARNING/SQLERROR clause is read from the catalog
 * (registered there by the WHENEVER statement's Stage-1 side effect) so the
 * template can chain {@code .onErrorGoto(...)}/{@code .onErrorContinue()} onto
 * the {@code cursorClose(...)} runtime call.
 */
public class CEntitySQLCloseStatement extends CBaseActionEntity
{
    public CEntitySQLCloseStatement(int line, CObjectCatalog cat, CEntitySQLCursor cursor)
    {
        super(line, cat);
        this.cursor = cursor ;
    }
    protected CEntitySQLCursor cursor = null ;
    public boolean ignore()
    {
        return false ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler. No formatting/output
    // happens here: the adaptor recursively renders the semantic cursor child.

    /** The closed cursor semantic child, read by {@code <entity.cursor>}. */
    public CEntitySQLCursor getCursor()
    {
        return cursor ;
    }

    /**
     * The SQLWARNING/SQLERROR clause to chain onto {@code cursorClose(...)} (e.g.
     * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
     * effect. Read from the catalog where the WHENEVER statement registered it.
     */
    public String getSqlWarningErrorStatement()
    {
        return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
    }
}
