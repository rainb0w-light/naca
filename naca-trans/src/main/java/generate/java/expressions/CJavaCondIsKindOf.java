/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 ao�t 2004
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
	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String)
	 */

}
