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
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondCompare;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCondCompare extends CEntityCondCompare
{
	public int GetPriorityLevel()
	{
		return 7;
	}
	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaCondCompare newCond = new CJavaCondCompare();
		newCond.bIsGreater = !bIsGreater ;
		newCond.bIsOrEquals = !bIsOrEquals ;
		newCond.op1 = op1 ;
		newCond.op2 = op2 ;
		return newCond;
	}
	public String Export()
	{
		String cs = "" ;
		String ebcdic = "" ;
//		if (op1.getExpressionType() != CBaseEntityExpression.CEntityExpressionType.MATH 
//			&& op1.getExpressionType() != CBaseEntityExpression.CEntityExpressionType.NUMERIC
//			&& op2.getExpressionType() != CBaseEntityExpression.CEntityExpressionType.MATH 
//			&& op2.getExpressionType() != CBaseEntityExpression.CEntityExpressionType.NUMERIC)
//		{
//			ebcdic = "InEbcdic" ;
//		}
//		else
//		{
//			ebcdic = "" ;
//		}
		if (bIsGreater && bIsOrEquals)
		{
			cs = "isGreaterOrEqual"+ebcdic+"(" ;
		}
		else if (bIsGreater && !bIsOrEquals)
		{
			cs = "isGreater"+ebcdic+"(" ;
		}
		else if (!bIsGreater && bIsOrEquals)
		{
			cs = "isLessOrEqual"+ebcdic+"("; 
		}
		else if (!bIsGreater && !bIsOrEquals)
		{
			cs = "isLess"+ebcdic+"(" ;
		}
		cs += op1.Export() + ", " + op2.Export() + ")";
		return cs ;
	}

}
