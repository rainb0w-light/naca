/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.expression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprOpposite;

/**
 * @author sly
 *
 */
public class COppositeExpression extends CExpression
{
    protected CExpression expression = null ;

    public COppositeExpression(int line, CExpression term)
    {
        super(line) ;
        expression = term ;
    }
    public boolean IsReference()
    {
        return false;
    }

    public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
    {
        CEntityExprOpposite eOpp = factory.NewEntityExprOpposite();
        CBaseEntityExpression op1 = expression.AnalyseExpression(factory) ;
        eOpp.SetOpposite(op1) ;
        return eOpp;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#AnalyseCondition(semantic.CBaseEntityFactory)
     */
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
    {
        return null;
    }

    protected boolean CheckMembersBeforeExport()
    {
        return CheckMemberNotNull(expression);
    }

    /* (non-Javadoc)
     * @see parser.expression.CExpression#DoExport(org.w3c.dom.Document)
     */
    public Element DoExport(Document root)
    {
        Element eop = root.createElement("Opposite");
        eop.appendChild(expression.Export(root));
        return eop ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
     */
    public CExpression GetSimilarExpression(CExpression operand)
    {
        ASSERT() ;
        return null;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#IsBinaryCondition()
     */
    public boolean IsBinaryCondition()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetFirstOperand()
     */
    public CExpression GetFirstConditionOperand()
    {
        return this;
    }
    public String toString()
    {
        return "MINUS("+expression.toString()+")" ;
    }
    public CExpression getMasterBinaryCondition()
    {
        return null ;
    }
    @Override
    public CExpression GetFirstCalculOperand()
    {
        return this ;
    }

}
