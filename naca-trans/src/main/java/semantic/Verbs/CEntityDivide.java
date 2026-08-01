/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 sept. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;


import java.util.function.BiFunction;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityDivide extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityDivide(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}
	
	protected CDataEntity what = null ;
	protected CDataEntity by = null ;
	protected CDataEntity result = null ;
	protected CDataEntity remainder = null ;
	protected boolean isisRounded = false ;
	public void Clear()
	{
		super.Clear() ;
		what = null ;
		by = null ;
		result = null ;
		remainder = null ;
	}
	
	public void SetDivide(CDataEntity what, CDataEntity by, CDataEntity result, boolean isRounded)
	{
		this.what = what ;
		this.by = by ;
		isisRounded = isRounded ;
		this.result = result ;
	}
	public void SetDivide(CDataEntity what, CDataEntity by, boolean isRounded)
	{
		this.what = what ;
		this.by = by ;
		isisRounded = isRounded ;
		this.result = what ;
	}
	public void SetRemainder(CDataEntity rem)
	{
		remainder = rem ;
	}

	public CDataEntity getDividend()
	{
		return what;
	}

	public CDataEntity getDivisor()
	{
		return by;
	}

	public CDataEntity getResult()
	{
		return result;
	}

	public CDataEntity getRemainder()
	{
		return remainder;
	}

	public boolean getRounded()
	{
		return isisRounded;
	}

	public boolean ignore()
	{
		boolean ignore = what.ignore() ;
		ignore |= by.ignore() ;
		ignore |= result.ignore() ;
		if (remainder != null)
		{
			ignore |= remainder.ignore() ;
		}
		return ignore ;
	}

	/**
	 * The rendered reference text of the dividend ({@code what}) operand.
	 *
	 * <p>Target-neutral state consumed by the independent FPac lowering of this verb (the
	 * {@code recursiveFPacDivideEntity} template, FPAC_REFERENCE role), which writes
	 * {@code divide(<dividend>, <divisor>).to(<result>) ;}. The retired FPac direct backend
	 * rendered the dividend with the legacy reference-rendering bridge — a call that prefers an
	 * operand's own reference protocol and otherwise falls back to the recursive assembler — so
	 * that exact bridge is injected from the FPac factory as {@link #referenceRenderer} and applied
	 * here. Because the still-legacy FPac operand types (positioned substring/conversion buffers,
	 * numeric, string and undefined references) each carry their own reference protocol, routing
	 * them through the shared assembler reference walk instead would silently re-route them to the
	 * COBOL templates and diverge (or emit invalid Java); reproducing the injected bridge's result
	 * keeps the emitted output byte-for-byte identical to the retired backend. The template reads
	 * only these strings, so the semantic tree names no backend type and code generation stays a
	 * pure consumer of entity state. A hand-built verb (no factory) keeps the neutral fallback
	 * below, mirroring the bridge's null handling so it never fails. The frozen COBOL lowering
	 * ({@code recursiveDivideEntity}) keeps reading {@link #getDividend()} through the shared
	 * reference walk and is untouched by these FPac-only string getters.
	 */
	public String getDividendReference()
	{
		return referenceRenderer.apply(what, getLine());
	}

	/** The rendered reference text of the divisor ({@code by}) operand; see {@link #getDividendReference()}. */
	public String getDivisorReference()
	{
		return referenceRenderer.apply(by, getLine());
	}

	/** The rendered reference text of the quotient destination ({@code result}) operand; see {@link #getDividendReference()}. */
	public String getResultReference()
	{
		return referenceRenderer.apply(result, getLine());
	}

	/**
	 * Injected reference renderer standing in for the legacy reference-rendering bridge the
	 * retired FPac divide backend invoked per operand. Installed by the FPac factory; defaults to
	 * the operand's raw name ({@code [UNDEFINED]} when unset), mirroring the bridge's null handling
	 * so a directly-constructed verb stays well-formed. A pure injected value: no backend coupling
	 * lives in this tree.
	 */
	private BiFunction<CDataEntity, Integer, String> referenceRenderer =
		(ref, line) -> ref == null ? "[UNDEFINED]" : ref.GetName();

	public void setReferenceRenderer(BiFunction<CDataEntity, Integer, String> renderer)
	{
		if (renderer != null)
		{
			referenceRenderer = renderer;
		}
	}
}
