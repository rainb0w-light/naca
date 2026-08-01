/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseDataReference;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Target-neutral FPac conversion reference.
 *
 * <p>FPac wraps a data reference whose bytes must be reinterpreted as packed
 * decimal ({@code P}) or alphanumeric ({@code X}) — e.g. the operand of a
 * {@code CONVERT} or a positioned {@code MOVE}. This is the pure semantic entity
 * the FPac factory hands back; it carries the conversion mode and the wrapped
 * {@link #reference} and exposes read-only getters the recursive template
 * {@code recursiveFPacConvertReferenceEntity} consumes. It replaced the retired
 * FPac direct backend, whose reference-rendering output is reproduced exactly by
 * that template's three shapes:
 * <ul>
 *   <li>no conversion: the wrapped reference alone;</li>
 *   <li>conversion of a reference that {@link CDataEntity#HasAccessors() has
 *       accessors}: {@code <reference>P} / {@code <reference>X};</li>
 *   <li>conversion of a reference without accessors: the call head
 *       {@code bufferP(<reference>} / {@code bufferX(<reference>}.</li>
 * </ul>
 * The third shape is deliberately an UNCLOSED call head: in production this entity
 * is always wrapped by a {@code CSubStringAttributReference}, whose renderer detects
 * the {@code "("} and appends {@code ", start, length)"}, completing a real
 * {@code FPacProgram.bufferP/bufferX(FPacFileDescriptor, int, int)} call. That
 * composition protocol is preserved verbatim, so the fragment must keep its opening
 * paren and must not be "fixed" in isolation.
 *
 * @author S. Charton
 * @version $Id: CEntityConvertReference.java,v 1.2 2006/09/28 09:10:06 u930di Exp $
 */
public class CEntityConvertReference extends CBaseDataReference
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityConvertReference(CObjectCatalog cat)
	{
		super(0, "", cat);
	}

	/**
	 * The conversion reference itself always carries accessors: this is what makes
	 * the wrapping {@code CSubStringAttributReference} take its accessor branch and
	 * complete the {@code bufferP/bufferX} call head this entity emits. Mirrors the
	 * retired backend's {@code HasAccessors()} override.
	 */
	@Override
	public boolean HasAccessors()
	{
		return true ;
	}

	/**
	 * True when no conversion mode was set — the entity renders as the wrapped
	 * reference alone. Pure state read; populated by {@link #convertToPacked}/
	 * {@link #convertToAlphaNum}.
	 */
	public boolean isPlainReference()
	{
		return !isconvertToPacked && !isconvertToAlphaNum ;
	}

	/**
	 * True when a conversion mode is set and the wrapped reference has accessors, so
	 * the template renders {@code <reference>P}/{@code <reference>X}. Pure state read
	 * of the conversion mode plus the semantic {@link CDataEntity#HasAccessors()}
	 * classification of the wrapped reference (computed during semantic analysis, not
	 * a lowering step).
	 */
	public boolean isAccessorConversion()
	{
		return (isconvertToPacked || isconvertToAlphaNum)
			&& reference != null
			&& reference.HasAccessors() ;
	}

	/**
	 * The one-character conversion suffix: {@code "P"} for packed, {@code "X"} for
	 * alphanumeric. Only meaningful when a conversion mode is set.
	 */
	public String getConversionSuffix()
	{
		return isconvertToPacked ? "P" : "X" ;
	}

	/**
	 * @see semantic.CDataEntity#GetDataType()
	 */
	@Override
	public CDataEntityType GetDataType()
	{
		return reference.GetDataType() ;
	}
	

	/**
	 * @see semantic.CDataEntity#isValNeeded()
	 */
	@Override
	public boolean isValNeeded()
	{
		return false;
	}

	/**
	 * @see semantic.CDataEntity#GetConstantValue()
	 */
	@Override
	public String GetConstantValue()
	{
		return null;
	}

	/**
	 * @param buffer
	 */
	public void convertToPacked(CDataEntity buffer)
	{
		isconvertToPacked = true ;
		isconvertToAlphaNum = false ;
		reference = buffer ;
	}	
	protected boolean isconvertToPacked = false ;
	protected boolean isconvertToAlphaNum = false ;
	
	public void convertToAlphaNum(CDataEntity working) {
		isconvertToAlphaNum = true ;
		isconvertToPacked = false ;
		reference = working ;
	}
}
