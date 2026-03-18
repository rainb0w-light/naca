/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondIsKindOf;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaCondIsKindOf.java,v 1.2 2007/06/28 06:19:46 u930bm Exp $
 */
public class CFPacJavaCondIsKindOf extends CEntityCondIsKindOf
{

	public int GetPriorityLevel()
	{
		return 7;
	}
	public CBaseEntityCondition GetOppositeCondition()
	{
		CFPacJavaCondIsKindOf not = new CFPacJavaCondIsKindOf() ;
		not.bIsAlphabetic = bIsAlphabetic ;
		not.bIsLower = bIsLower ;
		not.bIsNumeric = bIsNumeric ;
		not.bIsUpper = bIsUpper ;
		not.bOpposite = ! bOpposite ;
		not.reference = reference ;
		return not;
	}
	public String Export()
	{
		String cs = "is" ;
		if (bOpposite)
		{
			cs += "Not" ;
		}
		if (bIsNumeric)
		{
			cs += "Numeric(";
		}
		else if (bIsAlphabetic)
		{
			cs += "Alphabetic(";
		}
		if (reference != null)
		{
			cs += reference.ExportReference(getLine());
		}
		else
		{
			cs += "[UNDEFINED]" ;
		}
		cs += ")";
		return cs ;
	}

}
