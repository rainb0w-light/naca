/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;




/**
 * @author U930CV
 *
 */
public class CEntityExprOpposite extends CBaseEntityExpression
{

    /** Sets the opposite. */
    public void SetOpposite(CBaseEntityExpression e)
    {
        data = e ;
    }
    protected CBaseEntityExpression data = null ;

    public CBaseEntityExpression getOperand()
    {
        return data;
    }

    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        data.Clear() ;
        data = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return data.ignore();
    }

    @Override
    public CEntityExpressionType getExpressionType()
    {
        return CEntityExpressionType.MATH;
    }


}
