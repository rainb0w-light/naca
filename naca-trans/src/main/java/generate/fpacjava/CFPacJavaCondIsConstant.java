/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondIsConstant;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaCondIsConstant.java,v 1.2 2007/06/28 06:19:46 u930bm Exp $
 */
public class CFPacJavaCondIsConstant extends CEntityCondIsConstant
{


	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetPriorityLevel()
	 */
	public int GetPriorityLevel()
	{
		return 7;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetOppositeCondition()
	 */
	public CBaseEntityCondition GetOppositeCondition()
	{
		CFPacJavaCondIsConstant not = new CFPacJavaCondIsConstant() ;
		not.bIsLowValue = bIsLowValue ;
		not.bIsHighValue = bIsHighValue ;
		not.bIsOpposite = ! bIsOpposite ;
		not.bIsSpace = bIsSpace ;
		not.bIsZero = bIsZero ;
		not.reference = reference ;
		reference.RegisterVarTesting(not);
		return not;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#ExportTo(semantic.CBaseLanguageExporter)
	 */
	public String Export()
	{
		String cs = "is" ;
		if (bIsOpposite)
		{
			cs += "Not" ;
		}
		if (bIsZero)
		{
			cs += "Zero(";
		}
		else if (bIsSpace)
		{
			cs += "Space(";
		}
		else if (bIsLowValue)
		{
			cs += "LowValue(";
		}
		else if (bIsHighValue)
		{
			cs += "HighValue(";
		}
		cs += reference.ExportReference(getLine()) + ")";
		return cs ;

	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String)
	 */

}
