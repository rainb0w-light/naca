/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import semantic.CEntityFileDescriptor;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondNot;
import semantic.expression.CEntityIsFileEOF;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaIsFileEOF.java,v 1.2 2007/06/28 06:19:46 u930bm Exp $
 */
public class CFPacJavaIsFileEOF extends CEntityIsFileEOF
{

	/**
	 * @param fb 
	 * 
	 */
	public CFPacJavaIsFileEOF(CEntityFileDescriptor fb)
	{
		super(fb);
	}

	/**
	 * @see semantic.expression.CBaseEntityCondition#GetPriorityLevel()
	 */
	@Override
	public int GetPriorityLevel()
	{
		return 7;
	}

	/**
	 * @see semantic.expression.CBaseEntityCondition#GetOppositeCondition()
	 *
	 * <p>FPac negates an EOF test by wrapping it in a pure target-neutral
	 * {@link semantic.expression.CEntityCondNot} (there is no {@code isNotEof} runtime call).
	 * The wrapping node renders through the shared {@code recursiveCondNotEntity} binding
	 * ({@code !(<entity.operand>)}); the EOF operand itself resolves through the
	 * {@code semantic.expression.CEntityIsFileEOF -> recursiveIsFileEOFEntity} runtime
	 * superclass alias, so the retired {@code CFPacJavaCondNot} direct backend is no longer
	 * needed. The emitted {@code !(isEof(<name>))} is the canonical recursive-assembler shape
	 * (semantically identical to the legacy {@code !isEof(<name>)}).
	 */
	@Override
	public CBaseEntityCondition GetOppositeCondition()
	{
		CEntityCondNot not = new CEntityCondNot();
		not.SetCondition(this) ;
		return not;
	}

	/**
	 * @see semantic.expression.CBaseEntityCondExpr#Export()
	 */
	public String Export()
	{
		return "isEof("+fileDescriptor.getFormattedName()+")" ;
	}

}
