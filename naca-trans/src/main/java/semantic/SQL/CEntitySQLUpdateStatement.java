/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Semantic node for {@code EXEC SQL UPDATE ... END-EXEC} (either a standalone
 * {@code UPDATE <table> SET ... WHERE ...} or a cursor-positioned
 * {@code UPDATE ... WHERE CURRENT OF <cursor>} bound to a DECLARE CURSOR).
 *
 * <p>Target-neutral: carries the prepared UPDATE text, the SET host-variable
 * values, the WHERE host-variable parameters and the optional bound cursor, and
 * exposes read-only getters for the recursive ST4 assembler (template
 * {@code recursiveSQLUpdateStatementEntity}). Every SET value and every parameter
 * remains a semantic child rendered through its reference binding; the cursor
 * handle stays a semantic child rendered through its {@code dataReferenceEntity}
 * binding. The SQLWARNING/SQLERROR clause is read from the catalog (registered
 * there by the WHENEVER statement's Stage-1 side effect) so the template can
 * chain {@code .onErrorGoto(...)}/{@code .onErrorContinue()} onto the runtime
 * call. The former direct backend is retired.
 */
public class CEntitySQLUpdateStatement extends CBaseActionEntity
{
    public CEntitySQLUpdateStatement(
        int line,
        CObjectCatalog cat,
        String csStatement,
        Vector<CDataEntity> arrSets,
        Vector<CDataEntity> arrParameters)
    {
        super(line, cat);
        this.csStatement = csStatement ;
        this.sets = arrSets;
        this.parameters = arrParameters;
    }
    protected String csStatement = "" ;
    protected Vector<CDataEntity> sets = null;
    protected Vector<CDataEntity> parameters = null;
    public void Clear()
    {
        super.Clear();
        sets.clear() ;
        parameters.clear() ;
    }
    public boolean ignore()
    {
        return false ;
    }
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
        n = sets.indexOf(field);
        if (n>=0)
        {
            sets.get(n).UnRegisterReadingAction(this) ;
            sets.set(n, var);
            var.RegisterReadingAction(this) ;
            return true ;
        }
        return false ;
    }
    /**
     * @param cur
     */
    public void setCursor(CEntitySQLCursor cur)
    {
        cursor = cur ;
    }
    protected CEntitySQLCursor cursor = null ;

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveSQLUpdateStatementEntity). No formatting/output happens here: the
    // adaptor recursively renders the cursor and each SET value / parameter child
    // through its reference binding.

    /** The bound cursor semantic child, read by {@code <entity.cursor>}; {@code null}
     * for a standalone {@code UPDATE ...} (rendered as {@code sql("<statement>")}). */
    public CEntitySQLCursor getCursor()
    {
        return cursor ;
    }

    /**
     * The trimmed UPDATE text, read by {@code <entity.statement>}. The template
     * wraps it as a Java string literal while this semantic node only supplies
     * the normalized statement text.
     */
    public String getStatement()
    {
        return csStatement == null ? "" : csStatement.trim() ;
    }

    /**
     * The SET host-variable value children, read by {@code <entity.sets>}. The
     * template chains one 1-based {@code .value(<i>, <ref>)} per entry (the ST4
     * {@code <i>} iteration index matches the retired backend's {@code (i+1)}
     * numbering); every value stays a semantic child rendered through its reference
     * binding.
     */
    public List<CDataEntity> getSets()
    {
        return sets == null ? Collections.emptyList() : sets ;
    }

    /**
     * The WHERE host-variable parameter bindings, read by {@code <entity.paramBindings>}.
     * Each binding carries the parameter's computed 1-based position and its
     * host-variable child; {@code null} parameters are dropped (exactly as the
     * retired backend's {@code if (e != null)} guard did) while the remaining
     * parameters keep the retired backend's {@code (i+1+sets.size())} numbering —
     * i.e. they continue the SET values' numbering — which the template emits as
     * {@code .param(<p.index>, <p.ref>)}.
     */
    public List<UpdateParamBinding> getParamBindings()
    {
        if (parameters == null)
        {
            return Collections.emptyList() ;
        }
        int setsSize = sets == null ? 0 : sets.size() ;
        List<UpdateParamBinding> bindings = new ArrayList<>(parameters.size()) ;
        for (int i = 0; i < parameters.size(); i++)
        {
            CDataEntity param = parameters.get(i) ;
            if (param != null)
            {
                bindings.add(new UpdateParamBinding(i + 1 + setsSize, param)) ;
            }
        }
        return bindings ;
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

    /**
     * A single WHERE host-variable parameter paired with its computed 1-based
     * position (continuing the SET values' numbering, exactly as the retired
     * backend's {@code (i+1+sets.size())} index). A read-only semantic holder: it
     * exposes the position and the host-variable child to the recursive ST4
     * assembler ({@code <p.index>} / {@code <p.ref>}) and does no formatting itself.
     */
    public static final class UpdateParamBinding
    {
        private final int index ;
        private final CDataEntity ref ;

        UpdateParamBinding(int index, CDataEntity ref)
        {
            this.index = index ;
            this.ref = ref ;
        }

        /** The 1-based parameter position (offset by the SET count), read by {@code <p.index>}. */
        public int getIndex()
        {
            return index ;
        }

        /** The host-variable parameter child, read by {@code <p.ref>}. */
        public CDataEntity getRef()
        {
            return ref ;
        }
    }
}
