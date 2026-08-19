/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL EXECUTE IMMEDIATE :<host> END-EXEC}.
 *
 * <p>Target-neutral: carries the host-variable reference and exposes read-only
 * getters for the recursive ST4 assembler (template
 * {@code recursiveSQLExecuteEntity}). The host variable stays a semantic child
 * and is recursively rendered through its reference binding. The
 * SQLWARNING/SQLERROR clause is read from the catalog (registered there by the
 * WHENEVER statement's Stage-1 side effect) so the template can chain
 * {@code .onErrorGoto(...)}/{@code .onErrorContinue()} onto the
 * {@code sql("EXECUTE IMMEDIATE #1").param(1, <ref>)} runtime call.
 *
 * @author S. Charton
 * @version $Id: CEntitySQLExecute.java,v 1.1 2006/03/01 22:47:49 U930CV Exp $
 */
public class CEntitySQLExecute extends CBaseActionEntity
{

    protected CDataEntity eVariable = null  ;
    /**
     * @param line
     * @param cat
     */
    public CEntitySQLExecute(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /**
     * @param var
     */
    public void setVar(CDataEntity var)
    {
        eVariable = var ;
    }

    public boolean ignore()
    {
        return false ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveSQLExecuteEntity). No formatting/output happens here: the adaptor
    // recursively renders the host-variable child through its reference binding
    // and the template concatenates the sql("EXECUTE IMMEDIATE #1").param(1, ...)
    // runtime call.

    /** The host-variable semantic child, read by {@code <entity.variable>}. */
    public CDataEntity getVariable()
    {
        return eVariable ;
    }

    /**
     * The SQLWARNING/SQLERROR clause to chain onto the {@code sql(...)} runtime
     * call (e.g. {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER
     * policy is in effect. Read from the catalog where the WHENEVER statement
     * registered it.
     */
    public String getSqlWarningErrorStatement()
    {
        return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
    }
}
