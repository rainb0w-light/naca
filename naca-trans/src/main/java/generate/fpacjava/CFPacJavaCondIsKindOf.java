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
		not.isisAlphabetic = isisAlphabetic;
		not.isisLower = isisLower;
		not.isisNumeric = isisNumeric;
		not.isisUpper = isisUpper;
		not.isopposite = !isopposite;
		not.reference = reference ;
		return not;
	}
	public String Export()
	{
		String cs = "is" ;
		if (isopposite)
		{
			cs += "Not" ;
		}
		if (isisNumeric)
		{
			cs += "Numeric(";
		}
		else if (isisAlphabetic)
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
