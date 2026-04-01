/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.expressions;

import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondIsConstant;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCondIsConstant extends CEntityCondIsConstant
{
//	public CJavaCondIsConstant(int nLine)
//	{
//		super(nLine);
//	}
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
		CJavaCondIsConstant not = new CJavaCondIsConstant() ;
		not.isisLowValue = isisLowValue;
		not.isisHighValue = isisHighValue;
		not.bIsOpposite = ! bIsOpposite ;
		not.isisSpace = isisSpace;
		not.isisZero = isisZero;
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
		if (isisZero)
		{
			cs += "Zero(";
		}
		else if (isisSpace)
		{
			cs += "Space(";
		}
		else if (isisLowValue)
		{
			cs += "LowValue(";
		}
		else if (isisHighValue)
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
