/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;

/**
 * @author S. Charton
 * @version $Id: CEntityIsFileEOF.java,v 1.2 2006/03/14 20:46:36 U930CV Exp $
 */
public class CEntityIsFileEOF extends CBaseEntityCondition
{
	protected CEntityFileDescriptor fileDescriptor ;
	/**
	 * @param fb
	 *
	 */
	public CEntityIsFileEOF(CEntityFileDescriptor fb)
	{
		super();
		fileDescriptor = fb ;
	}

	@Override
	public int GetPriorityLevel()
	{
		return 7 ;
	}

	@Override
	public CBaseEntityCondition GetOppositeCondition()
	{
		CEntityCondNot not = new CEntityCondNot() ;
		not.SetCondition(this) ;
		return not ;
	}


	/**
	 * @see semantic.expression.CBaseEntityCondition#isBinaryCondition()
	 */
	@Override
	public boolean isBinaryCondition()
	{
		return false;
	}

	/**
     * @see semantic.expression.CBaseEntityCondition#GetSpecialConditionReplacing(java.lang.String, semantic.CBaseEntityFactory,
     * semantic.CDataEntity)
	 */
	@Override
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return null;
	}

	@Override
	public boolean ignore()
	{
		return fileDescriptor.ignore() ;
	}

	/**
	 * Pure target-neutral getter consumed by the recursive ST4 reference binding
	 * ({@code semantic.expression.CEntityIsFileEOF -> recursiveIsFileEOFEntity}, a runtime
	 * superclass alias in {@code semantic-runtime-bindings.properties}). Returns the wrapped
	 * file descriptor object; the template renders it through the shared
	 * {@code recursiveFileDescriptorEntity} binding ({@code <entity.formattedName>}), exactly
	 * as {@code recursiveCloseFileEntity} does for {@code semantic.Verbs.CEntityCloseFile}.
	 * This is a plain field read — no {@code FormatIdentifier}, export, reference resolution
	 * or child building — so it honors the semantic/generation separation. The binding exists
	 * so the EOF condition is renderable as the OPERAND of a wrapping pure
	 * {@code CEntityCondNot} (FPac negates EOF via {@code GetOppositeCondition}; there is no
	 * {@code isNotEof} runtime call), whose {@code recursiveCondNotEntity} template renders
	 * {@code !(<entity.operand>)} and must therefore be able to render this operand.
	 */
	public CEntityFileDescriptor getFileDescriptor()
	{
		return fileDescriptor ;
	}
	/**
	 * @see semantic.expression.CBaseEntityCondition#GetConditionReference()
	 */
	@Override
	public CDataEntity GetConditionReference()
	{
		return null;
	}
	public void SetConditonReference(CDataEntity e)
	{
		ASSERT(null) ;
	}

}
