/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import semantic.expression.CEntityExprSum;

public class CFPacExprSum extends CEntityExprSum
{

	public CFPacExprSum()
	{
		super();
	}

	public String Export()
	{
		return generate.LegacyDataRenderer.renderReference(op1, getLine()) + "+" + generate.LegacyDataRenderer.renderReference(op2, getLine()) ;
	}

}
