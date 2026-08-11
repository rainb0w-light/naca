/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import parser.expression.CProdExpression;

/**
 * @author sly
 *
 */
public class CEntityExprProd extends CBaseEntityExpression
{
	public void SetProdExpression(CBaseEntityExpression Op1, CBaseEntityExpression Op2, CProdExpression.CProdType Type)
	{
		op1 = Op1 ;
		op2 = Op2 ;
		type = Type ;
	}
	protected CProdExpression.CProdType type = null ;
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
		return op1.ignore() || op2.ignore() ;
	}

	@Override
	public CEntityExpressionType getExpressionType()
	{
		return CEntityExpressionType.MATH;
	}

	public CBaseEntityExpression getLeft()
	{
		return op1;
	}

	public CBaseEntityExpression getRight()
	{
		return op2;
	}

	public boolean isMultiply()
	{
		return type != null && "MULT".equals(type.Text);
	}

	public boolean isDivide()
	{
		return type != null && "DIVID".equals(type.Text);
	}

}
