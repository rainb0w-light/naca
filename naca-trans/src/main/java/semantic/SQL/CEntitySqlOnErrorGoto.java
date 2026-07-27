/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 4 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

import semantic.CBaseActionEntity;
import utils.*;

/**
 * Semantic node for {@code EXEC SQL WHENEVER SQLERROR|SQLWARNING ...} (the
 * SQLERROR/SQLWARNING policy switch).
 *
 * <p>Target-neutral: this statement emits NO code. Its entire effect is a
 * Stage-1 catalog side effect — registering the WHENEVER policy
 * (continue/goto + formatted target label) into {@link CObjectCatalog} so the
 * other SQL backends can append {@code .onErrorGoto(...)}/{@code .onErrorContinue()}
 * onto each SQL runtime call chain. That registration is applied by the entity
 * factory during semantic analysis (in program order), never here; this class
 * only carries the parsed policy and exposes read-only getters for the recursive
 * ST4 assembler (template {@code recursiveSqlOnErrorGotoEntity}, which renders
 * nothing). Mirrors the READ exemplar ({@code semantic.Verbs.CEntityReadFile}).
 *
 * @author sly
 */
public class CEntitySqlOnErrorGoto extends CBaseActionEntity
{

	/**
	 * @param cat
	 */
	public CEntitySqlOnErrorGoto(int l, CObjectCatalog cat, String Reference, boolean OnWarning)
	{
		super(l, cat);
		csRef = Reference ;
		isonWarning = OnWarning ;
	}

	protected String csRef = "" ;
	protected boolean isonWarning = false ;
	public boolean ignore()
	{
		return false ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler. The WHENEVER statement
	// emits no code, so the bound template (recursiveSqlOnErrorGotoEntity) renders
	// empty; these accessors expose the parsed policy for completeness and for the
	// render test, and never perform formatting/output here (that is a Stage-1
	// factory concern).

	public String getReference()
	{
		return csRef;
	}

	public boolean isOnWarning()
	{
		return isonWarning;
	}
}
