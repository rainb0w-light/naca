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
package semantic.expression;

import parser.expression.CSumExpression;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityExprSum extends CBaseEntityExpression
{
	public void SetSumExpression(CBaseEntityExpression Op1, CBaseEntityExpression Op2, CSumExpression.CSumType Type)
	{
		op1 = Op1 ;
		op2 = Op2 ;
		type = Type ;
	} 
	protected CSumExpression.CSumType type = null ;
	protected CBaseEntityExpression op1 = null ;
	protected CBaseEntityExpression op2 = null ;
	public void Clear()
	{
		super.Clear() ;
		op1.Clear() ;
		op1 = null ;
		op2.Clear() ;
		op2 = null ;
	}
	public boolean ignore()
	{
		return op1.ignore() || op2.ignore();
	}

	@Override
	public CEntityExpressionType getExpressionType()
	{
		return CEntityExpressionType.MATH;
	}


}
