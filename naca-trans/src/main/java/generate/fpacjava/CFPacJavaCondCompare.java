/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondCompare;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaCondCompare.java,v 1.1 2006/03/07 15:31:58 U930CV Exp $
 */
public class CFPacJavaCondCompare extends CEntityCondCompare
{


	public int GetPriorityLevel()
	{
		return 7;
	}
	public CBaseEntityCondition GetOppositeCondition()
	{
		CFPacJavaCondCompare newCond = new CFPacJavaCondCompare();
		newCond.isisGreater = !isisGreater;
		newCond.isisOrEquals = !isisOrEquals;
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
		if (isisGreater && isisOrEquals)
		{
			cs = "isGreaterOrEqual"+ebcdic+"(" ;
		}
		else if (isisGreater && !isisOrEquals)
		{
			cs = "isGreater"+ebcdic+"(" ;
		}
		else if (!isisGreater && isisOrEquals)
		{
			cs = "isLessOrEqual"+ebcdic+"("; 
		}
		else if (!isisGreater && !isisOrEquals)
		{
			cs = "isLess"+ebcdic+"(" ;
		}
		cs += op1.Export() + ", " + op2.Export() + ")";
		return cs ;
	}

}
