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
    /** Sets the prod expression. */
    public void SetProdExpression(CBaseEntityExpression newOp1, CBaseEntityExpression newOp2, CProdExpression.CProdType newType)
    {
        op1 = newOp1 ;
        op2 = newOp2 ;
        type = newType ;
    }
    protected CProdExpression.CProdType type = null ;
    protected CBaseEntityExpression op1 = null ;
    protected CBaseEntityExpression op2 = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        op1.Clear() ;
        op1 = null ;
        op2.Clear() ;
        op2 = null ;
    }
    /** Executes the ignore operation. */
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
        return type != null && "MULT".equals(type.getText());
    }

    public boolean isDivide()
    {
        return type != null && "DIVID".equals(type.getText());
    }

}
