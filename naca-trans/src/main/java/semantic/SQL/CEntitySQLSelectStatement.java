/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 aot 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
 * Semantic node for {@code EXEC SQL SELECT ... INTO :host [, :ind] ... END-EXEC}
 * (the standalone, non-cursor form; a SELECT bound to a DECLARE CURSOR is a
 * {@link CEntitySQLCursorSelectStatement}).
 *
 * <p>Target-neutral: carries the prepared SELECT text, the host-variable
 * parameters, the INTO host-variable targets (each with its optional INDICATOR
 * variable) and exposes read-only getters for the recursive ST4 assembler
 * (template {@code recursiveSQLSelectStatementEntity}). Every parameter, every
 * INTO target and every INDICATOR remain semantic children and are recursively
 * rendered through their reference bindings. The SQLWARNING/SQLERROR clause is
 * read from the catalog (registered there by the WHENEVER statement's Stage-1
 * side effect) so the template can chain {@code .onErrorGoto(...)}/
 * {@code .onErrorContinue()} onto the {@code sql(...)} runtime call.
 */
public class CEntitySQLSelectStatement extends CBaseActionEntity
{
	public CEntitySQLSelectStatement(int line, CObjectCatalog cat, String csStatement, Vector<CDataEntity> parameters, Vector<CDataEntity> into, Vector<CDataEntity> ind)
	{
		super(line, cat);
		this.csStatement = csStatement ;
		this.parameters = parameters;
		this.into = into;
		this.ind = ind;
	}
	protected String csStatement = "" ;
	protected Vector<CDataEntity> parameters = null;
	protected Vector<CDataEntity> into = null;
	protected Vector<CDataEntity> ind = null;
	public void Clear()
	{
		super.Clear();
		parameters = null ;
		into = null ;
	}
	public boolean ignore()
	{
		return csStatement.equals("") ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (parameters.contains(data) || into.contains(data))
		{
			data.UnRegisterReadingAction(this) ;
			data.UnRegisterWritingAction(this) ;
			csStatement = "" ;
			return true ;
		}
		return false ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		int n = parameters.indexOf(field) ;
		if (n>=0)
		{
			parameters.get(n).UnRegisterReadingAction(this) ;
			parameters.set(n, var);
			var.RegisterReadingAction(this) ;
			return true ;
		}
		n = into.indexOf(field) ;
		if (n>=0)
		{
			into.get(n).UnRegisterWritingAction(this) ;
			into.set(n, var);
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler. No formatting/output
	// happens here: the adaptor recursively renders each parameter, each INTO
	// target and each optional INDICATOR child through its reference binding.

	/**
	 * The trimmed SELECT text, read by {@code <entity.statement>}. The template
	 * wraps it as a Java string literal.
	 */
	public String getStatement()
	{
		return csStatement == null ? "" : csStatement.trim() ;
	}

	/**
	 * The SELECT ... INTO host-variable bindings, read by {@code <entity.intoBindings>}.
	 * Each binding pairs an INTO target with its optional INDICATOR variable (paired
	 * by position; when there are fewer indicators than INTO targets the trailing
	 * targets have a {@code null} indicator, exactly as the retired backend's
	 * {@code if (i < ind.size())} guard did); both stay semantic children the
	 * template renders through their reference bindings.
	 */
	public List<SelectIntoBinding> getIntoBindings()
	{
		if (into == null)
		{
			return Collections.emptyList() ;
		}
		List<SelectIntoBinding> bindings = new ArrayList<>(into.size()) ;
		for (int i = 0; i < into.size(); i++)
		{
			CDataEntity indicator = (ind != null && i < ind.size()) ? ind.get(i) : null ;
			bindings.add(new SelectIntoBinding(into.get(i), indicator)) ;
		}
		return bindings ;
	}

	/**
	 * The host-variable parameter bindings, read by {@code <entity.paramBindings>}.
	 * Each binding carries the parameter's original 1-based position and its
	 * host-variable child; {@code null} parameters are dropped (exactly as the
	 * retired backend's {@code if (cs != null)} guard did) while the remaining
	 * parameters keep their original {@code (i+1)} numbering, which the template
	 * emits as {@code .param(<p.index>, <p.ref>)}.
	 */
	public List<SelectParamBinding> getParamBindings()
	{
		if (parameters == null)
		{
			return Collections.emptyList() ;
		}
		List<SelectParamBinding> bindings = new ArrayList<>(parameters.size()) ;
		for (int i = 0; i < parameters.size(); i++)
		{
			CDataEntity param = parameters.get(i) ;
			if (param != null)
			{
				bindings.add(new SelectParamBinding(i + 1, param)) ;
			}
		}
		return bindings ;
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

	/**
	 * A single SELECT ... INTO target paired with its optional INDICATOR variable.
	 * A read-only semantic holder: it exposes the two host-variable children to the
	 * recursive ST4 assembler ({@code <b.into>} / {@code <b.indicator>}) and does no
	 * formatting itself.
	 */
	public static final class SelectIntoBinding
	{
		private final CDataEntity m_into ;
		private final CDataEntity m_indicator ;

		SelectIntoBinding(CDataEntity into, CDataEntity indicator)
		{
			m_into = into ;
			m_indicator = indicator ;
		}

		/** The INTO target host variable, read by {@code <b.into>}. */
		public CDataEntity getInto()
		{
			return m_into ;
		}

		/** The optional INDICATOR host variable, read by {@code <b.indicator>}; may be null. */
		public CDataEntity getIndicator()
		{
			return m_indicator ;
		}
	}

	/**
	 * A single host-variable parameter paired with its original 1-based position.
	 * A read-only semantic holder: it exposes the position and the host-variable
	 * child to the recursive ST4 assembler ({@code <p.index>} / {@code <p.ref>}) and
	 * does no formatting itself. The index preserves the retired backend's
	 * {@code (i+1)} numbering even when earlier parameters were {@code null}.
	 */
	public static final class SelectParamBinding
	{
		private final int m_index ;
		private final CDataEntity m_ref ;

		SelectParamBinding(int index, CDataEntity ref)
		{
			m_index = index ;
			m_ref = ref ;
		}

		/** The 1-based parameter position, read by {@code <p.index>}. */
		public int getIndex()
		{
			return m_index ;
		}

		/** The host-variable parameter child, read by {@code <p.ref>}. */
		public CDataEntity getRef()
		{
			return m_ref ;
		}
	}
}
