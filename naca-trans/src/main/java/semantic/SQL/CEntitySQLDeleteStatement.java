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
import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL DELETE FROM ... END-EXEC}.
 *
 * <p>Target-neutral: carries the DELETE text, the host-variable parameters and the
 * optional bound cursor, and exposes read-only getters for the recursive ST4
 * assembler (template {@code recursiveSQLDeleteStatementEntity}). The cursor and
 * each parameter remain semantic children and are recursively rendered through
 * their reference bindings. The SQLWARNING/SQLERROR clause is read from the catalog
 * (registered there by the WHENEVER statement's Stage-1 side effect) so the
 * template can chain it onto the {@code sql(...)}/{@code cursorDeleteCurrent(...)}
 * runtime call.
 */
public class CEntitySQLDeleteStatement extends CBaseActionEntity
{
    /** Creates a new centity sqldelete statement instance. */
    public CEntitySQLDeleteStatement(int line, CObjectCatalog cat, String csStatement, Vector<CDataEntity> parameters)
    {
        super(line, cat);
        this.csStatement = csStatement ;
        this.parameters = parameters;
    }
    protected String csStatement = "" ;
    protected Vector<CDataEntity> parameters = null;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        parameters.clear();
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        int n = parameters.indexOf(field);
        if (n>=0)
        {
            parameters.get(n).UnRegisterReadingAction(this) ;
            parameters.set(n, var);
            var.RegisterReadingAction(this) ;
            return true ;
        }
        return false ;
    }
    /**
     * @param cursor
     */
    public void setCursor(CEntitySQLCursor cursor)
    {
        this.cursor = cursor ;
    }
    protected CEntitySQLCursor cursor = null ;

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler. No formatting/output
    // happens here: the adaptor recursively renders the cursor and each parameter
    // child through its reference binding.

    /** The bound cursor semantic child, read by {@code <entity.cursor>}; {@code null}
     * for an unbound {@code DELETE FROM ...} (rendered as {@code sql("<statement>")}). */
    public CEntitySQLCursor getCursor()
    {
        return cursor ;
    }

    /**
     * The trimmed DELETE text, read by {@code <entity.statement>}. The template
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
     * The SQLWARNING/SQLERROR clause to chain onto the runtime call (e.g.
     * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
     * effect. Read from the catalog where the WHENEVER statement registered it.
     */
    public String getSqlWarningErrorStatement()
    {
        return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
    }
}
