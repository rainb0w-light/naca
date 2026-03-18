/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.expressions;

import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondIsKindOf;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCondIsKindOf extends CEntityCondIsKindOf
{

	public int GetPriorityLevel()
	{
		return 7;
	}
	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaCondIsKindOf not = new CJavaCondIsKindOf() ;
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
	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String)
	 */

}
