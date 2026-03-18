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

import parser.expression.CProdExpression;
import semantic.expression.CEntityExprProd;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaExprProd extends CEntityExprProd
{

	public String Export()
	{
		String cs ;
		if (type == CProdExpression.CProdType.PROD)
		{
			cs = "multiply(" ;
		}
		else if (type == CProdExpression.CProdType.DIVIDE)
		{
			cs = "divide(" ;
		}
		else
		{
			cs = "pow(" ;
		}
		cs += op1.Export() + ", \n" + op2.Export() + ")" ;
		return cs ;
	}

}
