/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;

import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL CALL <program> [USING (:host, ...)] END-EXEC}.
 *
 * <p>Target-neutral: carries the called program reference and the host-variable
 * parameters and exposes read-only getters for the recursive ST4 assembler
 * (template {@code recursiveSQLCallEntity}). The program reference and each
 * parameter remain semantic children and are recursively rendered through their
 * reference bindings. The SQLWARNING/SQLERROR clause is read from the catalog
 * (registered there by the WHENEVER statement's Stage-1 side effect) so the
 * template can chain {@code .onErrorGoto(...)}/{@code .onErrorContinue()} onto the
 * {@code sqlCall(...)} runtime call.
 *
 * @author S. Charton
 * @version $Id: CEntitySQLCall.java,v 1.1 2006/08/04 08:32:52 u930cv Exp $
 */
public class CEntitySQLCall extends CBaseActionEntity
{

    protected CDataEntity programReference = null ;
    protected Vector<CDataEntity> parameters = new Vector<CDataEntity>() ;

    /**
     * @param line
     * @param cat
     */
    public CEntitySQLCall(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /**
     * @param prgRef
     */
    public void setReference(CDataEntity prgRef)
    {
        programReference = prgRef ;
    }

    /**
     * @param param
     */
    public void addParameter(CDataEntity param)
    {
        parameters.add(param) ;
    }

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler. No formatting/output
    // happens here: the adaptor recursively renders the program reference and each
    // parameter child through its reference binding.

    /** The called program semantic child, read by {@code <entity.programReference>}. */
    public CDataEntity getProgramReference()
    {
        return programReference ;
    }

    /** The host-variable parameter children, read by {@code <entity.parameters>}. */
    public List<CDataEntity> getParameters()
    {
        return parameters ;
    }

    /**
     * The SQLWARNING/SQLERROR clause to chain onto {@code sqlCall(...)} (e.g.
     * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
     * effect. Read from the catalog where the WHENEVER statement registered it.
     */
    public String getSqlWarningErrorStatement()
    {
        return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
    }

}
