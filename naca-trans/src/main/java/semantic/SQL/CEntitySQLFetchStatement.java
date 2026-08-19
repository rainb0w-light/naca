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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL FETCH <cursor> INTO :host [, :ind] ... END-EXEC}.
 *
 * <p>Target-neutral: carries the fetched cursor and the INTO host-variable targets
 * (each with its optional INDICATOR variable) and exposes read-only getters for the
 * recursive ST4 assembler (template {@code recursiveSQLFetchStatementEntity}). The
 * cursor, every INTO target and every INDICATOR remain semantic children and are
 * recursively rendered through their reference bindings. The SQLWARNING/SQLERROR
 * clause is read from the catalog (registered there by the WHENEVER statement's
 * Stage-1 side effect) so the template can chain {@code .onErrorGoto(...)}/
 * {@code .onErrorContinue()} onto the {@code cursorFetch(...)} runtime call.
 */
public class CEntitySQLFetchStatement extends CBaseActionEntity
{
    /* (non-Javadoc)
     * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
     */
    @Override
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (into.contains(field))
        {
            int pos = into.indexOf(field) ;
            into.set(pos, var) ;
            field.UnRegisterWritingAction(this) ;
            var.RegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }
    /** Creates a new centity sqlfetch statement instance. */
    public CEntitySQLFetchStatement(int line, CObjectCatalog cat, CEntitySQLCursor cur)
    {
        super(line, cat);
        cursor = cur;
        into = new Vector<CDataEntity>() ;
        indicators = new Vector<CDataEntity>() ;
    }
    protected CEntitySQLCursor cursor = null ;
    protected Vector<CDataEntity> into = null;
    protected Vector<CDataEntity> indicators = null;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        into.clear() ;
        cursor.Clear() ;
        cursor = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    /** Adds the fetch into. */
    public void AddFetchInto(CDataEntity e, CDataEntity eInd)
    {
        into.add(e) ;
        indicators.add(eInd) ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler. No formatting/output
    // happens here: the adaptor recursively renders the cursor and each INTO host
    // variable (with its optional INDICATOR) child through its reference binding.

    /** The fetched cursor semantic child, read by {@code <entity.cursor>}. */
    public CEntitySQLCursor getCursor()
    {
        return cursor ;
    }

    /**
     * The FETCH ... INTO host-variable bindings, read by {@code <entity.intoBindings>}.
     * Each binding pairs an INTO target with its optional INDICATOR variable; both
     * stay semantic children the template renders through their reference bindings.
     */
    public List<FetchIntoBinding> getIntoBindings()
    {
        List<FetchIntoBinding> bindings = new ArrayList<>(into.size()) ;
        for (int i = 0; i < into.size(); i++)
        {
            bindings.add(new FetchIntoBinding(into.get(i), indicators.get(i))) ;
        }
        return bindings ;
    }

    /**
     * The SQLWARNING/SQLERROR clause to chain onto {@code cursorFetch(...)} (e.g.
     * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
     * effect. Read from the catalog where the WHENEVER statement registered it.
     */
    public String getSqlWarningErrorStatement()
    {
        return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
    }

    /**
     * A single FETCH ... INTO target paired with its optional INDICATOR variable.
     * A read-only semantic holder: it exposes the two host-variable children to the
     * recursive ST4 assembler ({@code <b.into>} / {@code <b.indicator>}) and does no
     * formatting itself.
     */
    public static final class FetchIntoBinding
    {
        private final CDataEntity into ;
        private final CDataEntity indicator ;

        FetchIntoBinding(CDataEntity into, CDataEntity indicator)
        {
            this.into = into ;
            this.indicator = indicator ;
        }

        /** The INTO target host variable, read by {@code <b.into>}. */
        public CDataEntity getInto()
        {
            return into ;
        }

        /** The optional INDICATOR host variable, read by {@code <b.indicator>}; may be null. */
        public CDataEntity getIndicator()
        {
            return indicator ;
        }
    }
}
