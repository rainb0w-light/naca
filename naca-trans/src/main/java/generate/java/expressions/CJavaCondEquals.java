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
import semantic.expression.CEntityCondEquals;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCondEquals extends CEntityCondEquals
{

	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaCondEquals newCond = new CJavaCondEquals();
		if (bIsDifferent)
		{
			newCond.SetEqualCondition(op1, op2) ;
		}
		else
		{
			newCond.SetDifferentCondition(op1, op2) ;
		}
		if (op1.GetSingleOperator() != null)
		{
			op1.GetSingleOperator().RegisterVarTesting(newCond);
		}
		if (op2.GetSingleOperator() != null)
		{
			op2.GetSingleOperator().RegisterValueAccess(newCond);
		}
		return newCond ;
	}

	public int GetPriorityLevel()
	{
		return 7;
	}
	public String Export()
	{
		String cs = "" ;
		if (bIsDifferent)
		{
			cs = "isDifferent(";
		}
		else
		{
			cs = "isEqual(";
		}
		cs += op1.Export() + ", " ;
		if (op2 != null)
		{
			cs += op2.Export();
		}
		else
		{
			cs += "[UNDEFINED]" ;
		}
		cs += ")" ;
		return cs ;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String)
	 */
}
