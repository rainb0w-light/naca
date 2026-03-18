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

import parser.expression.CSumExpression;
import semantic.expression.CEntityExprSum;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaExprSum extends CEntityExprSum
{

	public String Export()
	{
		String cs ;
		if (type == CSumExpression.CSumType.ADD)
		{
			cs = "add(" ;
		}
		else
		{
			cs = "subtract(" ;
		}
		cs += op1.Export() + ", \n" + op2.Export() + ")" ;
		return cs ;
	}

}
