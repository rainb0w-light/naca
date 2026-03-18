/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondIsBoolean;

public class CFPacJavaCondIsBoolean extends CEntityCondIsBoolean
{
	public int GetPriorityLevel()
	{
		return 7;
	}

	@Override
	public CBaseEntityCondition GetOppositeCondition()
	{
		CFPacJavaCondIsBoolean cond = new CFPacJavaCondIsBoolean() ;
		cond.reference = reference ;
		cond.bIsTrue = !bIsTrue;
		return cond;
	}

	@Override
	public String Export()
	{
		String cs = "" ;
		if (!bIsTrue)
		{
			cs += "!" ;
		}
		cs += reference.ExportReference(getLine()) ;
		return cs ;
	}

}
